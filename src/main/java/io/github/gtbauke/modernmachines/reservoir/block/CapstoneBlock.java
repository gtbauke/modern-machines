package io.github.gtbauke.modernmachines.reservoir.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class CapstoneBlock extends Block {
    public CapstoneBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public CapstoneBlock() {
        this(BlockBehaviour.Properties.of()
                .mapColor(MapColor.DEEPSLATE)
                .strength(4.5f, 6.0f)
                .sound(SoundType.DEEPSLATE)
                .requiresCorrectToolForDrops());
    }
}
