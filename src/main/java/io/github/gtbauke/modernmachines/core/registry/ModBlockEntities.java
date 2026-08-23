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

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Alloy Smelter
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ALLOY_SMELTER.get(),
                WorldlyContainerWrapper::new
        );

        event.registerBlock(
                Capabilities.Item.BLOCK,
                (level, pos, state, be, side) -> {
                    BlockPos controllerPos = pos.above();
                    return level.getCapability(Capabilities.Item.BLOCK, controllerPos, side);
                },
                ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get()
        );
    }
}
