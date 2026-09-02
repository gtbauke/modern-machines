package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ReinforcedTrait extends ToolTrait {
    public ReinforcedTrait(Identifier id) {
        super(id);
    }

    @Override
    public int onDamageTool(ItemStack tool, int amount, LivingEntity user, int level) {
        var chance = Math.min(1.0f, level * 0.20f);
        if (user.getRandom().nextFloat() < chance) {
            return 0;
        }

        return amount;
    }
}
