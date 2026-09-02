package io.github.gtbauke.modernmachines.api.client.gui.screen;

import io.github.gtbauke.modernmachines.api.client.gui.core.Position;
import io.github.gtbauke.modernmachines.api.client.gui.elements.UIElement;
import io.github.gtbauke.modernmachines.mixin.SlotAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NonNull;

public abstract class ModularContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    public static final int DEFAULT_IMAGE_WIDTH = 176;
    public static final int DEFAULT_IMAGE_HEIGHT = 166;

    protected UIElement root;

    public ModularContainerScreen(T menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
        super(menu, inventory, title, imageWidth, imageHeight);
        this.titleLabelX = -9999;
        this.titleLabelY = -9999;
        this.inventoryLabelX = -9999;
        this.inventoryLabelY = -9999;
    }

    public ModularContainerScreen(T menu, Inventory inventory, Component title) {
        this(menu, inventory, title, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
    }

    public void hideAllSlots() {
        for (var slot : this.menu.slots) {
            if (slot instanceof SlotAccessor accessor) {
                try {
                    accessor.setX(-9999);
                    accessor.setY(-9999);
                } catch (Throwable ignored) {
                }
            }
        }
    }

    public void recalculateLayout() {
        if (this.root != null) {
            this.root.setPosition(new Position(this.leftPos, this.topPos));
            this.root.calculateSize();
            this.root.calculateLayout();
        }
    }

    @Override
    public void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        hideAllSlots();

        this.root = buildContent();
        if (this.root != null) {
            this.root.setPosition(new Position(this.leftPos, this.topPos));
            this.root.calculateSize();
            this.root.calculateLayout();
        }
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        if (this.root != null) {
            this.root.render(graphics, new Position(mouseX, mouseY), partialTick);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.root != null && this.root.mouseClicked(event.x(), event.y(), event.button())) {
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.root != null && this.root.mouseReleased(event.x(), event.y(), event.button())) {
            return true;
        }

        return super.mouseReleased(event);
    }

    public abstract UIElement buildContent();
}
