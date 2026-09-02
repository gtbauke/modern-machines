package io.github.gtbauke.modernmachines.api.client.gui.core.layout;

import io.github.gtbauke.modernmachines.api.client.gui.core.*;
import io.github.gtbauke.modernmachines.api.client.gui.elements.UIElement;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class FlexContainer extends UIElement {
    protected FlexDirection flexDirection;
    protected JustifyContent justifyContent;
    protected AlignItems alignItems;
    protected int gap = 0;

    public FlexContainer(FlexDirection flexDirection, JustifyContent justifyContent, AlignItems alignItems, int gap) {
        this.flexDirection = flexDirection;
        this.justifyContent = justifyContent;
        this.alignItems = alignItems;
        this.gap = gap;
        this.autoSize = true;
    }

    public FlexContainer() {
        this(FlexDirection.ROW, JustifyContent.START, AlignItems.STRETCH, 0);
    }

    public FlexContainer setPosition(Position position) {
        this.position = position;
        return this;
    }

    public FlexContainer setSize(Size size) {
        this.size = size;
        this.autoSize = false;

        return this;
    }

    public FlexContainer setPadding(Padding padding) {
        this.padding = padding;
        return this;
    }

    public FlexContainer setVisibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    public FlexContainer setBackgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public FlexContainer setBorderColor(Color color) {
        this.topBorderColor = color;
        this.rightBorderColor = color;
        this.bottomBorderColor = color;
        this.leftBorderColor = color;
        return this;
    }

    public FlexContainer setTopBorderColor(Color color) {
        this.topBorderColor = color;
        return this;
    }

    public FlexContainer setRightBorderColor(Color color) {
        this.rightBorderColor = color;
        return this;
    }

    public FlexContainer setBottomBorderColor(Color color) {
        this.bottomBorderColor = color;
        return this;
    }

    public FlexContainer setLeftBorderColor(Color color) {
        this.leftBorderColor = color;
        return this;
    }

    public FlexContainer setFlowWeight(int weight) {
        this.flowWeight = weight;
        return this;
    }

    public FlexContainer setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
        return this;
    }

    public FlexContainer setFlexDirection(FlexDirection flexDirection) {
        this.flexDirection = flexDirection;
        return this;
    }

    public FlexContainer setJustifyContent(JustifyContent justifyContent) {
        this.justifyContent = justifyContent;
        return this;
    }

    public FlexContainer setAlignItems(AlignItems alignItems) {
        this.alignItems = alignItems;
        return this;
    }

    public FlexContainer setGap(int gap) {
        this.gap = gap;
        return this;
    }

    @Override
    public void calculateSize() {
        super.calculateSize();

        if (!this.autoSize) {
            return;
        }

        var visibleCount = 0;
        var sumMain = 0;
        var maxCross = 0;

        for (var child : children) {
            if (child.isHidden()) {
                continue;
            }

            visibleCount++;
            if (this.flexDirection == FlexDirection.ROW) {
                sumMain += child.width();
                maxCross = Math.max(maxCross, child.height());
            } else {
                sumMain += child.height();
                maxCross = Math.max(maxCross, child.width());
            }
        }

        var totalGaps = visibleCount > 1 ? (visibleCount - 1) * gap : 0;
        if (this.flexDirection == FlexDirection.ROW) {
            var fullWidth = sumMain + totalGaps + padding.left() + padding.right();
            var fullHeight = maxCross + padding.top() + padding.bottom();

            this.size = new Size(fullWidth, fullHeight);
        } else {
            var fullWidth = maxCross + padding.left() + padding.right();
            var fullHeight = sumMain + totalGaps + padding.top() + padding.bottom();

            this.size = new Size(fullWidth, fullHeight);
        }
    }

    @Override
    public void calculateLayout() {
        var contentWidth = Math.max(0, this.width() - (this.padding.left() + this.padding.right()));
        var contentHeight = Math.max(0, this.height() - (this.padding.top() + this.padding.bottom()));

        var contentMain = this.flexDirection == FlexDirection.ROW ? contentWidth : contentHeight;
        var contentCross = this.flexDirection == FlexDirection.ROW ? contentHeight : contentWidth;

        var visibleCount = 0;
        var fixedMainSize = 0;
        var totalFlowWeight = 0;

        for (var child : children) {
            if (child.isHidden()) {
                continue;
            }

            visibleCount++;
            if (child.flowWeight() > 0) {
                totalFlowWeight += child.flowWeight();
            } else {
                fixedMainSize += (this.flexDirection == FlexDirection.ROW) ? child.width() : child.height();
            }
        }

        var totalGaps = visibleCount > 1 ? (visibleCount - 1) * gap : 0;
        var freeSpace = Math.max(0, contentMain - fixedMainSize - totalGaps);

        if (totalFlowWeight > 0) {
            for (var child : children) {
                if (child.isHidden() || child.flowWeight() <= 0) {
                    continue;
                }

                var allocatedSize = (int) Math.round(((double) child.flowWeight() / totalFlowWeight) * freeSpace);
                if (this.flexDirection == FlexDirection.ROW) {
                    child.setSize(new Size(allocatedSize, child.height()));
                } else {
                    child.setSize(new Size(child.width(), allocatedSize));
                }
            }
        }

        if (this.alignItems == AlignItems.STRETCH) {
            for (var child : children) {
                if (child.isHidden()) {
                    continue;
                }

                if (this.flexDirection == FlexDirection.ROW) {
                    child.setSize(new Size(child.width(), contentCross));
                } else {
                    child.setSize(new Size(contentCross, child.height()));
                }
            }
        }

        var usedMain = 0;
        for (var child : children) {
            if (child.isHidden()) {
                continue;
            }

            usedMain += (this.flexDirection == FlexDirection.ROW) ? child.width() : child.height();
        }

        if (visibleCount > 1) {
            usedMain += (visibleCount - 1) * gap;
        }

        var remainingSpace = Math.max(0, contentMain - usedMain);
        var cursorOffset = 0;
        var extraGap = 0;

        switch (this.justifyContent) {
            case CENTER -> cursorOffset = remainingSpace / 2;
            case END -> cursorOffset = remainingSpace;
            case SPACE_BETWEEN -> {
                if (visibleCount > 1) {
                    extraGap = remainingSpace / (visibleCount - 1);
                }
            }
            case SPACE_AROUND -> {
                if (visibleCount > 0) {
                    extraGap = remainingSpace / visibleCount;
                    cursorOffset = extraGap / 2;
                }
            }
            case SPACE_EVENLY -> {
                if (visibleCount > 0) {
                    extraGap = remainingSpace / (visibleCount + 1);
                    cursorOffset = extraGap;
                }
            }
        }

        var startMain = ((this.flexDirection == FlexDirection.ROW) ? (this.left() + this.padding.left()) : (this.top() + this.padding.top())) + cursorOffset;
        var baseCross = (this.flexDirection == FlexDirection.ROW) ? (this.top() + this.padding.top()) : (this.left() + this.padding.left());

        var currentMain = startMain;
        for (var child : children) {
            if (child.isHidden()) {
                continue;
            }

            var childCross = (this.flexDirection == FlexDirection.ROW) ? child.height() : child.width();
            var crossOffset = 0;

            if (this.alignItems == AlignItems.CENTER) {
                crossOffset = (contentCross - childCross) / 2;
            } else if (this.alignItems == AlignItems.END) {
                crossOffset = contentCross - childCross;
            }

            var childX = (this.flexDirection == FlexDirection.ROW) ? currentMain : baseCross + crossOffset;
            var childY = (this.flexDirection == FlexDirection.ROW) ? baseCross + crossOffset : currentMain;

            child.setPosition(new Position(childX, childY));
            child.calculateLayout();

            var childMain = (this.flexDirection == FlexDirection.ROW) ? child.width() : child.height();
            currentMain += childMain + gap + extraGap;
        }
    }

    @Override
    public void renderChildren(GuiGraphicsExtractor graphics, Position mousePos, float partialTick) {
        for (var child : children) {
            child.render(graphics, mousePos, partialTick);
        }
    }
}
