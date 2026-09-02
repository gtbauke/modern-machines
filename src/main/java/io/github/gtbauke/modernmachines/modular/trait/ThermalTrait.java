package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ThermalTrait extends ToolTrait {
    public ThermalTrait(Identifier id) {
        super(id);
    }

    @Override
    public void onAttack(ItemStack tool, LivingEntity target, LivingEntity attacker, int level) {
        target.igniteForSeconds(4 * level);
    }
}
