package io.github.gtbauke.modernmachines.client.gui.declarative;

import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;

public class Scaffold {
    private final Tabs tabs;
    private final UiWidget body;

    public Scaffold(Tabs tabs, UiWidget body) {
        this.tabs = tabs != null ? tabs : Tabs.empty();
        this.body = body;
    }

    public static Scaffold of(Tabs tabs, UiWidget body) {
        return new Scaffold(tabs, body);
    }

    public static Scaffold of(Tabs tabs, MainWindow mainWindow) {
        return new Scaffold(tabs, mainWindow != null ? mainWindow.body() : null);
    }

    public static Scaffold of(MainWindow mainWindow) {
        return new Scaffold(Tabs.empty(), mainWindow != null ? mainWindow.body() : null);
    }

    public static Scaffold of(UiWidget body) {
        return new Scaffold(Tabs.empty(), body);
    }

    public Tabs getTabs() { return tabs; }
    public UiWidget getBody() { return body; }
}
