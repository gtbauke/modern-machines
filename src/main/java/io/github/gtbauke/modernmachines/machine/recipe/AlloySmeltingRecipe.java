package io.github.gtbauke.modernmachines.machine.recipe;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.gtbauke.modernmachines.core.registry.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.NonNull;

public record AlloySmeltingRecipe(List<SizedIngredient> inputs, ItemStackTemplate result, int cookingTime, int energyCost,
                                  float experience) implements Recipe<AlloySmelterInput> {

    @Override
    public boolean matches(AlloySmelterInput input, @NonNull Level level) {
        NonNullList<ItemStack> available = NonNullList.create();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                available.add(stack.copy());
            }
        }

        if (available.isEmpty()) {
            return false;
        }

        for (SizedIngredient sizedIngredient : inputs) {
            int needed = sizedIngredient.count();
            for (ItemStack stack : available) {
                if (!stack.isEmpty() && sizedIngredient.ingredient().test(stack)) {
                    int take = Math.min(needed, stack.getCount());
                    stack.shrink(take);
                    needed -= take;
                    if (needed <= 0) break;
                }
            }
            if (needed > 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NonNull ItemStack assemble(AlloySmelterInput input) {
        return result.create();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull String group() {
        return "";
    }

    @Override
    public @NonNull RecipeSerializer<? extends Recipe<AlloySmelterInput>> getSerializer() {
        return ModRecipeTypes.ALLOY_SMELTING_SERIALIZER.get();
    }

    @Override
    public @NonNull RecipeType<? extends Recipe<AlloySmelterInput>> getType() {
        return ModRecipeTypes.ALLOY_SMELTING.get();
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
    }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    public static final MapCodec<AlloySmeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    SizedIngredient.NESTED_CODEC.listOf().fieldOf("inputs").forGetter(AlloySmeltingRecipe::inputs),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(AlloySmeltingRecipe::result),
                    Codec.INT.optionalFieldOf("cooking_time", 200).forGetter(AlloySmeltingRecipe::cookingTime),
                    Codec.INT.optionalFieldOf("energy", 4000).forGetter(AlloySmeltingRecipe::energyCost),
                    Codec.FLOAT.optionalFieldOf("experience", 0.7f).forGetter(AlloySmeltingRecipe::experience)
            ).apply(instance, AlloySmeltingRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AlloySmeltingRecipe> STREAM_CODEC = StreamCodec.composite(
            SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()),
            AlloySmeltingRecipe::inputs,
            ItemStackTemplate.STREAM_CODEC,
            AlloySmeltingRecipe::result,
            ByteBufCodecs.VAR_INT,
            AlloySmeltingRecipe::cookingTime,
            ByteBufCodecs.VAR_INT,
            AlloySmeltingRecipe::energyCost,
            ByteBufCodecs.FLOAT,
            AlloySmeltingRecipe::experience,
            AlloySmeltingRecipe::new
    );
}
