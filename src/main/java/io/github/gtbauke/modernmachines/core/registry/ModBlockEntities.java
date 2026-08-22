package io.github.gtbauke.modernmachines.core.registry;

import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.machine.blockentity.AlloySmelterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ModernMachines.MOD_ID);

    public static final Supplier<BlockEntityType<AlloySmelterBlockEntity>> ALLOY_SMELTER =
            BLOCK_ENTITIES.register("alloy_smelter", () -> new BlockEntityType<>(
                    AlloySmelterBlockEntity::new,
                    java.util.Set.of(ModBlocks.ALLOY_SMELTER.get())
            ));

//    public static final Supplier<BlockEntityType<SolidFuelBoilerBlockEntity>> SOLID_FUEL_BOILER =
//            BLOCK_ENTITIES.register("solid_fuel_boiler", () -> new BlockEntityType<>(
//                    SolidFuelBoilerBlockEntity::new,
//                    java.util.Set.of(ModBlocks.SOLID_FUEL_BOILER.get())
//            ));

//    public static final Supplier<BlockEntityType<SteamTurbineBlockEntity>> STEAM_TURBINE =
//            BLOCK_ENTITIES.register("steam_turbine", () -> new BlockEntityType<>(
//                    SteamTurbineBlockEntity::new,
//                    java.util.Set.of(ModBlocks.STEAM_TURBINE.get())
//            ));
//
//    public static final Supplier<BlockEntityType<SteamCrusherBlockEntity>> STEAM_CRUSHER =
//            BLOCK_ENTITIES.register("steam_crusher", () -> new BlockEntityType<>(
//                    SteamCrusherBlockEntity::new,
//                    java.util.Set.of(ModBlocks.STEAM_CRUSHER.get())
//            ));
//
//    public static final Supplier<BlockEntityType<SteamAlloySmelterBlockEntity>> STEAM_ALLOY_SMELTER =
//            BLOCK_ENTITIES.register("steam_alloy_smelter", () -> new BlockEntityType<>(
//                    SteamAlloySmelterBlockEntity::new,
//                    java.util.Set.of(ModBlocks.STEAM_ALLOY_SMELTER.get())
//            ));
//
//    public static final Supplier<BlockEntityType<BronzeFluidTankBlockEntity>> BRONZE_FLUID_TANK =
//            BLOCK_ENTITIES.register("bronze_fluid_tank", () -> new BlockEntityType<>(
//                    BronzeFluidTankBlockEntity::new,
//                    java.util.Set.of(ModBlocks.BRONZE_FLUID_TANK.get())
//            ));
//
//    public static final Supplier<BlockEntityType<BronzeFluidPipeBlockEntity>> BRONZE_FLUID_PIPE =
//            BLOCK_ENTITIES.register("bronze_fluid_pipe", () -> new BlockEntityType<>(
//                    BronzeFluidPipeBlockEntity::new,
//                    java.util.Set.of(ModBlocks.BRONZE_FLUID_PIPE.get())
//            ));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Alloy Smelter
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ALLOY_SMELTER.get(),
                (be, side) -> new WorldlyContainerWrapper(be, side)
        );

        event.registerBlock(
                Capabilities.Item.BLOCK,
                (level, pos, state, be, side) -> {
                    BlockPos controllerPos = pos.above();
                    return level.getCapability(Capabilities.Item.BLOCK, controllerPos, side);
                },
                ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get()
        );

//        // Solid Fuel Boiler
//        event.registerBlockEntity(
//                Capabilities.Item.BLOCK,
//                SOLID_FUEL_BOILER.get(),
//                (be, side) -> new WorldlyContainerWrapper(be, side)
//        );
//
//        // Steam Turbine
//        event.registerBlockEntity(
//                Capabilities.Item.BLOCK,
//                STEAM_TURBINE.get(),
//                (be, side) -> new WorldlyContainerWrapper(be, side)
//        );
//
//        // Steam Crusher
//        event.registerBlockEntity(
//                Capabilities.Item.BLOCK,
//                STEAM_CRUSHER.get(),
//                (be, side) -> new WorldlyContainerWrapper(be, side)
//        );
//
//        // Steam Alloy Smelter
//        event.registerBlockEntity(
//                Capabilities.Item.BLOCK,
//                STEAM_ALLOY_SMELTER.get(),
//                (be, side) -> new WorldlyContainerWrapper(be, side)
//        );
    }
}
