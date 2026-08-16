package io.github.gtbauke.modernmachines.client.gui.declarative;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.layout.Length;
import io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Divider extends UiWidget {
    public static final int DEFAULT_COLOR = 0xFF333740;
    private final boolean horizontal;
    private final int thickness;
    private final int color;

    public Divider(boolean horizontal, int thickness, int color) {
        this.horizontal = horizontal;
        this.thickness = thickness;
        this.color = color;

        if (horizontal) {
            this.width(Length.matchParent());
            this.height(thickness);
            this.margin(FlexInsets.symmetric(2, 0));
        } else {
            this.width(thickness);
            this.height(Length.matchParent());
            this.margin(FlexInsets.symmetric(0, 2));
        }
    }

    public static Divider horizontal() {
        return new Divider(true, 1, DEFAULT_COLOR);
    }

    public static Divider horizontal(int thickness, int color) {
        return new Divider(true, thickness, color);
    }

    public static Divider vertical() {
        return new Divider(false, 1, DEFAULT_COLOR);
    }

    public static Divider vertical(int thickness, int color) {
        return new Divider(false, thickness, color);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        Bounds b = getBounds();
        GuiRenderHelper.drawRect(graphics, b.x(), b.y(), b.width(), b.height(), color);
    }
}
