package io.github.gtbauke.modernmachines.client.gui.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlexNode {
    private FlexDirection direction = FlexDirection.ROW;
    private JustifyContent justifyContent = JustifyContent.FLEX_START;
    private AlignItems alignItems = AlignItems.FLEX_START;
    private FlexWrap wrap = FlexWrap.NO_WRAP;

    private FlexSize width = FlexSize.AUTO;
    private FlexSize height = FlexSize.AUTO;
    private int minWidth = 0;
    private int maxWidth = Integer.MAX_VALUE;
    private int minHeight = 0;
    private int maxHeight = Integer.MAX_VALUE;

    private FlexInsets padding = FlexInsets.ZERO;
    private FlexInsets margin = FlexInsets.ZERO;
    private int gap = 0;

    private float flexGrow = 0.0f;
    private float flexShrink = 1.0f;

    private int measuredWidth = 0;
    private int measuredHeight = 0;
    private Bounds bounds = Bounds.EMPTY;

    private FlexNode parent;
    private final List<FlexNode> children = new ArrayList<>();

    public FlexNode() {}

    public FlexDirection getDirection() { return direction; }
    public FlexNode setDirection(FlexDirection direction) { this.direction = direction; return this; }

    public JustifyContent getJustifyContent() { return justifyContent; }
    public FlexNode setJustifyContent(JustifyContent justifyContent) { this.justifyContent = justifyContent; return this; }

    public AlignItems getAlignItems() { return alignItems; }
    public FlexNode setAlignItems(AlignItems alignItems) { this.alignItems = alignItems; return this; }

    private boolean displayed = true;
    private float aspectRatio = 0.0f;

    public boolean isDisplayed() { return displayed; }
    public FlexNode setDisplayed(boolean displayed) { this.displayed = displayed; return this; }

    public FlexWrap getWrap() { return wrap; }
    public FlexNode setWrap(FlexWrap wrap) { this.wrap = wrap; return this; }

    public float getAspectRatio() { return aspectRatio; }
    public FlexNode setAspectRatio(float aspectRatio) { this.aspectRatio = aspectRatio; return this; }

    public FlexSize getWidth() { return width; }
    public FlexNode setWidth(FlexSize width) { this.width = width; return this; }
    public FlexNode setWidth(Length length) { this.width = FlexSize.fromLength(length); return this; }
    public FlexNode setWidth(int px) { this.width = FlexSize.px(px); return this; }

    public FlexSize getHeight() { return height; }
    public FlexNode setHeight(FlexSize height) { this.height = height; return this; }
    public FlexNode setHeight(Length length) { this.height = FlexSize.fromLength(length); return this; }
    public FlexNode setHeight(int px) { this.height = FlexSize.px(px); return this; }

    public FlexNode setSize(int w, int h) {
        this.width = FlexSize.px(w);
        this.height = FlexSize.px(h);
        return this;
    }

    public FlexNode setSize(Length w, Length h) {
        this.width = FlexSize.fromLength(w);
        this.height = FlexSize.fromLength(h);
        return this;
    }

    public int getMinWidth() { return minWidth; }
    public FlexNode setMinWidth(int minWidth) { this.minWidth = minWidth; return this; }

    public int getMaxWidth() { return maxWidth; }
    public FlexNode setMaxWidth(int maxWidth) { this.maxWidth = maxWidth; return this; }

    public int getMinHeight() { return minHeight; }
    public FlexNode setMinHeight(int minHeight) { this.minHeight = minHeight; return this; }

    public int getMaxHeight() { return maxHeight; }
    public FlexNode setMaxHeight(int maxHeight) { this.maxHeight = maxHeight; return this; }

    public FlexInsets getPadding() { return padding; }
    public FlexNode setPadding(FlexInsets padding) { this.padding = padding; return this; }
    public FlexNode setPadding(int all) { this.padding = FlexInsets.all(all); return this; }

    public FlexInsets getMargin() { return margin; }
    public FlexNode setMargin(FlexInsets margin) { this.margin = margin; return this; }
    public FlexNode setMargin(int all) { this.margin = FlexInsets.all(all); return this; }

    public int getGap() { return gap; }
    public FlexNode setGap(int gap) { this.gap = gap; return this; }

    public float getFlexGrow() { return flexGrow; }
    public FlexNode setFlexGrow(float flexGrow) { this.flexGrow = flexGrow; return this; }

    public float getFlexShrink() { return flexShrink; }
    public FlexNode setFlexShrink(float flexShrink) { this.flexShrink = flexShrink; return this; }

    public Bounds getBounds() { return bounds; }
    public int getMeasuredWidth() { return measuredWidth; }
    public int getMeasuredHeight() { return measuredHeight; }

    public FlexNode getParent() { return parent; }
    public List<FlexNode> getChildren() { return Collections.unmodifiableList(children); }

    public FlexNode addChild(FlexNode child) {
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        child.parent = this;
        children.add(child);
        return this;
    }

    public FlexNode removeChild(FlexNode child) {
        if (children.remove(child)) {
            child.parent = null;
        }
        return this;
    }

    public void clearChildren() {
        for (FlexNode child : children) {
            child.parent = null;
        }
        children.clear();
    }

    /**
     * Pass 1: Measure content and resolve natural dimensions
     */
    public void measure(int availableWidth, int availableHeight) {
        if (!displayed) {
            measuredWidth = 0;
            measuredHeight = 0;
            return;
        }

        int resolvedW = 0;
        int resolvedH = 0;

        if (!width.isAuto()) {
            resolvedW = Math.round(width.resolve(availableWidth));
        }
        if (!height.isAuto()) {
            resolvedH = Math.round(height.resolve(availableHeight));
        }

        int innerAvailableW = Math.max(0, (resolvedW > 0 ? resolvedW : availableWidth) - padding.horizontal() - margin.horizontal());
        int innerAvailableH = Math.max(0, (resolvedH > 0 ? resolvedH : availableHeight) - padding.vertical() - margin.vertical());

        int contentMain = 0;
        int contentCross = 0;
        int displayedCount = 0;

        for (int i = 0; i < children.size(); i++) {
            FlexNode child = children.get(i);
            if (!child.displayed) continue;
            displayedCount++;
            child.measure(innerAvailableW, innerAvailableH);

            int childW = child.measuredWidth + child.margin.horizontal();
            int childH = child.measuredHeight + child.margin.vertical();

            if (direction.isHorizontal()) {
                contentMain += childW;
                contentCross = Math.max(contentCross, childH);
            } else {
                contentMain += childH;
                contentCross = Math.max(contentCross, childW);
            }
        }

        if (displayedCount > 1) {
            contentMain += (displayedCount - 1) * gap;
        }

        if (width.isAuto()) {
            measuredWidth = direction.isHorizontal() ? contentMain : contentCross;
            measuredWidth += padding.horizontal();
        } else {
            measuredWidth = resolvedW;
        }

        if (height.isAuto()) {
            measuredHeight = direction.isHorizontal() ? contentCross : contentMain;
            measuredHeight += padding.vertical();
        } else {
            measuredHeight = resolvedH;
        }

        if (aspectRatio > 0.0f) {
            if (width.isAuto() && !height.isAuto()) {
                measuredWidth = Math.round(measuredHeight * aspectRatio);
            } else if (!width.isAuto() && height.isAuto()) {
                measuredHeight = Math.round(measuredWidth / aspectRatio);
            }
        }

        measuredWidth = Math.max(minWidth, Math.min(maxWidth, measuredWidth));
        measuredHeight = Math.max(minHeight, Math.min(maxHeight, measuredHeight));
    }

    /**
     * Pass 2: Layout children within allocated bounding box
     */
    public void layout(int x, int y, int allocatedWidth, int allocatedHeight) {
        if (!displayed) {
            this.bounds = Bounds.EMPTY;
            return;
        }

        this.bounds = new Bounds(x + margin.left(), y + margin.top(), allocatedWidth, allocatedHeight);

        if (children.isEmpty()) return;

        int innerX = bounds.x() + padding.left();
        int innerY = bounds.y() + padding.top();
        int innerW = Math.max(0, bounds.width() - padding.horizontal());
        int innerH = Math.max(0, bounds.height() - padding.vertical());

        boolean isHoriz = direction.isHorizontal();
        int mainSpace = isHoriz ? innerW : innerH;
        int crossSpace = isHoriz ? innerH : innerW;

        int totalMainNeeded = 0;
        float totalGrow = 0.0f;
        float totalShrink = 0.0f;
        int displayedCount = 0;

        for (FlexNode child : children) {
            if (!child.displayed) continue;
            displayedCount++;
            int childMain = isHoriz ? (child.measuredWidth + child.margin.horizontal()) : (child.measuredHeight + child.margin.vertical());
            totalMainNeeded += childMain;
            totalGrow += child.flexGrow;
            totalShrink += child.flexShrink;
        }

        if (displayedCount > 1) {
            totalMainNeeded += (displayedCount - 1) * gap;
        }

        int remainingSpace = mainSpace - totalMainNeeded;

        // Determine main item sizes
        int[] childMainSizes = new int[children.size()];
        int[] childCrossSizes = new int[children.size()];

        for (int i = 0; i < children.size(); i++) {
            FlexNode child = children.get(i);
            if (!child.displayed) continue;
            int baseMain = isHoriz ? child.measuredWidth : child.measuredHeight;
            int baseCross = isHoriz ? child.measuredHeight : child.measuredWidth;

            if (remainingSpace > 0 && totalGrow > 0 && child.flexGrow > 0) {
                baseMain += Math.round((remainingSpace * child.flexGrow) / totalGrow);
            } else if (remainingSpace < 0 && totalShrink > 0 && child.flexShrink > 0) {
                int shrinkAmount = Math.round((Math.abs(remainingSpace) * child.flexShrink) / totalShrink);
                baseMain = Math.max(isHoriz ? child.minWidth : child.minHeight, baseMain - shrinkAmount);
            }

            if (alignItems == AlignItems.STRETCH && (isHoriz ? child.height.isAuto() : child.width.isAuto())) {
                baseCross = crossSpace - (isHoriz ? child.margin.vertical() : child.margin.horizontal());
            }

            childMainSizes[i] = Math.max(0, baseMain);
            childCrossSizes[i] = Math.max(0, baseCross);
        }

        // Justify main-axis positions
        int currentMain = 0;
        int spacing = gap;

        if (totalGrow == 0.0f && remainingSpace > 0 && displayedCount > 0) {
            switch (justifyContent) {
                case CENTER -> currentMain = remainingSpace / 2;
                case FLEX_END -> currentMain = remainingSpace;
                case SPACE_BETWEEN -> {
                    if (displayedCount > 1) {
                        spacing = gap + (remainingSpace / (displayedCount - 1));
                    }
                }
                case SPACE_AROUND -> {
                    int spaceUnit = remainingSpace / displayedCount;
                    currentMain = spaceUnit / 2;
                    spacing = gap + spaceUnit;
                }
                case SPACE_EVENLY -> {
                    int spaceUnit = remainingSpace / (displayedCount + 1);
                    currentMain = spaceUnit;
                    spacing = gap + spaceUnit;
                }
                case FLEX_START -> currentMain = 0;
            }
        }

        int count = children.size();
        for (int i = 0; i < count; i++) {
            int index = direction.isReversed() ? (count - 1 - i) : i;
            FlexNode child = children.get(index);
            if (!child.displayed) {
                child.bounds = Bounds.EMPTY;
                continue;
            }

            int mainSize = childMainSizes[index];
            int crossSize = childCrossSizes[index];

            // Cross-axis alignment
            int crossOffset = 0;
            int childCrossMargin = isHoriz ? child.margin.vertical() : child.margin.horizontal();
            int availableCross = crossSpace - crossSize - childCrossMargin;

            switch (alignItems) {
                case CENTER -> crossOffset = Math.max(0, availableCross / 2);
                case FLEX_END -> crossOffset = Math.max(0, availableCross);
                case FLEX_START, STRETCH -> crossOffset = 0;
            }

            int childX = isHoriz ? (innerX + currentMain) : (innerX + crossOffset);
            int childY = isHoriz ? (innerY + crossOffset) : (innerY + currentMain);
            int childW = isHoriz ? mainSize : crossSize;
            int childH = isHoriz ? crossSize : mainSize;

            child.layout(childX, childY, childW, childH);

            currentMain += mainSize + (isHoriz ? child.margin.horizontal() : child.margin.vertical()) + spacing;
        }
    }
}
