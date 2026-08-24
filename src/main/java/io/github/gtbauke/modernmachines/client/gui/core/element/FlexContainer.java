package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.core.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Padding;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;

import java.util.List;

public class FlexContainer extends UIElement {
    protected FlexDirection direction = FlexDirection.ROW;
    protected int gap = 0;
    protected AlignItems alignItems = AlignItems.START;
    protected JustifyContent justifyContent = JustifyContent.START;
    protected boolean autoSize = true;

    public FlexContainer(FlexDirection direction, int gap, AlignItems alignItems, JustifyContent justifyContent, Bounds bounds, Padding padding, UIElement parent, List<UIElement> children) {
        super(bounds, padding, parent, children);
        this.direction = direction != null ? direction : FlexDirection.ROW;
        this.gap = gap;
        this.alignItems = alignItems != null ? alignItems : AlignItems.START;
        this.justifyContent = justifyContent != null ? justifyContent : JustifyContent.START;
    }

    public FlexContainer(FlexDirection direction, int gap, AlignItems alignItems, JustifyContent justifyContent, Bounds bounds, Padding padding) {
        this(direction, gap, alignItems, justifyContent, bounds, padding, null, null);
    }

    public FlexContainer(FlexDirection direction, int gap, AlignItems alignItems, JustifyContent justifyContent) {
        this(direction, gap, alignItems, justifyContent, Bounds.EMPTY, new Padding(0), null, null);
    }

    public FlexContainer(FlexDirection direction, int gap, AlignItems alignItems) {
        this(direction, gap, alignItems, JustifyContent.START);
    }

    public FlexContainer(FlexDirection direction, int gap) {
        this(direction, gap, AlignItems.START, JustifyContent.START);
    }

    public FlexContainer(FlexDirection direction) {
        this(direction, 0, AlignItems.START, JustifyContent.START);
    }

    public FlexContainer() {
        this(FlexDirection.ROW, 0, AlignItems.START, JustifyContent.START);
    }

    public FlexDirection getDirection() {
        return direction;
    }

    public FlexContainer setDirection(FlexDirection direction) {
        this.direction = direction != null ? direction : FlexDirection.ROW;
        markDirty();
        return this;
    }

    public int getGap() {
        return gap;
    }

    public FlexContainer setGap(int gap) {
        this.gap = gap;
        markDirty();
        return this;
    }

    public AlignItems getAlignItems() {
        return alignItems;
    }

    public FlexContainer setAlignItems(AlignItems alignItems) {
        this.alignItems = alignItems != null ? alignItems : AlignItems.START;
        markDirty();
        return this;
    }

    public JustifyContent getJustifyContent() {
        return justifyContent;
    }

    public FlexContainer setJustifyContent(JustifyContent justifyContent) {
        this.justifyContent = justifyContent != null ? justifyContent : JustifyContent.START;
        markDirty();
        return this;
    }

    public boolean isAutoSize() {
        return autoSize;
    }

