package io.github.gtbauke.modernmachines.reservoir.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class DrillCasingBlock extends Block {
    public DrillCasingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public DrillCasingBlock() {
        this(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(4.0f, 6.0f)
                .sound(SoundType.NETHERITE_BLOCK)
                .requiresCorrectToolForDrops());
    }
}
