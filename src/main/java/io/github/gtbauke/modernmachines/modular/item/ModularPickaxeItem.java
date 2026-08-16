package io.github.gtbauke.modernmachines.modular.item;

import net.minecraft.tags.BlockTags;

public class ModularPickaxeItem extends ModularToolItem {
    public ModularPickaxeItem(Properties properties) {
        super(BlockTags.MINEABLE_WITH_PICKAXE, 1.0f, -2.8f, properties);
    }
}
