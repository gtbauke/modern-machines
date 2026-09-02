package io.github.gtbauke.modernmachines.api.client.gui.elements;

import java.util.function.DoubleSupplier;

public class BurningElement extends ProgressBarElement {

    public BurningElement(DoubleSupplier burnSupplier) {
        super(ProgressType.FLAME, burnSupplier);
    }

    public static BurningElement flame(DoubleSupplier burnSupplier) {
        return new BurningElement(burnSupplier);
    }
}
