package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class IconButtonWidget extends UiWidget {
    private final int spriteU;
    private final int spriteV;
    private final int spriteSize;
    private final List<Consumer<IconButtonWidget>> clickListeners = new ArrayList<>();
    private final List<Component> tooltips = new ArrayList<>();
    private boolean drawButtonFrame = true;
    private boolean active = false;

    public IconButtonWidget(int spriteU, int spriteV, int spriteSize) {
        this.spriteU = spriteU;
        this.spriteV = spriteV;
        this.spriteSize = spriteSize;
        flexNode.setSize(spriteSize + 4, spriteSize + 4);
    }

    public IconButtonWidget(int spriteU, int spriteV, int spriteSize, Consumer<IconButtonWidget> onPress) {
        this(spriteU, spriteV, spriteSize);
        this.clickListeners.add(onPress);
    }

    public IconButtonWidget setDrawButtonFrame(boolean draw) {
        this.drawButtonFrame = draw;
        return this;
    }

    public IconButtonWidget setActive(boolean active) {
        this.active = active;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public IconButtonWidget addTooltip(Component tooltip) {
        this.tooltips.add(tooltip);
        return this;
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        return tooltips;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        Bounds b = getBounds();
        if (drawButtonFrame) {
            NineSliceRenderer.SliceDef slice = active ? NineSliceRenderer.BUTTON_PRESSED : (hovered ? NineSliceRenderer.BUTTON_HOVER : NineSliceRenderer.BUTTON_NORMAL);
            NineSliceRenderer.drawNineSlice(graphics, slice, b);
        }
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        Bounds b = getBounds();
        int iconX = b.x() + (b.width() - spriteSize) / 2;
        int iconY = b.y() + (b.height() - spriteSize) / 2 + (active ? 1 : 0);

        graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, iconX, iconY, (float) spriteU, (float) spriteV, spriteSize, spriteSize, 256, 256);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled || button != 0) return false;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        for (Consumer<IconButtonWidget> listener : clickListeners) {
            listener.accept(this);
        }
        return true;
    }
}
