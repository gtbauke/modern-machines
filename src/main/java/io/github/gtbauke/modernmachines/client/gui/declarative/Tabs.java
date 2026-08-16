package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tabs {
    private final List<FloatingTab> tabs = new ArrayList<>();

    public Tabs(List<FloatingTab> tabs) {
        if (tabs != null) {
            for (FloatingTab tab : tabs) {
                if (tab != null) {
                    this.tabs.add(tab);
                }
            }
        }
    }

    public static Tabs of(FloatingTab... tabs) {
        return new Tabs(Arrays.asList(tabs));
    }

    public static Tabs of(List<FloatingTab> tabs) {
        return new Tabs(tabs);
    }

    public static Tabs empty() {
        return new Tabs(List.of());
    }

    public Tabs add(FloatingTab tab) {
        if (tab != null) {
            this.tabs.add(tab);
        }
        return this;
    }

    public List<FloatingTab> getTabs() {
        return tabs;
    }
}
