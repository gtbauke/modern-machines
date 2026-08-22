package io.github.gtbauke.modernmachines.core.registry;

import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.machine.menu.AlloySmelterMenu;
import io.github.gtbauke.modernmachines.modular.menu.PartBuilderMenu;
import io.github.gtbauke.modernmachines.modular.menu.TinkeringTableMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, ModernMachines.MOD_ID);

    public static final Supplier<MenuType<PartBuilderMenu>> PART_BUILDER =
            MENUS.register("part_builder", () -> new MenuType<>(PartBuilderMenu::new, FeatureFlagSet.of()));

    public static final Supplier<MenuType<TinkeringTableMenu>> TINKERING_TABLE =
            MENUS.register("tinkering_table", () -> new MenuType<>(TinkeringTableMenu::new, FeatureFlagSet.of()));

    public static final Supplier<MenuType<AlloySmelterMenu>> ALLOY_SMELTER =
            MENUS.register("alloy_smelter", () -> net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create(AlloySmelterMenu::new));


    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
