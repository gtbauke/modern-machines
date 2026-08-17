package io.github.gtbauke.modernmachines.modular.client;

import io.github.gtbauke.modernmachines.api.modular.MaterialStatsManager;
import io.github.gtbauke.modernmachines.api.modular.MaterialToolStats;
import io.github.gtbauke.modernmachines.api.modular.MaterialTrait;
import io.github.gtbauke.modernmachines.api.modular.ModifierEntry;
import io.github.gtbauke.modernmachines.api.modular.ModularToolData;
import io.github.gtbauke.modernmachines.api.modular.PartSlot;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Padding;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.windows.Window;
import io.github.gtbauke.modernmachines.modular.item.ModularToolItem;
import io.github.gtbauke.modernmachines.modular.menu.TinkeringTableMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TinkeringStatsWindow extends Window {
    public static final int WINDOW_WIDTH = 114;
    public static final int WINDOW_HEIGHT = 146;

    private final TinkeringTableMenu menu;

    public TinkeringStatsWindow(TinkeringTableMenu menu, Position initialPosition) {
        super(Component.literal("Tool Stats"), new Bounds(initialPosition, new Size(WINDOW_WIDTH, WINDOW_HEIGHT)), new Padding(0));
        this.menu = menu;

        setHasHeader(true);
        setHeaderHeight(18);
        setDraggable(true);
        setHasCloseButton(true);
        setVisible(false); // Closed initially

        setContent(new StatsContentElement());
    }

    private class StatsContentElement extends UIElement {
        public StatsContentElement() {
            super(new Bounds(Position.ZERO, new Size(WINDOW_WIDTH, WINDOW_HEIGHT - 18)));
        }

        @Override
        protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
            Font font = Minecraft.getInstance().font;
            int x = absoluteBounds.position().x() + 6;
            int y = absoluteBounds.position().y() + 4;
            int right = absoluteBounds.right() - 6;

            ItemStack outputStack = menu.slots.size() > 4 ? menu.slots.get(4).getItem() : ItemStack.EMPTY;
            ItemStack inputTool = menu.slots.size() > 0 ? menu.slots.get(0).getItem() : ItemStack.EMPTY;

            ItemStack targetStack = !outputStack.isEmpty() ? outputStack
                    : (inputTool.getItem() instanceof ModularToolItem ? inputTool : ItemStack.EMPTY);

            if (targetStack.isEmpty() || !(targetStack.getItem() instanceof ModularToolItem toolItem)) {
                // Empty state guidance
                graphics.text(font, Component.literal("No Tool Active"), x, y, 0xFF888888, false);
                graphics.text(font, Component.literal("Insert parts to"), x, y + 14, 0xFFAAAAAA, false);
                graphics.text(font, Component.literal("preview stats."), x, y + 26, 0xFFAAAAAA, false);
                return;
            }

            ModularToolData data = ModularToolItem.getData(targetStack);

            // 1. Tool Name Header
            Component name = targetStack.getHoverName();
            graphics.text(font, name, x, y, 0xFFFFFFFF, false);
            y += 12;

            // Horizontal Separator Line
            GUIRenderHelper.drawLine(graphics, new Position(x, y), new Position(right, y + 1), 0xFF555555);
            y += 4;

            // 2. Durability
            int maxDurability = ModularToolItem.getMaxDurability(targetStack);
            int currentDurability = maxDurability - ModularToolItem.getData(targetStack).damage();
            graphics.text(font, Component.literal("Durability:"), x, y, 0xFFAAAAAA, false);
            graphics.text(font, Component.literal(currentDurability + "/" + maxDurability), right - font.width(currentDurability + "/" + maxDurability), y, 0xFF55FF55, false);
            y += 10;

            // Mini Durability Bar
            int barWidth = right - x;
            int fillWidth = Math.max(1, (int) ((float) currentDurability / maxDurability * barWidth));
            GUIRenderHelper.drawRect(graphics, new Bounds(new Position(x, y), new Size(barWidth, 3)), 0xFF333333);
            GUIRenderHelper.drawRect(graphics, new Bounds(new Position(x, y), new Size(fillWidth, 3)), 0xFF55FF55);
            y += 6;

            // 3. Mining Speed & Damage
            float speed = ModularToolItem.getMiningSpeed(targetStack);
            graphics.text(font, Component.literal("Speed:"), x, y, 0xFFAAAAAA, false);
            graphics.text(font, Component.literal(String.format("%.1f", speed)), right - font.width(String.format("%.1f", speed)), y, 0xFF55FFFF, false);
            y += 11;

            float damage = ModularToolItem.getAttackDamage(targetStack, 1.0f);
            graphics.text(font, Component.literal("Damage:"), x, y, 0xFFAAAAAA, false);
            graphics.text(font, Component.literal(String.format("%.1f", damage)), right - font.width(String.format("%.1f", damage)), y, 0xFFFF5555, false);
            y += 11;

            // 4. Harvest Tier
            String tier = ModularToolItem.getHarvestTier(targetStack);
            graphics.text(font, Component.literal("Tier:"), x, y, 0xFFAAAAAA, false);
            String capitalizedTier = tier.isEmpty() ? "None" : Character.toUpperCase(tier.charAt(0)) + tier.substring(1);
            graphics.text(font, Component.literal(capitalizedTier), right - font.width(capitalizedTier), y, 0xFFFFAA00, false);
            y += 11;

            // 5. Modifier Slots
            int usedMods = data.getUsedModifierSlots();
            int maxMods = data.getMaxModifierSlots();
            graphics.text(font, Component.literal("Slots:"), x, y, 0xFFAAAAAA, false);
            graphics.text(font, Component.literal(usedMods + "/" + maxMods), right - font.width(usedMods + "/" + maxMods), y, 0xFFFF55FF, false);
            y += 12;

            // 6. Traits list preview
            List<MaterialTrait> traits = new ArrayList<>();
            for (Identifier matId : data.parts().values()) {
                MaterialStatsManager.getStats(matId).ifPresent(s -> traits.addAll(s.traits()));
            }

            if (!traits.isEmpty()) {
                graphics.text(font, Component.literal("Traits:"), x, y, 0xFFFFFF55, false);
                y += 10;
                for (int i = 0; i < Math.min(2, traits.size()); i++) {
                    MaterialTrait trait = traits.get(i);
                    graphics.text(font, Component.literal("• ").append(trait.getDisplayName()), x + 2, y, 0xFF55FFFF, false);
                    y += 9;
                }
            }
        }
    }
}
