package io.github.gtbauke.modernmachines.client.gui.editor.model;

import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;

import java.util.ArrayList;
import java.util.List;

public class ScreenLayoutDefinition {
    private String screenId = "custom_screen";
    private String title = "Machine";
    private int imageWidth = 176;
    private int imageHeight = 166;
    private int backgroundColor = GUIRenderHelper.ORE_BG_PRIMARY;
    private int borderColor = GUIRenderHelper.ORE_BORDER_DARK;

    private List<ElementDefinition> elements = new ArrayList<>();
    private List<TabDefinition> tabs = new ArrayList<>();

    public ScreenLayoutDefinition() {
    }

    public ScreenLayoutDefinition(String screenId, String title, int imageWidth, int imageHeight) {
        this.screenId = screenId;
        this.title = title;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public ScreenLayoutDefinition deepCopy() {
        var copy = new ScreenLayoutDefinition(this.screenId, this.title, this.imageWidth, this.imageHeight);
        copy.backgroundColor = this.backgroundColor;
        copy.borderColor = this.borderColor;

        for (var el : this.elements) {
            copy.elements.add(el.deepCopy());
        }

        for (var tab : this.tabs) {
            copy.tabs.add(tab.deepCopy());
        }

        return copy;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
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

    public List<TabDefinition> getTabs() {
        return tabs;
    }

    public void setTabs(List<TabDefinition> tabs) {
        this.tabs = tabs != null ? tabs : new ArrayList<>();
    }

    public void addTab(TabDefinition tab) {
        if (tab != null && !tabs.contains(tab)) {
            this.tabs.add(tab);
        }
    }

    public boolean removeTab(TabDefinition tab) {
        return this.tabs.remove(tab);
    }
}
