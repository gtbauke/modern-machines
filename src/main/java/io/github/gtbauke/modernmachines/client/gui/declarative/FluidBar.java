package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class FluidBar extends UiWidget {
    private final Supplier<String> fluidNameSupplier;
    private final IntSupplier currentAmount;
    private final IntSupplier capacity;
    private final int fluidColor;

    public FluidBar(Supplier<String> fluidNameSupplier, IntSupplier currentAmount, IntSupplier capacity, int fluidColor) {
        this.fluidNameSupplier = fluidNameSupplier;
        this.currentAmount = currentAmount;
        this.capacity = capacity;
        this.fluidColor = fluidColor;
        getFlexNode().setSize(16, 50);
    }

    public static FluidBar of(Supplier<String> name, IntSupplier amount, IntSupplier capacity, int color) {
        return new FluidBar(name, amount, capacity, color);
    }

    public static FluidBar of(IntSupplier amount, IntSupplier capacity, int color) {
        return new FluidBar(null, amount, capacity, color);
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        int cur = currentAmount != null ? currentAmount.getAsInt() : 0;
        int cap = capacity != null ? capacity.getAsInt() : 1;
        String name = fluidNameSupplier != null ? fluidNameSupplier.get() : (cur > 0 ? "Fluid" : "Empty");
        if (name == null || name.isEmpty()) name = "Empty";

        String curFormatted = String.format("%,d", cur);
        String capFormatted = String.format("%,d", cap);

        return List.of(
            Component.literal(name + ": ").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
                .append(Component.literal(curFormatted + " / " + capFormatted + " mB").withStyle(ChatFormatting.GRAY))
        );
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        Bounds b = getBounds();

        // 1. Dark fluid well plate
        graphics.fill(b.x(), b.y(), b.x() + b.width(), b.y() + b.height(), 0xFF1C1C24);
        graphics.fill(b.x() + 1, b.y() + 1, b.x() + b.width() - 1, b.y() + b.height() - 1, 0xFF0E0E14);

        int cur = currentAmount != null ? Math.max(0, currentAmount.getAsInt()) : 0;
        int cap = capacity != null ? Math.max(1, capacity.getAsInt()) : 1;
        double ratio = Math.min(1.0, (double) cur / (double) cap);

        if (ratio > 0.0) {
            int innerH = b.height() - 2;
            int fillH = Math.max(1, (int) Math.round(innerH * ratio));
            int fillY = b.y() + 1 + (innerH - fillH);
            graphics.fill(b.x() + 1, fillY, b.x() + b.width() - 1, b.y() + b.height() - 1, fluidColor);
        }

        // Highlight border
        if (hovered) {
            graphics.fill(b.x(), b.y(), b.x() + b.width(), b.y() + 1, 0x44FFFFFF);
            graphics.fill(b.x(), b.y() + b.height() - 1, b.x() + b.width(), b.y() + b.height(), 0x44FFFFFF);
            graphics.fill(b.x(), b.y(), b.x() + 1, b.y() + b.height(), 0x44FFFFFF);
            graphics.fill(b.x() + b.width() - 1, b.y(), b.x() + b.width(), b.y() + b.height(), 0x44FFFFFF);
        }
    }
}
