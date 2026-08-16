package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import io.github.gtbauke.modernmachines.client.gui.widget.BurnIndicatorWidget;

public class BurnIndicator extends BurnIndicatorWidget {

    public BurnIndicator(DoubleSupplier progressSupplier) {
        super(progressSupplier);
    }

    public BurnIndicator(DoubleSupplier progressSupplier, BooleanSupplier isLitSupplier) {
        super(progressSupplier, isLitSupplier);
    }

    public static BurnIndicator of(DoubleSupplier progressSupplier) {
        return new BurnIndicator(progressSupplier);
    }

    public static BurnIndicator of(DoubleSupplier progressSupplier, BooleanSupplier isLitSupplier) {
        return new BurnIndicator(progressSupplier, isLitSupplier);
    }
}
