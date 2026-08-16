package io.github.gtbauke.modernmachines.client.gui.tab;

import java.util.Set;

import io.github.gtbauke.modernmachines.api.machine.capability.MachineCapabilityType;
import io.github.gtbauke.modernmachines.api.machine.side.ISideConfigurable;
import io.github.gtbauke.modernmachines.api.machine.side.RelativeSide;
import io.github.gtbauke.modernmachines.client.gui.declarative.Card;
import io.github.gtbauke.modernmachines.client.gui.declarative.Column;
import io.github.gtbauke.modernmachines.client.gui.declarative.Divider;
import io.github.gtbauke.modernmachines.client.gui.declarative.Row;
import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.ButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.FaceButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import io.github.gtbauke.modernmachines.network.ServerboundSideConfigPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class SideConfigContentWidget extends FlexContainer {
    private final ISideConfigurable machine;
    private MachineCapabilityType activeCapability;

    private final ButtonWidget autoEjectBtn;
    private final ButtonWidget autoPullBtn;

    public SideConfigContentWidget(ISideConfigurable machine) {
        super(FlexDirection.COLUMN);
        this.machine = machine;

        Set<MachineCapabilityType> supported = machine.getSupportedCapabilities();
        this.activeCapability = supported.contains(MachineCapabilityType.ITEM) ? MachineCapabilityType.ITEM : supported.iterator().next();

        getFlexNode().setPadding(FlexInsets.of(4, 6, 6, 6));
        getFlexNode().setAlignItems(AlignItems.CENTER);
        getFlexNode().setGap(4);

        // 1. Capability Selector Bar
        if (supported.size() > 1) {
            Row capRow = Row.of().gap(4).center();
            for (MachineCapabilityType cap : supported) {
                ButtonWidget capBtn = new ButtonWidget(Component.literal(cap.getDisplayName()), btn -> {
                    this.activeCapability = cap;
                    updateToggleLabels();
                });
                capBtn.getFlexNode().setSize(38, 16);
                capRow.addChild(capBtn);
            }
            addChild(capRow);
        } else {
            LabelWidget capLabel = LabelWidget.of(
                Component.literal("Configuring: ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(activeCapability.getFormattedName())
            ).centered();
            addChild(capLabel);
        }

        // 2. Planified 2D Net inside Card (20x20 face buttons)
        FaceButtonWidget topFace = new FaceButtonWidget(machine, RelativeSide.TOP, () -> activeCapability, 20);
        FaceButtonWidget leftFace = new FaceButtonWidget(machine, RelativeSide.LEFT, () -> activeCapability, 20);
        FaceButtonWidget frontFace = new FaceButtonWidget(machine, RelativeSide.FRONT, () -> activeCapability, 20);
        FaceButtonWidget rightFace = new FaceButtonWidget(machine, RelativeSide.RIGHT, () -> activeCapability, 20);
        FaceButtonWidget backFace = new FaceButtonWidget(machine, RelativeSide.BACK, () -> activeCapability, 20);
        FaceButtonWidget bottomFace = new FaceButtonWidget(machine, RelativeSide.BOTTOM, () -> activeCapability, 20);

        UiWidget faceNetCard = Card.of(
                Column.of(
                        topFace,
                        Row.of(leftFace, frontFace, rightFace, backFace).gap(3).center(),
                        bottomFace
                ).gap(3).center()
        ).padding(FlexInsets.of(6, 6, 6, 6));
        addChild(faceNetCard);

        // 3. Divider
        addChild(Divider.horizontal());

        // 4. Auto-Transfer Controls
        autoEjectBtn = new ButtonWidget(Component.literal("Auto-Push"), btn -> {
            boolean newVal = machine.getSideConfig().toggleAutoEject(activeCapability);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            ClientPacketDistributor.sendToServer(ServerboundSideConfigPayload.setAutoEject(machine.getMachinePos(), activeCapability, newVal));
            updateToggleLabels();
        });
        autoEjectBtn.getFlexNode().setSize(120, 16);
        addChild(autoEjectBtn);

        autoPullBtn = new ButtonWidget(Component.literal("Auto-Pull"), btn -> {
            boolean newVal = machine.getSideConfig().toggleAutoPull(activeCapability);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            ClientPacketDistributor.sendToServer(ServerboundSideConfigPayload.setAutoPull(machine.getMachinePos(), activeCapability, newVal));
            updateToggleLabels();
        });
        autoPullBtn.getFlexNode().setSize(120, 16);
        addChild(autoPullBtn);

        updateToggleLabels();
    }

    private void updateToggleLabels() {
        boolean eject = machine.getSideConfig().isAutoEject(activeCapability);
        boolean pull = machine.getSideConfig().isAutoPull(activeCapability);

        autoEjectBtn.setLabel(Component.literal("Auto-Push: ").append(
                eject ? Component.literal("ON").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                      : Component.literal("OFF").withStyle(ChatFormatting.RED)
        ));

        autoPullBtn.setLabel(Component.literal("Auto-Pull: ").append(
                pull ? Component.literal("ON").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                     : Component.literal("OFF").withStyle(ChatFormatting.RED)
        ));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        updateToggleLabels();
        super.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
    }
}
