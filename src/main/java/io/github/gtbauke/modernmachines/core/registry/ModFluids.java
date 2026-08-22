package io.github.gtbauke.modernmachines.core.registry;

import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.ModernMachines;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, ModernMachines.MOD_ID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, ModernMachines.MOD_ID);

    public static final Supplier<FluidType> STEAM_TYPE = FLUID_TYPES.register("steam", () ->
            new FluidType(FluidType.Properties.create()
                    .density(-100)
                    .temperature(373)
                    .viscosity(200)
                    .descriptionId("fluid_type.modernmachines.steam")));

//    public static final Supplier<FlowingFluid> STEAM_SOURCE = FLUIDS.register("steam", () ->
//            new BaseFlowingFluid.Source(ModFluids.STEAM_PROPERTIES));
//
//    public static final Supplier<FlowingFluid> STEAM_FLOWING = FLUIDS.register("steam_flowing", () ->
//            new BaseFlowingFluid.Flowing(ModFluids.STEAM_PROPERTIES));

//    public static final BaseFlowingFluid.Properties STEAM_PROPERTIES = new BaseFlowingFluid.Properties(
//            STEAM_TYPE,
//            STEAM_SOURCE,
//            STEAM_FLOWING
//    ).bucket(ModItems.STEAM_BUCKET);

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}
