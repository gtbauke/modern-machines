package io.github.gtbauke.modernmachines.modular.trait;

import org.jspecify.annotations.Nullable;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class EcologicalTrait extends ToolTrait {
    public EcologicalTrait(Identifier id) {
        super(id);
    }

    @Override
    public void onInventoryTick(ItemStack tool, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot, int traitLevel) {
        if (!(entity instanceof LivingEntity)) {
            return;
        }

        if (entity.tickCount % 100 != 0) {
            return;
        }

        var currentDamage = tool.getOrDefault(DataComponents.DAMAGE, 0);
        if (currentDamage <= 0) {
            return;
        }

        var isSelected = (slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        var repairAmount = isSelected ? traitLevel : 1;
        tool.set(DataComponents.DAMAGE, Math.max(0, currentDamage - repairAmount));
    }
}
