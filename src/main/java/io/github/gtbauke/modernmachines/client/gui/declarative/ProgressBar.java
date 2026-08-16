package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.function.DoubleSupplier;

import io.github.gtbauke.modernmachines.client.gui.widget.ProgressBarWidget;

public class ProgressBar extends ProgressBarWidget {

    public ProgressBar(ProgressType type, DoubleSupplier progressSupplier) {
        super(type, progressSupplier);
    }

    public static ProgressArrow arrow(DoubleSupplier progressSupplier) {
        return ProgressArrow.of(progressSupplier);
    }

    public static ProgressBar linear(DoubleSupplier progressSupplier) {
        return new ProgressBar(ProgressType.LINEAR_HORIZONTAL, progressSupplier);
    }

    public static ProgressBar linear(DoubleSupplier progressSupplier, int width, int height) {
        ProgressBar bar = new ProgressBar(ProgressType.LINEAR_HORIZONTAL, progressSupplier);
        bar.size(width, height);
        return bar;
    }

    public static ProgressBar flame(DoubleSupplier progressSupplier) {
        return new ProgressBar(ProgressType.FLAME, progressSupplier);
    }
}
