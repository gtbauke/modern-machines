package io.github.gtbauke.modernmachines.machine.recipe;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jspecify.annotations.NonNull;

public record AlloySmelterInput(List<ItemStack> items) implements RecipeInput {
    @Override
    public @NonNull ItemStack getItem(int index) {
        return (index >= 0 && index < items.size()) ? items.get(index) : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return items.size();
    }
}
