package io.github.gtbauke.modernmachines.client.gui.window;

import io.github.gtbauke.modernmachines.client.gui.declarative.WindowControls;
import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public class FloatingTabWindow extends WindowWidget {
    private final WindowWidget parentMainWindow;
    private final int iconU;
    private final int iconV;
    private final boolean leftSided;

    public FloatingTabWindow(Component title, int iconU, int iconV, WindowWidget parentMainWindow) {
        this(title, iconU, iconV, 140, 80, parentMainWindow, true);
        this.setAutoSize(true);
    }

    public FloatingTabWindow(Component title, int iconU, int iconV, WindowWidget parentMainWindow, boolean leftSided) {
        this(title, iconU, iconV, 140, 80, parentMainWindow, leftSided);
        this.setAutoSize(true);
    }

    public FloatingTabWindow(Component title, int iconU, int iconV, int windowWidth, WindowWidget parentMainWindow, boolean leftSided) {
        this(title, iconU, iconV, windowWidth, 80, parentMainWindow, leftSided);
    }

    public FloatingTabWindow(Component title, int iconU, int iconV, int windowWidth, int windowHeight, WindowWidget parentMainWindow, boolean leftSided) {
        super(title, windowWidth, windowHeight);
        this.parentMainWindow = parentMainWindow;
        this.iconU = iconU;
        this.iconV = iconV;
        this.leftSided = leftSided;
        this.draggable = true;
        this.autoWidth = true;
        this.autoHeight = true;
        this.visible = false; // Closed by default until tab button is clicked

        // Pad header on left by 22px so the title label does not overlap the 16x16 icon
        this.headerContainer.getFlexNode().setPadding(FlexInsets.of(2, 6, 2, 22));

        // Configure standard floating controls (Dock + Close)
        setWindowControls(WindowControls.standardFloating(this::snapToMainWindow, () -> setVisible(false)));
    }

    public void snapToMainWindow() {
        if (parentMainWindow != null) {
            int targetX = leftSided ? (parentMainWindow.getBounds().x() - this.windowWidth - 4)
                                    : (parentMainWindow.getBounds().x() + parentMainWindow.getBounds().width() + 4);
            int targetY = parentMainWindow.getBounds().y();
            setPosition(targetX, targetY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        if (autoWidth || autoHeight) {
            pack();
        }

        Bounds b = getBounds();

        // 1. Drop shadow
        GuiRenderHelper.drawDropShadow(graphics, b, theme.dropShadowColor(), 4);

        // 2. Solid opaque window background & frame
        graphics.fill(b.x() + 2, b.y() + 2, b.x() + b.width() - 2, b.y() + b.height() - 2, 0xFF181820);
        NineSliceRenderer.drawNineSlice(graphics, NineSliceRenderer.WINDOW_DARK, b);

        // 3. Header bar separator
        GuiRenderHelper.drawHorizontalLine(graphics, b.x() + 4, b.x() + b.width() - 4, b.y() + 20, 0xFF353545);

        // 4. Header Tab Icon (16x16)
        int iconX = b.x() + 4;
        int iconY = b.y() + 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, iconX, iconY, (float) iconU, (float) iconV, 16, 16, 256, 256);

        // 5. Content background
        contentContainer.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
        headerContainer.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
    }
}
