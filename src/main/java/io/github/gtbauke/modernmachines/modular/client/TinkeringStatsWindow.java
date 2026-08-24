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
            var font = Minecraft.getInstance().font;
            int x = absoluteBounds.position().x() + 6;
            int y = absoluteBounds.position().y() + 4;
            int right = absoluteBounds.right() - 6;

            var outputStack = menu.slots.size() > 4 ? menu.slots.get(4).getItem() : ItemStack.EMPTY;
            var inputTool = menu.slots.size() > 0 ? menu.slots.get(0).getItem() : ItemStack.EMPTY;

            var targetStack = !outputStack.isEmpty() ? outputStack
                    : (inputTool.getItem() instanceof ModularToolItem ? inputTool : ItemStack.EMPTY);

            if (targetStack.isEmpty() || !(targetStack.getItem() instanceof ModularToolItem toolItem)) {
                graphics.text(font, Component.literal("No Tool Active"), x, y, GUIRenderHelper.ORE_TEXT_MUTED, false);
                graphics.text(font, Component.literal("Insert parts to"), x, y + 14, GUIRenderHelper.ORE_TEXT_DARK, false);
                graphics.text(font, Component.literal("preview stats."), x, y + 26, GUIRenderHelper.ORE_TEXT_DARK, false);
                return;
            }

            var data = ModularToolItem.getData(targetStack);

            var name = targetStack.getHoverName();
            graphics.text(font, name, x, y, GUIRenderHelper.ORE_TEXT_TITLE, true);
            y += 12;

            GUIRenderHelper.drawLine(graphics, new Position(x, y), new Position(right, y + 1), GUIRenderHelper.ORE_BORDER_LIGHT);
            y += 4;

            int maxDurability = ModularToolItem.getMaxDurability(targetStack);
            int currentDurability = maxDurability - ModularToolItem.getData(targetStack).damage();
            graphics.text(font, Component.literal("Durability:"), x, y, GUIRenderHelper.ORE_TEXT_MUTED, false);
            graphics.text(font, Component.literal(currentDurability + "/" + maxDurability), right - font.width(currentDurability + "/" + maxDurability), y, GUIRenderHelper.ORE_GREEN_HOVER, false);
            y += 10;

            int barWidth = right - x;
            int fillWidth = Math.max(1, (int) ((float) currentDurability / maxDurability * barWidth));
            GUIRenderHelper.drawRect(graphics, new Bounds(new Position(x, y), new Size(barWidth, 3)), GUIRenderHelper.ORE_SLOT_BG);
            GUIRenderHelper.drawRect(graphics, new Bounds(new Position(x, y), new Size(fillWidth, 3)), GUIRenderHelper.ORE_GREEN_PRIMARY);
            y += 6;

            float speed = ModularToolItem.getMiningSpeed(targetStack);
            graphics.text(font, Component.literal("Speed:"), x, y, GUIRenderHelper.ORE_TEXT_MUTED, false);
            graphics.text(font, Component.literal(String.format("%.1f", speed)), right - font.width(String.format("%.1f", speed)), y, 0xFF55FFFF, false);
            y += 11;

            float damage = ModularToolItem.getAttackDamage(targetStack, 1.0f);
            graphics.text(font, Component.literal("Damage:"), x, y, GUIRenderHelper.ORE_TEXT_MUTED, false);
            graphics.text(font, Component.literal(String.format("%.1f", damage)), right - font.width(String.format("%.1f", damage)), y, 0xFFFF5555, false);
            y += 11;

            var tier = ModularToolItem.getHarvestTier(targetStack);
            graphics.text(font, Component.literal("Tier:"), x, y, GUIRenderHelper.ORE_TEXT_MUTED, false);
            var capitalizedTier = tier.isEmpty() ? "None" : Character.toUpperCase(tier.charAt(0)) + tier.substring(1);
            graphics.text(font, Component.literal(capitalizedTier), right - font.width(capitalizedTier), y, 0xFFFFAA00, false);
            y += 11;

            int usedMods = data.getUsedModifierSlots();
            int maxMods = data.getMaxModifierSlots();
            graphics.text(font, Component.literal("Slots:"), x, y, GUIRenderHelper.ORE_TEXT_MUTED, false);
            graphics.text(font, Component.literal(usedMods + "/" + maxMods), right - font.width(usedMods + "/" + maxMods), y, 0xFFFF55FF, false);
            y += 12;

            var traits = new ArrayList<MaterialTrait>();
            for (var matId : data.parts().values()) {
                MaterialStatsManager.getStats(matId).ifPresent(s -> traits.addAll(s.traits()));
            }

            if (!traits.isEmpty()) {
                graphics.text(font, Component.literal("Traits:"), x, y, 0xFFFFFF55, false);
                y += 10;
                for (int i = 0; i < Math.min(2, traits.size()); i++) {
                    var trait = traits.get(i);
                    graphics.text(font, Component.literal("• ").append(trait.getDisplayName()), x + 2, y, 0xFF55FFFF, false);
                    y += 9;
                }
            }
        }
    }
}
