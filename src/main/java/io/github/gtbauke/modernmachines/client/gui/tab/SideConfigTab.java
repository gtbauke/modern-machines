package io.github.gtbauke.modernmachines.client.gui.tab;

import java.util.Set;

import io.github.gtbauke.modernmachines.api.machine.capability.MachineCapabilityType;
import io.github.gtbauke.modernmachines.api.machine.side.ISideConfigurable;
import io.github.gtbauke.modernmachines.api.machine.side.RelativeSide;
import io.github.gtbauke.modernmachines.client.gui.declarative.Column;
import io.github.gtbauke.modernmachines.client.gui.declarative.Divider;
import io.github.gtbauke.modernmachines.client.gui.declarative.Row;
import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.widget.ButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.FaceButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import io.github.gtbauke.modernmachines.client.gui.window.SideTabWidget;
import io.github.gtbauke.modernmachines.network.ServerboundSideConfigPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class SideConfigTab extends SideTabWidget {
    private final ISideConfigurable machine;
    private MachineCapabilityType activeCapability;

    private final ButtonWidget autoEjectBtn;
    private final ButtonWidget autoPullBtn;

    public SideConfigTab(ISideConfigurable machine) {
        super(Component.literal("Side Configuration"), 32, 96, false);
        this.machine = machine;

        Set<MachineCapabilityType> supported = machine.getSupportedCapabilities();
        this.activeCapability = supported.contains(MachineCapabilityType.ITEM) ? MachineCapabilityType.ITEM : supported.iterator().next();

        setExpandedSize(115, 160);

        getContentContainer().getFlexNode().setPadding(FlexInsets.of(4, 4, 4, 4));
        getContentContainer().getFlexNode().setAlignItems(AlignItems.CENTER);
        getContentContainer().getFlexNode().setGap(3);

        // 1. Header
        LabelWidget title = new LabelWidget(Component.literal("Side Config").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        title.setCentered(true);
        getContentContainer().addChild(title);

        // 2. Capability Switcher (If multiple capabilities supported)
        if (supported.size() > 1) {
            Row capRow = Row.of().gap(2).center();
            for (MachineCapabilityType cap : supported) {
                ButtonWidget capBtn = new ButtonWidget(Component.literal(cap.getDisplayName()), btn -> {
                    this.activeCapability = cap;
                    updateToggleLabels();
                });
                capBtn.getFlexNode().setSize(32, 14);
                capRow.addChild(capBtn);
            }
            getContentContainer().addChild(capRow);
        } else {
            LabelWidget capLabel = new LabelWidget(activeCapability.getFormattedName());
            capLabel.setCentered(true);
            getContentContainer().addChild(capLabel);
        }

        // 3. Planified 2D Block Net (The 6 Faces Unfolded)
        FaceButtonWidget topFace = new FaceButtonWidget(machine, RelativeSide.TOP, () -> activeCapability, 18);
        FaceButtonWidget leftFace = new FaceButtonWidget(machine, RelativeSide.LEFT, () -> activeCapability, 18);
        FaceButtonWidget frontFace = new FaceButtonWidget(machine, RelativeSide.FRONT, () -> activeCapability, 18);
        FaceButtonWidget rightFace = new FaceButtonWidget(machine, RelativeSide.RIGHT, () -> activeCapability, 18);
        FaceButtonWidget backFace = new FaceButtonWidget(machine, RelativeSide.BACK, () -> activeCapability, 18);
        FaceButtonWidget bottomFace = new FaceButtonWidget(machine, RelativeSide.BOTTOM, () -> activeCapability, 18);

        UiWidget faceNet = Column.of(
                topFace,
                Row.of(leftFace, frontFace, rightFace, backFace).gap(2).center(),
                bottomFace
        ).gap(2).center();
        getContentContainer().addChild(faceNet);

        // 4. Divider
        getContentContainer().addChild(Divider.horizontal());

        // 5. Auto-Eject and Auto-Pull Controls
        autoEjectBtn = new ButtonWidget(Component.literal("Auto-Push"), btn -> {
            boolean newVal = machine.getSideConfig().toggleAutoEject(activeCapability);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            ClientPacketDistributor.sendToServer(ServerboundSideConfigPayload.setAutoEject(machine.getMachinePos(), activeCapability, newVal));
            updateToggleLabels();
        });
        autoEjectBtn.getFlexNode().setSize(98, 14);
        getContentContainer().addChild(autoEjectBtn);

        autoPullBtn = new ButtonWidget(Component.literal("Auto-Pull"), btn -> {
            boolean newVal = machine.getSideConfig().toggleAutoPull(activeCapability);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            ClientPacketDistributor.sendToServer(ServerboundSideConfigPayload.setAutoPull(machine.getMachinePos(), activeCapability, newVal));
            updateToggleLabels();
        });
        autoPullBtn.getFlexNode().setSize(98, 14);
        getContentContainer().addChild(autoPullBtn);

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
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (isExpanded()) {
            updateToggleLabels();
        }
        super.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
    }
}
