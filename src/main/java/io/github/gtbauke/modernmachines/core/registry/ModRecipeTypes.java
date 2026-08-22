package io.github.gtbauke.modernmachines.core.registry;

import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.machine.recipe.AlloySmeltingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ModernMachines.MOD_ID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ModernMachines.MOD_ID);

    public static final Supplier<RecipeType<AlloySmeltingRecipe>> ALLOY_SMELTING =
            RECIPE_TYPES.register("alloy_smelting", () -> RecipeType.simple(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "alloy_smelting")));

    public static final Supplier<RecipeSerializer<AlloySmeltingRecipe>> ALLOY_SMELTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("alloy_smelting", () -> new RecipeSerializer<>(AlloySmeltingRecipe.CODEC, AlloySmeltingRecipe.STREAM_CODEC));

    public static final Supplier<RecipeType<io.github.gtbauke.modernmachines.machine.recipe.CrushingRecipe>> CRUSHING =
            RECIPE_TYPES.register("crushing", () -> RecipeType.simple(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "crushing")));

    public static final Supplier<RecipeSerializer<io.github.gtbauke.modernmachines.machine.recipe.CrushingRecipe>> CRUSHING_SERIALIZER =
            RECIPE_SERIALIZERS.register("crushing", () -> new RecipeSerializer<>(io.github.gtbauke.modernmachines.machine.recipe.CrushingRecipe.CODEC, io.github.gtbauke.modernmachines.machine.recipe.CrushingRecipe.STREAM_CODEC));

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
