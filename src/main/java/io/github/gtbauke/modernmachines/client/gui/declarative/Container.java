package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.Collections;
import java.util.List;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Container extends UiWidget {
    private UiWidget child;
    private NineSliceRenderer.SliceDef backgroundNineSlice;
    private int backgroundColor = 0;

    public Container() {
        this.flexNode.setDirection(FlexDirection.COLUMN);
    }

    public Container(UiWidget child) {
        this();
        child(child);
    }

    public static Container of(UiWidget child) {
        return new Container(child);
    }

    public static Container empty() {
        return new Container();
    }

    public Container child(UiWidget child) {
        if (this.child != null) {
            this.flexNode.removeChild(this.child.getFlexNode());
            this.child.setParent(null);
        }
        this.child = child;
        if (child != null) {
            child.setParent(this);
            this.flexNode.addChild(child.getFlexNode());
        }
        return this;
    }

    public UiWidget getChild() {
        return child;
    }

    public Container background(NineSliceRenderer.SliceDef nineSlice) {
        this.backgroundNineSlice = nineSlice;
        return this;
    }

    public Container backgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public Container align(AlignItems align) {
        this.flexNode.setAlignItems(align);
        return this;
    }

    public Container justify(JustifyContent justify) {
        this.flexNode.setJustifyContent(justify);
        return this;
    }

    public Container center() {
        this.flexNode.setAlignItems(AlignItems.CENTER);
        this.flexNode.setJustifyContent(JustifyContent.CENTER);
        return this;
    }

    @Override
    public List<UiWidget> getChildren() {
        return child != null ? List.of(child) : Collections.emptyList();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        Bounds b = getBounds();

        if (backgroundColor != 0) {
            GuiRenderHelper.drawRect(graphics, b.x(), b.y(), b.width(), b.height(), backgroundColor);
        }

        if (backgroundNineSlice != null) {
            NineSliceRenderer.drawNineSlice(graphics, backgroundNineSlice, b);
        }

        if (child != null && child.isVisible()) {
            child.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        if (child != null && child.isVisible()) {
            child.extractForeground(graphics, font, theme, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void updateHoverState(double mouseX, double mouseY) {
        super.updateHoverState(mouseX, mouseY);
        if (child != null) {
            child.updateHoverState(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (child != null && child.isVisible() && child.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
