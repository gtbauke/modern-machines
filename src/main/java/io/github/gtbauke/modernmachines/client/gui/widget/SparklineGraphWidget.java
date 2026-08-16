package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.LinkedList;
import java.util.function.DoubleSupplier;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class SparklineGraphWidget extends UiWidget {
    private final DoubleSupplier dataSupplier;
    private final LinkedList<Double> history = new LinkedList<>();
    private final int maxSamples;
    private double maxValue = 200.0;
    private int lineColor = 0xFF00E5FF;
    private int fillColor = 0x3300E5FF;
    private long lastTick = 0;

    public SparklineGraphWidget(int width, int height, int maxSamples, DoubleSupplier dataSupplier) {
        this.maxSamples = maxSamples;
        this.dataSupplier = dataSupplier;
        flexNode.setSize(width, height);

        // Pre-populate with flat data
        for (int i = 0; i < maxSamples; i++) {
            history.add(100.0 + Math.sin(i * 0.4) * 40.0);
        }
    }

    public SparklineGraphWidget setLineColor(int color) {
        this.lineColor = color;
        return this;
    }

    public SparklineGraphWidget setFillColor(int color) {
        this.fillColor = color;
        return this;
    }

    public SparklineGraphWidget setMaxValue(double max) {
        this.maxValue = Math.max(1.0, max);
        return this;
    }

    private void updateData() {
        long current = System.currentTimeMillis();
        if (current - lastTick > 150) { // update sample every 150ms
            lastTick = current;
            double val = dataSupplier != null ? dataSupplier.getAsDouble() : 100.0;
            history.addLast(val);
            if (history.size() > maxSamples) {
                history.removeFirst();
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        updateData();
        Bounds b = getBounds();

        // 1. Dark graph background
        GuiRenderHelper.drawRect(graphics, b.x(), b.y(), b.width(), b.height(), 0xFF0E121B);
        GuiRenderHelper.drawRectOutline(graphics, b.x(), b.y(), b.width(), b.height(), 0xFF222B3D);

        // 2. Horizontal grid lines (25%, 50%, 75%)
        int y25 = b.y() + (int) (b.height() * 0.25);
        int y50 = b.y() + (int) (b.height() * 0.50);
        int y75 = b.y() + (int) (b.height() * 0.75);
        GuiRenderHelper.drawHorizontalLine(graphics, b.x() + 2, b.x() + b.width() - 2, y25, 0xFF182030);
        GuiRenderHelper.drawHorizontalLine(graphics, b.x() + 2, b.x() + b.width() - 2, y50, 0xFF182030);
        GuiRenderHelper.drawHorizontalLine(graphics, b.x() + 2, b.x() + b.width() - 2, y75, 0xFF182030);

        // 3. Draw sparkline data points & filled vertical columns
        if (history.size() < 2) return;

        double stepX = (double) (b.width() - 4) / (maxSamples - 1);
        int count = history.size();

        for (int i = 0; i < count; i++) {
            double val = history.get(i);
            double clamped = Math.max(0.0, Math.min(maxValue, val));
            double ratio = clamped / maxValue;

            int px = b.x() + 2 + (int) (i * stepX);
            int py = b.y() + b.height() - 2 - (int) (ratio * (b.height() - 4));

            // Fill vertical column under point
            GuiRenderHelper.drawRect(graphics, px, py, Math.max(1, (int) stepX), b.y() + b.height() - 2 - py, fillColor);

            // Draw line to next point
            if (i < count - 1) {
                double nextVal = history.get(i + 1);
                double nextClamped = Math.max(0.0, Math.min(maxValue, nextVal));
                double nextRatio = nextClamped / maxValue;
                int nextX = b.x() + 2 + (int) ((i + 1) * stepX);
                int nextY = b.y() + b.height() - 2 - (int) (nextRatio * (b.height() - 4));

                GuiRenderHelper.drawLine(graphics, px, py, nextX, nextY, lineColor);
            }
        }
    }
}
