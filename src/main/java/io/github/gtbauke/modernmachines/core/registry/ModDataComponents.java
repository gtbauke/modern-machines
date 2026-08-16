package io.github.gtbauke.modernmachines.core.registry;

import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.modular.ModularToolData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ModernMachines.MOD_ID);

    public static final Supplier<DataComponentType<ModularToolData>> MODULAR_TOOL_DATA =
            DATA_COMPONENT_TYPES.register("modular_tool_data", () -> DataComponentType.<ModularToolData>builder()
                    .persistent(ModularToolData.CODEC)
                    .networkSynchronized(ModularToolData.STREAM_CODEC)
                    .build());

    public static final Supplier<DataComponentType<Identifier>> MATERIAL_ID =
            DATA_COMPONENT_TYPES.register("material_id", () -> DataComponentType.<Identifier>builder()
                    .persistent(Identifier.CODEC)
                    .networkSynchronized(Identifier.STREAM_CODEC)
                    .build());

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
    }
}