    public FlexContainer setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
        markDirty();
        return this;
    }

    @Override
    public FlexContainer setSize(Size size) {
        this.autoSize = false;
        super.setSize(size);
        return this;
    }

    @Override
    public void calculateSize() {
        super.calculateSize();

        if (!autoSize) {
            return;
        }

        int count = children.size();
        if (direction == FlexDirection.ROW) {
            int totalW = 0;
            int maxH = 0;

            for (UIElement child : children) {
                totalW += child.getSize().width();
                maxH = Math.max(maxH, child.getSize().height());
            }

            if (count > 1) {
                totalW += (count - 1) * gap;
            }

            int fullW = totalW + padding.left() + padding.right();
            int fullH = maxH + padding.top() + padding.bottom();
            this.bounds = new Bounds(this.bounds.position(), new Size(fullW, fullH));
        } else {
            int maxW = 0;
            int totalH = 0;

            for (UIElement child : children) {
                maxW = Math.max(maxW, child.getSize().width());
                totalH += child.getSize().height();
            }

            if (count > 1) {
                totalH += (count - 1) * gap;
            }

            int fullW = maxW + padding.left() + padding.right();
            int fullH = totalH + padding.top() + padding.bottom();
            this.bounds = new Bounds(this.bounds.position(), new Size(fullW, fullH));
        }
    }

    @Override
    public void calculateLayout() {
        int contentW = Math.max(0, this.bounds.size().width() - (padding.left() + padding.right()));
        int contentH = Math.max(0, this.bounds.size().height() - (padding.top() + padding.bottom()));
        int count = children.size();

        if (direction == FlexDirection.ROW) {
            int totalFlow = 0;
            int fixedW = 0;

            for (var child : children) {
                if (child.isFillParentHeight()) {
                    child.setSize(new Size(child.getSize().width(), contentH));
                }

                if (child.getFlowWeight() > 0) {
                    totalFlow += child.getFlowWeight();
                } else if (child.isFillParentWidth()) {
                    totalFlow += 1;
                } else {
                    fixedW += child.getSize().width();
                }
            }

            int gaps = count > 1 ? (count - 1) * gap : 0;
            int availableW = Math.max(0, contentW - fixedW - gaps);

            int usedW = 0;
            for (var child : children) {
                int flow = child.getFlowWeight() > 0 ? child.getFlowWeight() : (child.isFillParentWidth() ? 1 : 0);
                if (flow > 0 && totalFlow > 0) {
                    int proportionalW = (int) Math.round(((double) flow / totalFlow) * availableW);
                    child.setSize(new Size(Math.max(1, proportionalW), child.getSize().height()));
                }

                usedW += child.getSize().width();
            }

            if (count > 1) {
                usedW += (count - 1) * gap;
            }

            int remainingW = Math.max(0, contentW - usedW);
            int cursorX = 0;
            int extraGap = 0;

            if (justifyContent == JustifyContent.CENTER) {
                cursorX = remainingW / 2;
            } else if (justifyContent == JustifyContent.END) {
                cursorX = remainingW;
            } else if (justifyContent == JustifyContent.SPACE_BETWEEN) {
                extraGap = count > 1 ? remainingW / (count - 1) : 0;
            } else if (justifyContent == JustifyContent.SPACE_AROUND) {
                int unit = count > 0 ? remainingW / (count * 2) : 0;
                cursorX = unit;
                extraGap = unit * 2;
            } else if (justifyContent == JustifyContent.SPACE_EVENLY) {
                int unit = count > 0 ? remainingW / (count + 1) : 0;
                cursorX = unit;
                extraGap = unit;
            }

            for (var child : children) {
                int childH = child.getSize().height();
                int childY = 0;

                if (alignItems == AlignItems.CENTER) {
                    childY = (contentH - childH) / 2;
                } else if (alignItems == AlignItems.END) {
                    childY = contentH - childH;
                }

                child.setPosition(new Position(cursorX, childY));
                cursorX += child.getSize().width() + gap + extraGap;
            }
        } else {
            int totalFlow = 0;
            int fixedH = 0;

            for (var child : children) {
                if (child.isFillParentWidth()) {
                    child.setSize(new Size(contentW, child.getSize().height()));
                }

                if (child.getFlowWeight() > 0) {
                    totalFlow += child.getFlowWeight();
                } else if (child.isFillParentHeight()) {
                    totalFlow += 1;
                } else {
                    fixedH += child.getSize().height();
                }
            }

            int gaps = count > 1 ? (count - 1) * gap : 0;
            int availableH = Math.max(0, contentH - fixedH - gaps);

            int usedH = 0;
            for (var child : children) {
                int flow = child.getFlowWeight() > 0 ? child.getFlowWeight() : (child.isFillParentHeight() ? 1 : 0);
                if (flow > 0 && totalFlow > 0) {
                    int proportionalH = (int) Math.round(((double) flow / totalFlow) * availableH);
                    child.setSize(new Size(child.getSize().width(), Math.max(1, proportionalH)));
                }

                usedH += child.getSize().height();
            }

            if (count > 1) {
                usedH += (count - 1) * gap;
            }

            int remainingH = Math.max(0, contentH - usedH);
            int cursorY = 0;
            int extraGap = 0;

            if (justifyContent == JustifyContent.CENTER) {
                cursorY = remainingH / 2;
            } else if (justifyContent == JustifyContent.END) {
                cursorY = remainingH;
            } else if (justifyContent == JustifyContent.SPACE_BETWEEN) {
                extraGap = count > 1 ? remainingH / (count - 1) : 0;
            } else if (justifyContent == JustifyContent.SPACE_AROUND) {
                int unit = count > 0 ? remainingH / (count * 2) : 0;
                cursorY = unit;
                extraGap = unit * 2;
            } else if (justifyContent == JustifyContent.SPACE_EVENLY) {
                int unit = count > 0 ? remainingH / (count + 1) : 0;
                cursorY = unit;
                extraGap = unit;
            }

            for (var child : children) {
                int childW = child.getSize().width();
                int childX = 0;

                if (alignItems == AlignItems.CENTER) {
                    childX = (contentW - childW) / 2;
                } else if (alignItems == AlignItems.END) {
                    childX = contentW - childW;
                }

                child.setPosition(new Position(childX, cursorY));
                cursorY += child.getSize().height() + gap + extraGap;
            }
        }

        super.calculateLayout();
    }
}
