package io.github.gtbauke.modernmachines.machine.recipe;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.gtbauke.modernmachines.core.registry.ModRecipeTypes;
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

public record CrushingRecipe(
        SizedIngredient input,
        ItemStackTemplate result,
        Optional<ItemStackTemplate> byproduct,
        float byproductChance,
        int duration,
        int steamCost
) implements Recipe<CrushingInput> {

    @Override
    public boolean matches(CrushingInput recipeInput, @NonNull Level level) {
        ItemStack stack = recipeInput.getItem(0);
        if (stack.isEmpty()) {
            return false;
        }
        return input.ingredient().test(stack) && stack.getCount() >= input.count();
    }

    @Override
    public @NonNull ItemStack assemble(CrushingInput recipeInput) {
        return result.create();
    }

    public ItemStack assembleByproduct() {
        return byproduct.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);
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
    public @NonNull RecipeSerializer<? extends Recipe<CrushingInput>> getSerializer() {
        return ModRecipeTypes.CRUSHING_SERIALIZER.get();
    }

    @Override
    public @NonNull RecipeType<? extends Recipe<CrushingInput>> getType() {
        return ModRecipeTypes.CRUSHING.get();
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
    }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    public static final MapCodec<CrushingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    SizedIngredient.NESTED_CODEC.fieldOf("ingredient").forGetter(CrushingRecipe::input),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(CrushingRecipe::result),
                    ItemStackTemplate.CODEC.optionalFieldOf("byproduct").forGetter(CrushingRecipe::byproduct),
                    Codec.FLOAT.optionalFieldOf("byproduct_chance", 0.0f).forGetter(CrushingRecipe::byproductChance),
                    Codec.INT.optionalFieldOf("duration", 200).forGetter(CrushingRecipe::duration),
                    Codec.INT.optionalFieldOf("steam", 4000).forGetter(CrushingRecipe::steamCost)
            ).apply(instance, CrushingRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CrushingRecipe> STREAM_CODEC = StreamCodec.composite(
            SizedIngredient.STREAM_CODEC, CrushingRecipe::input,
            ItemStackTemplate.STREAM_CODEC, CrushingRecipe::result,
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), CrushingRecipe::byproduct,
            ByteBufCodecs.FLOAT, CrushingRecipe::byproductChance,
            ByteBufCodecs.VAR_INT, CrushingRecipe::duration,
            ByteBufCodecs.VAR_INT, CrushingRecipe::steamCost,
            CrushingRecipe::new
    );
}
