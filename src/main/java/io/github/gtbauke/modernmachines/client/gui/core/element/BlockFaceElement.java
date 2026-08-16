package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.api.machine.side.RelativeSide;
import io.github.gtbauke.modernmachines.api.machine.side.SideIoMode;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BlockFaceElement extends UIElement {
    public static final int FACE_SIZE = 22;

    private final RelativeSide side;
    private final Supplier<SideIoMode> modeSupplier;
    private final BiConsumer<RelativeSide, SideIoMode> onModeChanged;
    private final String label;

    public BlockFaceElement(RelativeSide side, Supplier<SideIoMode> modeSupplier, BiConsumer<RelativeSide, SideIoMode> onModeChanged) {
        super(new Bounds(Position.ZERO, new Size(FACE_SIZE, FACE_SIZE)));
        this.side = side;
        this.modeSupplier = modeSupplier != null ? modeSupplier : () -> SideIoMode.NONE;
        this.onModeChanged = onModeChanged;
        this.label = getShortLabel(side);
    }

    private static String getShortLabel(RelativeSide side) {
        return switch (side) {
            case TOP -> "T";
            case BOTTOM -> "B";
            case FRONT -> "F";
            case BACK -> "Bk";
            case LEFT -> "L";
            case RIGHT -> "R";
        };
    }

    public RelativeSide getSide() {
        return side;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Position clickPos = new Position((int) mouseX, (int) mouseY);
        if (getAbsoluteBounds().contains(clickPos)) {
            SideIoMode current = modeSupplier.get();
            SideIoMode next = (button == 1) ? current.previous() : current.next();
            if (onModeChanged != null) {
                onModeChanged.accept(side, next);
            }
            markDirty();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        SideIoMode mode = modeSupplier.get();
        if (mode == null) mode = SideIoMode.NONE;

        int fillColor = (0xFF << 24) | (mode.getColorRgb() & 0x00FFFFFF);

        // 1. Draw face body fill
        GUIRenderHelper.drawRect(graphics, absoluteBounds, fillColor);

        // 2. Outer dark border outline
        GUIRenderHelper.drawRectOutline(graphics, absoluteBounds, 0xFF222428);

        // 3. Subtle 3D bevel highlight/shadow
        GUIRenderHelper.drawBevel(graphics, absoluteBounds, 0x40FFFFFF, 0x40000000);

        // 4. Centered face side abbreviation
        Font font = Minecraft.getInstance().font;
        int centerX = absoluteBounds.position().x() + absoluteBounds.size().width() / 2;
        int centerY = absoluteBounds.position().y() + (absoluteBounds.size().height() - 8) / 2;
        GUIRenderHelper.drawCenteredString(graphics, font, Component.literal(label), new Position(centerX, centerY), 0xFFFFFFFF, true);

        // 5. Tooltip on hover
        if (absoluteBounds.contains(new Position(mouseX, mouseY))) {
            Component tooltip = Component.literal(side.name() + ": ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(mode.name()).withStyle(mode.getFormatting(), ChatFormatting.BOLD))
                    .append(Component.literal(" (Click to toggle)").withStyle(ChatFormatting.DARK_GRAY));
            GUIRenderHelper.drawTooltip(graphics, font, List.of(tooltip), new Position(mouseX, mouseY));
        }
    }
}
