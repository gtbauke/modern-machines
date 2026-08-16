package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexNode;
import io.github.gtbauke.modernmachines.client.gui.layout.Length;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public abstract class UiWidget {
    protected final FlexNode flexNode = new FlexNode();
    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean hovered = false;
    protected boolean focused = false;
    protected UiWidget parent;

    protected BooleanSupplier visibilitySupplier;
    protected BooleanSupplier enabledSupplier;
    protected Supplier<Component> tooltipSupplier;
    protected Consumer<UiWidget> clickHandler;
    protected Consumer<Boolean> hoverHandler;

    public FlexNode getFlexNode() {
        return flexNode;
    }

    public Bounds getBounds() {
        return flexNode.getBounds();
    }

    public boolean isVisible() {
        return visibilitySupplier != null ? visibilitySupplier.getAsBoolean() : visible;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T setVisible(boolean visible) {
        this.visible = visible;
        this.visibilitySupplier = null;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T visibleWhen(BooleanSupplier supplier) {
        this.visibilitySupplier = supplier;
        return (T) this;
    }

    public boolean isEnabled() {
        return enabledSupplier != null ? enabledSupplier.getAsBoolean() : enabled;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.enabledSupplier = null;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T enabledWhen(BooleanSupplier supplier) {
        this.enabledSupplier = supplier;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T withTooltip(Supplier<Component> supplier) {
        this.tooltipSupplier = supplier;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T withTooltip(Component component) {
        this.tooltipSupplier = () -> component;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T onClick(Consumer<UiWidget> clickHandler) {
        this.clickHandler = clickHandler;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T onHover(Consumer<Boolean> hoverHandler) {
        this.hoverHandler = hoverHandler;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T size(int width, int height) {
        this.flexNode.setSize(width, height);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T size(Length width, Length height) {
        this.flexNode.setSize(width, height);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T width(int width) {
        this.flexNode.setWidth(width);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T width(Length width) {
        this.flexNode.setWidth(width);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T height(int height) {
        this.flexNode.setHeight(height);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T height(Length height) {
        this.flexNode.setHeight(height);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T widthPct(float pct) {
        this.flexNode.setWidth(Length.pct(pct));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T heightPct(float pct) {
        this.flexNode.setHeight(Length.pct(pct));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T sizePct(float wPct, float hPct) {
        this.flexNode.setSize(Length.pct(wPct), Length.pct(hPct));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T matchParentWidth() {
        this.flexNode.setWidth(Length.matchParent());
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T matchParentHeight() {
        this.flexNode.setHeight(Length.matchParent());
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T matchParent() {
        this.flexNode.setSize(Length.matchParent(), Length.matchParent());
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T minWidth(int minW) {
        this.flexNode.setMinWidth(minW);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T maxWidth(int maxW) {
        this.flexNode.setMaxWidth(maxW);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T minHeight(int minH) {
        this.flexNode.setMinHeight(minH);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T maxHeight(int maxH) {
        this.flexNode.setMaxHeight(maxH);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T aspectRatio(float ratio) {
        this.flexNode.setAspectRatio(ratio);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T flexGrow(float flexGrow) {
        this.flexNode.setFlexGrow(flexGrow);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T padding(FlexInsets insets) {
        this.flexNode.setPadding(insets);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T padding(int top, int right, int bottom, int left) {
        this.flexNode.setPadding(FlexInsets.of(top, right, bottom, left));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T padding(int all) {
        this.flexNode.setPadding(FlexInsets.all(all));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T paddingAll(int p) {
        this.flexNode.setPadding(FlexInsets.all(p));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T margin(FlexInsets insets) {
        this.flexNode.setMargin(insets);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T margin(int top, int right, int bottom, int left) {
        this.flexNode.setMargin(FlexInsets.of(top, right, bottom, left));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UiWidget> T marginAll(int m) {
        this.flexNode.setMargin(FlexInsets.all(m));
        return (T) this;
    }

    public boolean isHovered() {
        return hovered;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public UiWidget getParent() {
        return parent;
    }

    public void setParent(UiWidget parent) {
        this.parent = parent;
    }

    public List<UiWidget> getChildren() {
        return Collections.emptyList();
    }

    public void syncFlexDisplay() {
        this.flexNode.setDisplayed(isVisible());
        for (UiWidget child : getChildren()) {
            child.syncFlexDisplay();
        }
    }

    public void updateHoverState(double mouseX, double mouseY) {
        boolean wasHovered = this.hovered;
        this.hovered = isVisible() && getBounds().contains(mouseX, mouseY);
        if (wasHovered != this.hovered && hoverHandler != null) {
            hoverHandler.accept(this.hovered);
        }
    }

    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        // Subclasses override to draw background elements
    }

    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        // Subclasses override to draw text, overlays, icons
    }

    public List<Component> getTooltip(int mouseX, int mouseY) {
        if (tooltipSupplier != null) {
            Component c = tooltipSupplier.get();
            if (c != null) {
                return List.of(c);
            }
        }
        return Collections.emptyList();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isVisible() && isEnabled() && getBounds().contains(mouseX, mouseY)) {
            if (clickHandler != null) {
                clickHandler.accept(this);
                return true;
            }
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }
}
