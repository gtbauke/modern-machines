package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WindowControls {
    private final List<WindowControl> controls = new ArrayList<>();

    public WindowControls(List<WindowControl> controls) {
        if (controls != null) {
            for (WindowControl c : controls) {
                if (c != null) {
                    this.controls.add(c);
                }
            }
        }
    }

    public static WindowControls of(WindowControl... controls) {
        return new WindowControls(Arrays.asList(controls));
    }

    public static WindowControls of(List<WindowControl> controls) {
        return new WindowControls(controls);
    }

    public static WindowControls standardFloating(Runnable onDock, Runnable onClose) {
        return of(WindowControl.dock(onDock), WindowControl.close(onClose));
    }

    public List<WindowControl> getControls() {
        return controls;
    }
}
