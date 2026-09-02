package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SturdyTrait extends ToolTrait {
    public SturdyTrait(Identifier id) {
        super(id);
    }

    @Override
    public int onDamageTool(ItemStack tool, int amount, LivingEntity user, int level) {
        if (user.getRandom().nextFloat() < 0.15f * level) {
            return 0;
        }

        return amount;
    }
}
