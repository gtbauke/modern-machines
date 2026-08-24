package io.github.gtbauke.modernmachines.client.gui.editor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ElementDefinition {
    public enum ElementType {
        SLOT,
        SLOT_GRID,
        PLAYER_INVENTORY,
        PROGRESS_ARROW,
        PROGRESS_LINEAR,
        FLAME,
        COLUMN,
        ROW,
        SPACER,
        BUTTON,
        TEXT,
        ICON,
        SIDE_TAB,
        BLOCK_FACE,
        SIDE_CONFIG_GRID
    }

    public enum LabelSourceType {
        STATIC,
        TRANSLATABLE,
        MENU_DATA
    }

    private String id;
    private ElementType type;
    private int x;
    private int y;
    private int width;
    private int height;

    private int slotIndex = 0;
    private int slotCount = 1;
    private int gridRows = 2;
    private int gridCols = 2;

    private int dataIndex = 0;
    private int dataMax = 100;

    private int gap = 0;
    private String align = "CENTER";
    private String justifyContent = "START";

    private String text = "";
    private int color = 0xFFFFFFFF;
    private LabelSourceType labelSource = LabelSourceType.STATIC;
    private String labelFormat = "%d";
    private String labelAlign = "LEFT";
    private boolean shadow = true;

    private String iconItem = "minecraft:iron_ingot";
    private float opacity = 1.0f;
    private String colorMode = "RGB";

    private String relativeSide = "FRONT";

    private boolean locked = false;
    private boolean fitParentWidth = false;
    private boolean fitParentHeight = false;
    private int flowAmount = 0;

    private List<ElementDefinition> children = new ArrayList<>();

    public ElementDefinition() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
    }

    public ElementDefinition(ElementType type) {
        this();
        this.type = type;
        applyDefaultsForType(type);
    }

    private void applyDefaultsForType(ElementType type) {
        switch (type) {
            case SLOT -> {
                this.width = 18;
                this.height = 18;
            }
            case SLOT_GRID -> {
                this.width = 36;
                this.height = 36;
                this.gridRows = 2;
                this.gridCols = 2;
            }
            case PLAYER_INVENTORY -> {
                this.width = 162;
                this.height = 76;
            }
            case PROGRESS_ARROW -> {
                this.width = 22;
                this.height = 15;
            }
            case PROGRESS_LINEAR -> {
                this.width = 13;
                this.height = 5;
            }
            case FLAME -> {
                this.width = 13;
                this.height = 13;
            }
            case COLUMN -> {
                this.width = 50;
                this.height = 50;
                this.gap = 2;
                this.align = "CENTER";
                this.justifyContent = "START";
            }
            case ROW -> {
                this.width = 50;
                this.height = 20;
                this.gap = 2;
                this.align = "CENTER";
                this.justifyContent = "START";
            }
            case SPACER -> {
                this.width = 10;
                this.height = 10;
            }
            case BUTTON -> {
                this.width = 60;
                this.height = 16;
                this.text = "Button";
            }
            case TEXT -> {
                this.width = 60;
                this.height = 10;
                this.text = "Label";
                this.color = 0xFFFFFFFF;
                this.labelSource = LabelSourceType.STATIC;
            }
            case ICON -> {
                this.width = 18;
                this.height = 18;
                this.iconItem = "minecraft:iron_ingot";
                this.opacity = 1.0f;
                this.colorMode = "RGB";
            }
            case SIDE_TAB -> {
                this.width = 28;
                this.height = 28;
                this.text = "Tab";
            }
            case BLOCK_FACE -> {
                this.width = 22;
                this.height = 22;
                this.relativeSide = "FRONT";
            }
            case SIDE_CONFIG_GRID -> {
                this.width = 100;
                this.height = 118;
            }
        }
    }

    public ElementDefinition deepCopy() {
        var copy = new ElementDefinition(this.type);
        copy.id = UUID.randomUUID().toString().substring(0, 8);
        copy.x = this.x;
        copy.y = this.y;
        copy.width = this.width;
        copy.height = this.height;
        copy.slotIndex = this.slotIndex;
        copy.slotCount = this.slotCount;
        copy.gridRows = this.gridRows;
        copy.gridCols = this.gridCols;
        copy.dataIndex = this.dataIndex;
        copy.dataMax = this.dataMax;
        copy.gap = this.gap;
        copy.align = this.align;
        copy.justifyContent = this.justifyContent;
        copy.text = this.text;
        copy.color = this.color;
        copy.labelSource = this.labelSource;
        copy.labelFormat = this.labelFormat;
        copy.labelAlign = this.labelAlign;
        copy.shadow = this.shadow;
        copy.iconItem = this.iconItem;
        copy.opacity = this.opacity;
        copy.colorMode = this.colorMode;
        copy.relativeSide = this.relativeSide;
        copy.locked = this.locked;
        copy.fitParentWidth = this.fitParentWidth;
        copy.fitParentHeight = this.fitParentHeight;
        copy.flowAmount = this.flowAmount;

        for (var child : this.children) {
            copy.children.add(child.deepCopy());
        }

        return copy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ElementType getType() {
        return type;
    }

    public void setType(ElementType type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public void setSlotIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public int getSlotCount() {
        return slotCount;
    }

    public void setSlotCount(int slotCount) {
        this.slotCount = slotCount;
    }

    public int getGridRows() {
        return gridRows;
    }

    public void setGridRows(int gridRows) {
        this.gridRows = gridRows;
    }

    public int getGridCols() {
        return gridCols;
    }

    public void setGridCols(int gridCols) {
        this.gridCols = gridCols;
    }

    public int getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }

    public int getDataMax() {
        return dataMax;
    }

    public void setDataMax(int dataMax) {
        this.dataMax = dataMax;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align != null ? align : "CENTER";
    }

    public String getJustifyContent() {
        return justifyContent;
    }

    public void setJustifyContent(String justifyContent) {
        this.justifyContent = justifyContent != null ? justifyContent : "START";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public LabelSourceType getLabelSource() {
        return labelSource;
    }

    public void setLabelSource(LabelSourceType labelSource) {
        this.labelSource = labelSource != null ? labelSource : LabelSourceType.STATIC;
    }

    public String getLabelFormat() {
        return labelFormat;
    }

    public void setLabelFormat(String labelFormat) {
        this.labelFormat = labelFormat;
    }

    public String getLabelAlign() {
        return labelAlign;
    }

    public void setLabelAlign(String labelAlign) {
        this.labelAlign = labelAlign != null ? labelAlign : "LEFT";
    }

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public String getIconItem() {
        return iconItem;
    }

    public void setIconItem(String iconItem) {
        this.iconItem = iconItem != null ? iconItem : "minecraft:iron_ingot";
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = Math.max(0.0f, Math.min(1.0f, opacity));
    }

    public String getColorMode() {
        return colorMode;
    }

    public void setColorMode(String colorMode) {
        this.colorMode = colorMode != null ? colorMode : "RGB";
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isFitParentWidth() {
        return fitParentWidth;
    }

    public void setFitParentWidth(boolean fitParentWidth) {
        this.fitParentWidth = fitParentWidth;
    }

    public boolean isFitParentHeight() {
        return fitParentHeight;
    }

    public void setFitParentHeight(boolean fitParentHeight) {
        this.fitParentHeight = fitParentHeight;
    }

    public int getFlowAmount() {
        return flowAmount;
    }

    public void setFlowAmount(int flowAmount) {
        this.flowAmount = Math.max(0, flowAmount);
    }

    public String getRelativeSide() {
        return relativeSide;
    }

    public void setRelativeSide(String relativeSide) {
        this.relativeSide = relativeSide != null ? relativeSide : "FRONT";
    }

    public List<ElementDefinition> getChildren() {
        return children;
    }

    public void setChildren(List<ElementDefinition> children) {
        this.children = children != null ? children : new ArrayList<>();
    }

    public void addChild(ElementDefinition child) {
        if (child != null) {
            this.children.add(child);
        }
    }

    public boolean removeChild(ElementDefinition child) {
        return this.children.remove(child);
    }
}
