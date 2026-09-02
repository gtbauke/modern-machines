package io.github.gtbauke.modernmachines.core.registry;

import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import io.github.gtbauke.modernmachines.api.modular.trait.ToolTraitRegistry;
import io.github.gtbauke.modernmachines.modular.trait.ConductiveTrait;
import io.github.gtbauke.modernmachines.modular.trait.DenseTrait;
import io.github.gtbauke.modernmachines.modular.trait.EcologicalTrait;
import io.github.gtbauke.modernmachines.modular.trait.HeavyTrait;
import io.github.gtbauke.modernmachines.modular.trait.HellforgedTrait;
import io.github.gtbauke.modernmachines.modular.trait.KeenEdgeTrait;
import io.github.gtbauke.modernmachines.modular.trait.LightweightTrait;
import io.github.gtbauke.modernmachines.modular.trait.LuckyTrait;
import io.github.gtbauke.modernmachines.modular.trait.OverclockedTrait;
import io.github.gtbauke.modernmachines.modular.trait.ProsperityTrait;
import io.github.gtbauke.modernmachines.modular.trait.PurifyingTrait;
import io.github.gtbauke.modernmachines.modular.trait.RadioactiveTrait;
import io.github.gtbauke.modernmachines.modular.trait.ReinforcedTrait;
import io.github.gtbauke.modernmachines.modular.trait.ResilientTrait;
import io.github.gtbauke.modernmachines.modular.trait.SharpTrait;
import io.github.gtbauke.modernmachines.modular.trait.SturdyTrait;
import io.github.gtbauke.modernmachines.modular.trait.TemperedTrait;
import io.github.gtbauke.modernmachines.modular.trait.ThermalTrait;
import io.github.gtbauke.modernmachines.modular.trait.UnyieldingTrait;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ModTraits {
    public static final ResourceKey<Registry<ToolTrait>> TRAIT_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "tool_traits"));

    public static final Registry<ToolTrait> REGISTRY =
            new RegistryBuilder<>(TRAIT_REGISTRY_KEY).sync(true).create();

    public static final DeferredRegister<ToolTrait> TRAITS =
            DeferredRegister.create(TRAIT_REGISTRY_KEY, ModernMachines.MOD_ID);

    public static final Supplier<ToolTrait> ECOLOGICAL = register("ecological", EcologicalTrait::new);
    public static final Supplier<ToolTrait> DENSE = register("dense", DenseTrait::new);
    public static final Supplier<ToolTrait> REINFORCED = register("reinforced", ReinforcedTrait::new);
    public static final Supplier<ToolTrait> PROSPERITY = register("prosperity", ProsperityTrait::new);
    public static final Supplier<ToolTrait> KEEN_EDGE = register("keen_edge", KeenEdgeTrait::new);
    public static final Supplier<ToolTrait> HELLFORGED = register("hellforged", HellforgedTrait::new);
    public static final Supplier<ToolTrait> CONDUCTIVE = register("conductive", ConductiveTrait::new);
    public static final Supplier<ToolTrait> LIGHTWEIGHT = register("lightweight", LightweightTrait::new);
    public static final Supplier<ToolTrait> STURDY = register("sturdy", SturdyTrait::new);
    public static final Supplier<ToolTrait> SHARP = register("sharp", SharpTrait::new);
    public static final Supplier<ToolTrait> PURIFYING = register("purifying", PurifyingTrait::new);
    public static final Supplier<ToolTrait> HEAVY = register("heavy", HeavyTrait::new);
    public static final Supplier<ToolTrait> RESILIENT = register("resilient", ResilientTrait::new);
    public static final Supplier<ToolTrait> TEMPERED = register("tempered", TemperedTrait::new);
    public static final Supplier<ToolTrait> OVERCLOCKED = register("overclocked", OverclockedTrait::new);
    public static final Supplier<ToolTrait> THERMAL = register("thermal", ThermalTrait::new);
    public static final Supplier<ToolTrait> UNYIELDING = register("unyielding", UnyieldingTrait::new);
    public static final Supplier<ToolTrait> RADIOACTIVE = register("radioactive", RadioactiveTrait::new);
    public static final Supplier<ToolTrait> LUCKY = register("lucky", LuckyTrait::new);

    private static <T extends ToolTrait> Supplier<T> register(String name, java.util.function.Function<Identifier, T> factory) {
        var id = Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, name);
        return TRAITS.register(name, () -> {
            var trait = factory.apply(id);
            ToolTraitRegistry.register(trait);
            return trait;
        });
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener((NewRegistryEvent event) -> event.register(REGISTRY));
        TRAITS.register(modEventBus);
    }
}
