package io.github.gtbauke.modernmachines.machine.upgrade;

import java.util.ArrayList;
import java.util.List;

import io.github.gtbauke.modernmachines.api.machine.stat.MachineStatModifier;
import io.github.gtbauke.modernmachines.api.machine.stat.MachineStatType;
import io.github.gtbauke.modernmachines.api.machine.stat.MachineStats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SpeedUpgradeItem extends UpgradeItem {
    public SpeedUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void applyUpgrade(MachineStats stats, ItemStack stack, int count) {
        if (count <= 0) return;
        // +25% Speed per upgrade
        stats.addModifier(MachineStatType.SPEED, MachineStatModifier.multiplyBase("speed_upgrade", 0.25 * count));
        // +10% Energy / Fuel Consumption penalty per upgrade
        stats.addModifier(MachineStatType.ENERGY_EFFICIENCY, MachineStatModifier.multiplyBase("speed_upgrade_penalty", -0.08 * count));
    }

    @Override
    public List<Component> getUpgradeTooltips(ItemStack stack) {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("tooltip.modernmachines.upgrade.speed_boost", "+25%").withStyle(ChatFormatting.GREEN));
        list.add(Component.translatable("tooltip.modernmachines.upgrade.energy_cost_penalty", "+8%").withStyle(ChatFormatting.RED));
        list.add(Component.translatable("tooltip.modernmachines.upgrade.max_stack", MAX_UPGRADE_STACK).withStyle(ChatFormatting.DARK_GRAY));
        return list;
    }
}
