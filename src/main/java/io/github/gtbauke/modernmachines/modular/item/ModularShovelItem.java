package io.github.gtbauke.modernmachines.modular.item;

import net.minecraft.tags.BlockTags;

public class ModularShovelItem extends ModularToolItem {
    public ModularShovelItem(Properties properties) {
        super(BlockTags.MINEABLE_WITH_SHOVEL, 1.5f, -3.0f, properties);
    }
}
