package io.github.gtbauke.modernmachines.reservoir.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class OilShaleBlock extends Block {
    public OilShaleBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public OilShaleBlock() {
        this(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .strength(3.0f, 4.0f)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
    }
}
