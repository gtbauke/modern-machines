package io.github.gtbauke.modernmachines.modular.item;

import net.minecraft.tags.BlockTags;

public class ModularSwordItem extends ModularToolItem {
    public ModularSwordItem(Properties properties) {
        super(BlockTags.SWORD_EFFICIENT, 3.5f, -2.4f, properties);
    }
}
