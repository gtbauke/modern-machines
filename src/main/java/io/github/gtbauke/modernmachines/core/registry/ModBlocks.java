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

    public static final DeferredBlock<io.github.gtbauke.modernmachines.machine.block.CopperPipeBlock> COPPER_PIPE = BLOCKS.registerBlock("copper_pipe",
            io.github.gtbauke.modernmachines.machine.block.CopperPipeBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.5f, 3.0f).sound(SoundType.COPPER).noOcclusion());

    public static final DeferredBlock<io.github.gtbauke.modernmachines.machine.block.SteelPipeBlock> STEEL_PIPE = BLOCKS.registerBlock("steel_pipe",
            io.github.gtbauke.modernmachines.machine.block.SteelPipeBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0f, 4.0f).sound(SoundType.METAL).noOcclusion());

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
