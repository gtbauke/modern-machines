package io.github.gtbauke.modernmachines.client.gui.editor.picker;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.editor.model.ElementDefinition;
import io.github.gtbauke.modernmachines.client.gui.editor.model.TabDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TabTemplateModal extends UIElement {
    public static final int MODAL_WIDTH = 190;
    public static final int MODAL_HEIGHT = 160;

    private final Consumer<TabDefinition> onSelect;
    private final Runnable onClose;
    private final List<TabPreset> presets = new ArrayList<>();
    private final List<ClickableAction> actions = new ArrayList<>();

    public record ClickableAction(Bounds bounds, Runnable onClick) {}
    public record TabPreset(String name, String description, String iconItem, java.util.function.Supplier<TabDefinition> creator) {}

    public TabTemplateModal(Consumer<TabDefinition> onSelect, Runnable onClose) {
        super(new Bounds(Position.ZERO, new Size(MODAL_WIDTH, MODAL_HEIGHT)));
        this.onSelect = onSelect;
        this.onClose = onClose;

        registerPresets();
    }

    private void registerPresets() {
        // 1. Blank Custom Tab
        presets.add(new TabPreset(
            "Blank Window",
            "Empty customizable window",
            "minecraft:paper",
            () -> {
                var tab = new TabDefinition();
                tab.setTitle("Custom Window");
                tab.setTooltip("Custom Tab");
                tab.setIconItem("minecraft:paper");
                tab.setSide("LEFT");
                tab.setWindowWidth(120);
                tab.setWindowHeight(100);
                return tab;
            }
        ));

        // 2. Upgrades Tab
        presets.add(new TabPreset(
            "Upgrades Window",
            "2x2 Upgrade Slots (Slots 4..7)",
            "modernmachines:speed_upgrade",
            () -> {
                var tab = new TabDefinition();
                tab.setTitle("Upgrades");
                tab.setTooltip("Upgrades");
                tab.setIconItem("modernmachines:speed_upgrade");
                tab.setSide("LEFT");
                tab.setWindowWidth(80);
                tab.setWindowHeight(72);

                var grid = new ElementDefinition(ElementDefinition.ElementType.SLOT_GRID);
                grid.setSlotIndex(4);
                grid.setGridRows(2);
                grid.setGridCols(2);
                grid.setGap(4);
                grid.setX((80 - 40) / 2);
                grid.setY(10);
                tab.addElement(grid);

                return tab;
            }
        ));

        // 3. Side Configuration Tab
        presets.add(new TabPreset(
            "Side Config Window",
            "6-Face Cross Grid & Auto IO",
            "modernmachines:engineers_tablet",
            () -> {
                var tab = new TabDefinition();
                tab.setTitle("Side Configuration");
                tab.setTooltip("Side Configuration");
                tab.setIconItem("modernmachines:engineers_tablet");
                tab.setSide("LEFT");
                tab.setWindowWidth(110);
                tab.setWindowHeight(142);

                var sideGrid = new ElementDefinition(ElementDefinition.ElementType.SIDE_CONFIG_GRID);
                sideGrid.setWidth(100);
                sideGrid.setHeight(118);
                sideGrid.setX((110 - 100) / 2);
                sideGrid.setY(3);
                tab.addElement(sideGrid);

                return tab;
            }
        ));

        // 4. Stats / Info Window
        presets.add(new TabPreset(
            "Stats / Info Window",
            "Machine statistics & data",
            "minecraft:writable_book",
            () -> {
                var tab = new TabDefinition();
                tab.setTitle("Machine Stats");
                tab.setTooltip("Statistics");
                tab.setIconItem("minecraft:writable_book");
                tab.setSide("RIGHT");
                tab.setWindowWidth(120);
                tab.setWindowHeight(90);

                var label = new ElementDefinition(ElementDefinition.ElementType.TEXT);
                label.setText("Status: Active");
                label.setX(8);
                label.setY(8);
                tab.addElement(label);

                return tab;
            }
        ));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }

        var clickPos = new Position((int) mouseX, (int) mouseY);
        var absBounds = getAbsoluteBounds();
        if (!absBounds.contains(clickPos)) {
            if (onClose != null) {
                onClose.run();
            }

            return true;
        }

        for (var action : actions) {
            if (action.bounds().contains(clickPos)) {
                action.onClick().run();
                return true;
            }
        }

        return true;
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        actions.clear();

        // Dark modal backdrop
        graphics.fill(0, 0, Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight(), 0x88000000);

        // Modal Frame
        GUIRenderHelper.drawOreUIPanel(graphics, absoluteBounds);

        var font = Minecraft.getInstance().font;
        int x = absoluteBounds.position().x();
        int y = absoluteBounds.position().y();
        int right = absoluteBounds.right();

        // Header
        graphics.fill(x + 1, y + 1, right - 1, y + 16, GUIRenderHelper.ORE_BG_PRIMARY);
        GUIRenderHelper.drawLine(graphics, new Position(x, y + 16), new Position(right, y + 17), GUIRenderHelper.ORE_BORDER_DARK);
        graphics.text(font, Component.literal("Select Tab Template"), x + 6, y + 4, GUIRenderHelper.ORE_TEXT_TITLE, true);

        // Close [x] button
        var closeBounds = new Bounds(new Position(right - 15, y + 2), new Size(12, 12));
        boolean closeHovered = closeBounds.contains(new Position(mouseX, mouseY));
        graphics.fill(closeBounds.position().x(), closeBounds.position().y(), closeBounds.right(), closeBounds.bottom(), closeHovered ? 0xFF992222 : 0x00000000);
        graphics.text(font, Component.literal("x"), right - 12, y + 4, 0xFFFFFFFF, false);
        actions.add(new ClickableAction(closeBounds, () -> {
            if (onClose != null) {
                onClose.run();
            }
        }));

        // Preset items list
        int curY = y + 22;
        int cardW = MODAL_WIDTH - 12;
        int cardH = 28;

        for (var preset : presets) {
            var cardBounds = new Bounds(new Position(x + 6, curY), new Size(cardW, cardH));
            boolean hovered = cardBounds.contains(new Position(mouseX, mouseY));

            GUIRenderHelper.drawOreUIButton(graphics, cardBounds, hovered, false, false);

            // Icon
            try {
                var itemHolder = BuiltInRegistries.ITEM.get(Identifier.parse(preset.iconItem()));
                if (itemHolder.isPresent()) {
                    graphics.fakeItem(new ItemStack(itemHolder.get().value()), x + 9, curY + 6);
                }
            } catch (Throwable ignored) {
            }

            // Title & Description
            graphics.text(font, Component.literal(preset.name()), x + 30, curY + 4, hovered ? GUIRenderHelper.ORE_TEXT_TITLE : 0xFFE0E0E0, false);
            graphics.text(font, Component.literal("§8" + preset.description()), x + 30, curY + 15, 0xFF888888, false);

            actions.add(new ClickableAction(cardBounds, () -> {
                var tab = preset.creator().get();
                if (onSelect != null) {
                    onSelect.accept(tab);
                }
                if (onClose != null) {
                    onClose.run();
                }
            }));

            curY += cardH + 4;
        }
    }
}
