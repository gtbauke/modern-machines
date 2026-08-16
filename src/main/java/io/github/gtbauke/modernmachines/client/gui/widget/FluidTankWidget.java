package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public class FluidTankWidget extends UiWidget {
    private final Supplier<Component> fluidNameSupplier;
    private final IntSupplier amountSupplier;
    private final IntSupplier capacitySupplier;
    private final int fluidColor;

    public FluidTankWidget(Supplier<Component> fluidNameSupplier, IntSupplier amountSupplier, IntSupplier capacitySupplier, int fluidColor) {
        this.fluidNameSupplier = fluidNameSupplier;
        this.amountSupplier = amountSupplier;
        this.capacitySupplier = capacitySupplier;
        this.fluidColor = fluidColor;
        flexNode.setSize(18, 52);
    }

    public int getAmount() {
        return amountSupplier.getAsInt();
    }

    public int getCapacity() {
        return capacitySupplier.getAsInt();
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        int amount = getAmount();
        int cap = getCapacity();
        Component name = fluidNameSupplier.get();
        if (amount <= 0 || name == null) {
            return List.of(
                    Component.literal("Fluid Tank").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD),
                    Component.literal("Empty (" + cap + " mB)").withStyle(ChatFormatting.GRAY)
            );
        }

        return List.of(
                name.copy().withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD),
                Component.literal(String.format("%,d / %,d mB", amount, cap)).withStyle(ChatFormatting.AQUA)
        );
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        Bounds b = getBounds();
        int amount = getAmount();
        int cap = getCapacity();

        // 1. Draw tank frame
        NineSliceRenderer.drawNineSlice(graphics, NineSliceRenderer.SLOT, b);

        // 2. Draw fluid fill
        if (cap > 0 && amount > 0) {
            int fillH = Math.min(b.height() - 2, Math.max(1, (int) ((long) amount * (b.height() - 2) / cap)));
            int fillY = b.bottom() - 1 - fillH;
            graphics.fill(b.x() + 1, fillY, b.right() - 1, b.bottom() - 1, fluidColor);
        }

        // 3. Fluid drop icon
        graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, b.x() + 1, b.y() + 18, 16.0F, 96.0F, 16, 16, 256, 256);
    }
}
