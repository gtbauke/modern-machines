package io.github.gtbauke.modernmachines.machine.upgrade;

import java.util.ArrayList;
import java.util.List;

import io.github.gtbauke.modernmachines.api.machine.stat.MachineStatModifier;
import io.github.gtbauke.modernmachines.api.machine.stat.MachineStatType;
import io.github.gtbauke.modernmachines.api.machine.stat.MachineStats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class EnergyEfficiencyUpgradeItem extends UpgradeItem {
    public EnergyEfficiencyUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void applyUpgrade(MachineStats stats, ItemStack stack, int count) {
        if (count <= 0) return;
        // +20% Efficiency per upgrade (reduces energy/fuel cost)
        stats.addModifier(MachineStatType.ENERGY_EFFICIENCY, MachineStatModifier.multiplyBase("efficiency_upgrade", 0.20 * count));
    }

    @Override
    public List<Component> getUpgradeTooltips(ItemStack stack) {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("tooltip.modernmachines.upgrade.energy_efficiency_boost", "+20%").withStyle(ChatFormatting.GREEN));
        list.add(Component.translatable("tooltip.modernmachines.upgrade.max_stack", MAX_UPGRADE_STACK).withStyle(ChatFormatting.DARK_GRAY));
        return list;
    }
}
