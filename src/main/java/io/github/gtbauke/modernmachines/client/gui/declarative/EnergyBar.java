package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class EnergyBar extends UiWidget {
    private final LongSupplier currentEnergy;
    private final LongSupplier maxEnergy;
    private Supplier<String> rateSupplier;
    private boolean vertical = true;

    public EnergyBar(LongSupplier currentEnergy, LongSupplier maxEnergy) {
        this.currentEnergy = currentEnergy;
        this.maxEnergy = maxEnergy;
        getFlexNode().setSize(12, 50);
    }

    public static EnergyBar vertical(LongSupplier current, LongSupplier max) {
        EnergyBar bar = new EnergyBar(current, max);
        bar.vertical = true;
        bar.getFlexNode().setSize(12, 50);
        return bar;
    }

    public static EnergyBar vertical(IntSupplier current, IntSupplier max) {
        return vertical(() -> (long) current.getAsInt(), () -> (long) max.getAsInt());
    }

    public static EnergyBar horizontal(LongSupplier current, LongSupplier max) {
        EnergyBar bar = new EnergyBar(current, max);
        bar.vertical = false;
        bar.getFlexNode().setSize(50, 12);
        return bar;
    }

    public static EnergyBar horizontal(IntSupplier current, IntSupplier max) {
        return horizontal(() -> (long) current.getAsInt(), () -> (long) max.getAsInt());
    }

    public EnergyBar rate(Supplier<String> rate) {
        this.rateSupplier = rate;
        return this;
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        long cur = currentEnergy != null ? currentEnergy.getAsLong() : 0;
        long max = maxEnergy != null ? maxEnergy.getAsLong() : 1;
        String curFormatted = String.format("%,d", cur);
        String maxFormatted = String.format("%,d", max);

        Component header = Component.literal("Energy: ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                .append(Component.literal(curFormatted + " / " + maxFormatted + " FE").withStyle(ChatFormatting.YELLOW));

        if (rateSupplier != null) {
            String r = rateSupplier.get();
            if (r != null && !r.isEmpty()) {
                return List.of(header, Component.literal(r).withStyle(ChatFormatting.GRAY));
            }
        }
        return List.of(header);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        Bounds b = getBounds();

        // 1. Dark well frame
        graphics.fill(b.x(), b.y(), b.x() + b.width(), b.y() + b.height(), 0xFF1C1C24);
        graphics.fill(b.x() + 1, b.y() + 1, b.x() + b.width() - 1, b.y() + b.height() - 1, 0xFF0E0E14);

        long cur = currentEnergy != null ? Math.max(0, currentEnergy.getAsLong()) : 0;
        long max = maxEnergy != null ? Math.max(1, maxEnergy.getAsLong()) : 1;
        double ratio = Math.min(1.0, (double) cur / (double) max);

        if (ratio > 0.0) {
            if (vertical) {
                int innerH = b.height() - 2;
                int fillH = Math.max(1, (int) Math.round(innerH * ratio));
                int fillY = b.y() + 1 + (innerH - fillH);
                // Electric red-to-amber gradient
                int topColor = 0xFFFF4444;
                int bottomColor = 0xFFCC1111;
                graphics.fillGradient(b.x() + 1, fillY, b.x() + b.width() - 1, b.y() + b.height() - 1, topColor, bottomColor);
            } else {
                int innerW = b.width() - 2;
                int fillW = Math.max(1, (int) Math.round(innerW * ratio));
                graphics.fillGradient(b.x() + 1, b.y() + 1, b.x() + 1 + fillW, b.y() + b.height() - 1, 0xFFFF4444, 0xFFCC1111);
            }
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
