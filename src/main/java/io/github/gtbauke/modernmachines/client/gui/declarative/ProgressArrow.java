package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.function.DoubleSupplier;

import io.github.gtbauke.modernmachines.client.gui.widget.ProgressBarWidget;

public class ProgressArrow extends ProgressBarWidget {

    public ProgressArrow(DoubleSupplier progressSupplier) {
        super(ProgressType.ARROW, progressSupplier);
    }

    public static ProgressArrow of(DoubleSupplier progressSupplier) {
        return new ProgressArrow(progressSupplier);
    }
}
