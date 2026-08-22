package io.github.gtbauke.modernmachines.core.registry;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.machine.block.BasicAlloySmelterControllerBlock;
import io.github.gtbauke.modernmachines.machine.block.BasicAlloySmelterHeaterBlock;
import io.github.gtbauke.modernmachines.modular.block.PartBuilderBlock;
import io.github.gtbauke.modernmachines.modular.block.TinkeringTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ModernMachines.MOD_ID);

    public static final DeferredBlock<PartBuilderBlock> PART_BUILDER = BLOCKS.registerBlock("part_builder",
            PartBuilderBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0f, 3.0f).sound(SoundType.WOOD));

    public static final DeferredBlock<TinkeringTableBlock> TINKERING_TABLE = BLOCKS.registerBlock("tinkering_table",
            TinkeringTableBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5f, 3.0f).sound(SoundType.WOOD));

    public static final DeferredBlock<BasicAlloySmelterControllerBlock> BASIC_ALLOY_SMELTER_CONTROLLER = BLOCKS.registerBlock("basic_alloy_smelter_controller",
            BasicAlloySmelterControllerBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.5f, 4.0f).sound(SoundType.STONE).lightLevel(st -> st.getValue(BasicAlloySmelterControllerBlock.LIT) ? 13 : 0));

    public static final DeferredBlock<BasicAlloySmelterHeaterBlock> BASIC_ALLOY_SMELTER_HEATER = BLOCKS.registerBlock("basic_alloy_smelter_heater",
            BasicAlloySmelterHeaterBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.5f, 4.0f).sound(SoundType.STONE).lightLevel(st -> st.getValue(BasicAlloySmelterHeaterBlock.LIT) ? 14 : 0));

    // Compatibility alias for existing references
    public static final DeferredBlock<BasicAlloySmelterControllerBlock> ALLOY_SMELTER = BASIC_ALLOY_SMELTER_CONTROLLER;

    public static final DeferredBlock<io.github.gtbauke.modernmachines.machine.block.EngineersTerminalBlock> ENGINEERS_TERMINAL = BLOCKS.registerBlock("engineers_terminal",
            io.github.gtbauke.modernmachines.machine.block.EngineersTerminalBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0f, 4.0f).sound(SoundType.METAL));

    // Steam Era Blocks
    public static final DeferredBlock<Block> ADOBE_BRICK = BLOCKS.registerBlock("adobe_brick",
            Block::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(2.0f, 3.0f).sound(SoundType.STONE));

//    public static final DeferredBlock<io.github.gtbauke.modernmachines.machine.block.BronzeCasingBlock> BRONZE_CASING = BLOCKS.registerBlock("bronze_casing",
//            io.github.gtbauke.modernmachines.machine.block.BronzeCasingBlock::new,
//            () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(3.0f, 5.0f).sound(SoundType.METAL));
//
//    public static final DeferredBlock<io.github.gtbauke.modernmachines.machine.block.SolidFuelBoilerBlock> SOLID_FUEL_BOILER = BLOCKS.registerBlock("solid_fuel_boiler",
//            io.github.gtbauke.modernmachines.machine.block.SolidFuelBoilerBlock::new,
//            () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(3.5f, 6.0f).sound(SoundType.METAL).lightLevel(st -> st.getValue(io.github.gtbauke.modernmachines.machine.block.SolidFuelBoilerBlock.LIT) ? 14 : 0));
//
//    public static final DeferredBlock<io.github.gtbauke.modernmachines.machine.block.SteamTurbineBlock> STEAM_TURBINE = BLOCKS.registerBlock("steam_turbine",
//            io.github.gtbauke.modernmachines.machine.block.SteamTurbineBlock::new,
//            () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(3.5f, 6.0f).sound(SoundType.METAL).lightLevel(st -> st.getValue(io.github.gtbauke.modernmachines.machine.block.SteamTurbineBlock.LIT) ? 10 : 0));
//
//    public static final DeferredBlock<io.github.gtbauke.modernmachines.machine.block.SteamCrusherBlock> STEAM_CRUSHER = BLOCKS.registerBlock("steam_crusher",
//            io.github.gtbauke.modernmachines.machine.block.SteamCrusherBlock::new,
//            () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(3.5f, 6.0f).sound(SoundType.METAL).lightLevel(st -> st.getValue(io.github.gtbauke.modernmachines.machine.block.SteamCrusherBlock.LIT) ? 8 : 0));
//
//    public static final DeferredBlock<io.github.gtbauke.modernmachines.machine.block.SteamAlloySmelterBlock> STEAM_ALLOY_SMELTER = BLOCKS.registerBlock("steam_alloy_smelter",
//            io.github.gtbauke.modernmachines.machine.block.SteamAlloySmelterBlock::new,
//            () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(3.5f, 6.0f).sound(SoundType.METAL).lightLevel(st -> st.getValue(io.github.gtbauke.modernmachines.machine.block.SteamAlloySmelterBlock.LIT) ? 13 : 0));
//
//    public static final DeferredBlock<io.github.gtbauke.modernmachines.machine.block.BronzeFluidTankBlock> BRONZE_FLUID_TANK = BLOCKS.registerBlock("bronze_fluid_tank",
//            io.github.gtbauke.modernmachines.machine.block.BronzeFluidTankBlock::new,
//            () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(2.5f, 4.0f).sound(SoundType.GLASS));
//
//    public static final DeferredBlock<io.github.gtbauke.modernmachines.machine.block.BronzeFluidPipeBlock> BRONZE_FLUID_PIPE = BLOCKS.registerBlock("bronze_fluid_pipe",
//            io.github.gtbauke.modernmachines.machine.block.BronzeFluidPipeBlock::new,
//            () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.5f, 2.0f).sound(SoundType.METAL));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
