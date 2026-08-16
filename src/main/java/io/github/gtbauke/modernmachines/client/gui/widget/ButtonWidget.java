package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class ButtonWidget extends UiWidget {
    private Component label;
    private final List<Consumer<ButtonWidget>> clickListeners = new ArrayList<>();
    private Supplier<List<Component>> tooltipSupplier;
    private boolean pressed = false;

    public ButtonWidget(Component label) {
        this.label = label;
        flexNode.setSize(60, 20);
    }

    public ButtonWidget(Component label, Consumer<ButtonWidget> onPress) {
        this(label);
        if (onPress != null) {
            this.clickListeners.add(onPress);
        }
    }

    public ButtonWidget addClickListener(Consumer<ButtonWidget> listener) {
        this.clickListeners.add(listener);
        return this;
    }

    public ButtonWidget setTooltip(Component tooltip) {
        this.tooltipSupplier = () -> List.of(tooltip);
        return this;
    }

    public ButtonWidget setTooltipSupplier(Supplier<List<Component>> supplier) {
        this.tooltipSupplier = supplier;
        return this;
    }

    public ButtonWidget setLabel(Component label) {
        this.label = label;
        return this;
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        if (tooltipSupplier != null) {
            return tooltipSupplier.get();
        }
        return List.of();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        Bounds b = getBounds();
        NineSliceRenderer.SliceDef slice = NineSliceRenderer.BUTTON_NORMAL;
        if (!enabled) {
            slice = NineSliceRenderer.BUTTON_NORMAL;
        } else if (pressed) {
            slice = NineSliceRenderer.BUTTON_PRESSED;
        } else if (hovered) {
            slice = NineSliceRenderer.BUTTON_HOVER;
        }

        NineSliceRenderer.drawNineSlice(graphics, slice, b);
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible || label == null) return;
        Bounds b = getBounds();
        int textW = font.width(label);
        int tx = b.x() + (b.width() - textW) / 2;
        int ty = b.y() + (b.height() - font.lineHeight) / 2 + (pressed ? 1 : 0);
        int textColor = !enabled ? 0xFFAAAAAA : (hovered ? 0xFFFFFFFF : 0xFFDDDDDD);

        graphics.text(font, label, tx, ty, textColor, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled || button != 0) return false;
        this.pressed = true;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        for (Consumer<ButtonWidget> listener : clickListeners) {
            listener.accept(this);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.pressed && button == 0) {
            this.pressed = false;
            return true;
        }
        return false;
    }
}
