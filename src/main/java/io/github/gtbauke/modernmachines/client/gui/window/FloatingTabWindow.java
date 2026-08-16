package io.github.gtbauke.modernmachines.client.gui.window;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.ButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class FloatingTabWindow extends WindowWidget {
    private final WindowWidget parentMainWindow;
    private final int iconU;
    private final int iconV;
    private final boolean leftSided;

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
        this.autoHeight = true;
        this.visible = false; // Closed by default until tab button is clicked

        // Pad header on left by 22px so the title label does not overlap the 16x16 icon
        this.headerContainer.getFlexNode().setPadding(io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets.of(2, 6, 2, 22));

        // Configure Header with Dock and Close action buttons
        // 1. Dock / Reset Button [⤢] (snaps back adjacent to main window)
        io.github.gtbauke.modernmachines.client.gui.widget.HeaderControlButtonWidget dockBtn =
                new io.github.gtbauke.modernmachines.client.gui.widget.HeaderControlButtonWidget("⤢", 0xFF52A9FF, btn -> snapToMainWindow())
                        .setTooltip(Component.literal("Dock adjacent to main window").withStyle(ChatFormatting.GRAY));
        this.headerButtonsContainer.addChild(dockBtn);

        // 2. Close Button [✕]
        io.github.gtbauke.modernmachines.client.gui.widget.HeaderControlButtonWidget closeBtn =
                new io.github.gtbauke.modernmachines.client.gui.widget.HeaderControlButtonWidget("✕", 0xFFFF5555, btn -> setVisible(false))
                        .setTooltip(Component.literal("Close Window").withStyle(ChatFormatting.GRAY));
        this.headerButtonsContainer.addChild(closeBtn);
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

        if (autoHeight) {
            pack();
        }

        Bounds b = getBounds();

        // 1. Drop shadow
        io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper.drawDropShadow(graphics, b, theme.dropShadowColor(), 4);

        // 2. Solid opaque window background & frame
        graphics.fill(b.x() + 2, b.y() + 2, b.x() + b.width() - 2, b.y() + b.height() - 2, 0xFF181820);
        NineSliceRenderer.drawNineSlice(graphics, NineSliceRenderer.WINDOW_DARK, b);

        // 3. Header bar separator
        io.github.gtbauke.modernmachines.client.gui.render.GuiRenderHelper.drawHorizontalLine(graphics, b.x() + 4, b.x() + b.width() - 4, b.y() + 20, 0xFF353545);

        // 4. Header Tab Icon (16x16)
        int iconX = b.x() + 4;
        int iconY = b.y() + 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, iconX, iconY, (float) iconU, (float) iconV, 16, 16, 256, 256);

        // 5. Content background
        contentContainer.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
        headerContainer.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
    }
}
