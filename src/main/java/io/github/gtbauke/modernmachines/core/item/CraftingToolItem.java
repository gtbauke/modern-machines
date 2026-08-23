package io.github.gtbauke.modernmachines.core.item;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public class CraftingToolItem extends Item {

    public CraftingToolItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(@NonNull ItemInstance instance) {
        if (instance instanceof ItemStack stack) {
            int damage = stack.getDamageValue() + 1;

            if (damage < stack.getMaxDamage()) {
                ItemStack copy = stack.copy();
                copy.setDamageValue(damage);
                return ItemStackTemplate.fromStack(copy);
            }
        }

        return null;
    }
}
