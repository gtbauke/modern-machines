package io.github.gtbauke.modernmachines.client.gui.editor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TabDefinition {
    private String id;
    private String title = "Tab Window";
    private String side = "RIGHT";
    private String iconItem = "minecraft:redstone";
    private String tooltip = "Tab";
    private int windowWidth = 120;
    private int windowHeight = 100;
    private List<ElementDefinition> elements = new ArrayList<>();

    public TabDefinition() {
        this.id = "tab_" + UUID.randomUUID().toString().substring(0, 6);
    }

    public TabDefinition(String id, String title, String iconItem, String tooltip, String side, int windowWidth, int windowHeight) {
        this.id = id != null ? id : "tab_" + UUID.randomUUID().toString().substring(0, 6);
        this.title = title;
        this.iconItem = iconItem;
        this.tooltip = tooltip;
        this.side = side != null ? side : "RIGHT";
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    public TabDefinition deepCopy() {
        var copy = new TabDefinition(this.id, this.title, this.iconItem, this.tooltip, this.side, this.windowWidth, this.windowHeight);
        for (var el : this.elements) {
            copy.elements.add(el.deepCopy());
        }

        return copy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getIconItem() {
        return iconItem;
    }

    public void setIconItem(String iconItem) {
        this.iconItem = iconItem;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public List<ElementDefinition> getElements() {
        return elements;
    }

    public void setElements(List<ElementDefinition> elements) {
        this.elements = elements != null ? elements : new ArrayList<>();
    }

    public void addElement(ElementDefinition element) {
        if (element != null) {
            this.elements.add(element);
        }
    }

    public boolean removeElement(ElementDefinition element) {
        return this.elements.remove(element);
    }
}
