package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.List;
import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.api.machine.capability.MachineCapabilityType;
import io.github.gtbauke.modernmachines.api.machine.side.ISideConfigurable;
import io.github.gtbauke.modernmachines.api.machine.side.RelativeSide;
import io.github.gtbauke.modernmachines.api.machine.side.SideIoMode;
import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.network.ServerboundSideConfigPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class FaceButtonWidget extends UiWidget {
    private final ISideConfigurable machine;
    private final RelativeSide relativeSide;
    private final Supplier<MachineCapabilityType> activeCapSupplier;
    private final int size;

    public FaceButtonWidget(ISideConfigurable machine, RelativeSide relativeSide, Supplier<MachineCapabilityType> activeCapSupplier, int size) {
        this.machine = machine;
        this.relativeSide = relativeSide;
        this.activeCapSupplier = activeCapSupplier;
        this.size = size;
        getFlexNode().setSize(size, size);
    }

    public RelativeSide getRelativeSide() {
        return relativeSide;
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        if (!isHovered()) return List.of();

        MachineCapabilityType cap = activeCapSupplier.get();
        SideIoMode mode = machine.getSideConfig().getMode(cap, relativeSide);
        Direction absDir = relativeSide.toAbsolute(machine.getMachineFacing());

        return List.of(
                Component.literal(relativeSide.name() + " Face (" + absDir.getName().toUpperCase() + ")")
                        .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
                Component.literal("Mode: ").withStyle(ChatFormatting.GRAY)
                        .append(mode.getDisplayName()),
                Component.literal("• Left-Click: Next Mode").withStyle(ChatFormatting.DARK_GRAY),
                Component.literal("• Right-Click: Previous Mode").withStyle(ChatFormatting.DARK_GRAY)
        );
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        Bounds b = getBounds();

        MachineCapabilityType cap = activeCapSupplier.get();
        SideIoMode mode = machine.getSideConfig().getMode(cap, relativeSide);

        // 1. Draw button base (SLOT 9-slice)
        NineSliceRenderer.drawNineSlice(graphics, NineSliceRenderer.SLOT, b);

        // 2. Colored overlay representing the mode
        int color = mode.getColorRgb();
        int alpha = isHovered() ? 0xDD : 0xAA;
        int argb = (alpha << 24) | (color & 0x00FFFFFF);

        graphics.fill(b.x() + 2, b.y() + 2, b.x() + b.width() - 2, b.y() + b.height() - 2, argb);

        // 3. Mode abbreviation label (I, O, I/O, -)
        String label = mode.getLabel();
        int textWidth = font.width(label);
        int textX = b.x() + (b.width() - textWidth) / 2;
        int textY = b.y() + (b.height() - 8) / 2;

        graphics.text(font, Component.literal(label), textX, textY, 0xFFFFFFFF, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;
        Bounds b = getBounds();

        if (b.contains(mouseX, mouseY)) {
            boolean forward = (button == 0); // 0 = Left click, 1 = Right click
            MachineCapabilityType cap = activeCapSupplier.get();

            SideIoMode next = machine.getSideConfig().cycleMode(cap, relativeSide, forward);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            // Sync to server immediately
            ClientPacketDistributor.sendToServer(ServerboundSideConfigPayload.setSide(
                    machine.getMachinePos(),
                    cap,
                    relativeSide,
                    next
            ));
            return true;
        }
        return false;
    }
}
