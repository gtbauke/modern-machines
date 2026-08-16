package io.github.gtbauke.modernmachines.modular.item;

import net.minecraft.tags.BlockTags;

public class ModularAxeItem extends ModularToolItem {
    public ModularAxeItem(Properties properties) {
        super(BlockTags.MINEABLE_WITH_AXE, 5.0f, -3.0f, properties);
    }
}
