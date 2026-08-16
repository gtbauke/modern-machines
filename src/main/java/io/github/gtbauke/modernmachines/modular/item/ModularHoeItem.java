package io.github.gtbauke.modernmachines.modular.item;

import net.minecraft.tags.BlockTags;

public class ModularHoeItem extends ModularToolItem {
    public ModularHoeItem(Properties properties) {
        super(BlockTags.MINEABLE_WITH_HOE, 0.0f, -1.0f, properties);
    }
}
