package io.github.gtbauke.modernmachines.api.modular.trait;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ToolTrait {
    private final Identifier id;

    public ToolTrait(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }

    public Component getDisplayName(int level) {
        var baseKey = "trait." + id.getNamespace() + "." + id.getPath();
        if (level > 1) {
            return Component.translatable(baseKey).append(" " + toRoman(level));
        }

        return Component.translatable(baseKey);
    }

    public Component getDescription(int level) {
        return Component.translatable("trait." + id.getNamespace() + "." + id.getPath() + ".desc");
    }

    public float modifyMiningSpeed(ItemStack tool, BlockState state, Player player, float currentSpeed, int level) {
        return currentSpeed;
    }

    public float modifyAttackDamage(ItemStack tool, LivingEntity target, LivingEntity attacker, float currentDamage, int level) {
        return currentDamage;
    }

    public void onAttack(ItemStack tool, LivingEntity target, LivingEntity attacker, int level) {
    }

    public void onMineBlock(ItemStack tool, Level level, BlockState state, BlockPos pos, LivingEntity miner, int traitLevel) {
    }

    public int onDamageTool(ItemStack tool, int amount, LivingEntity user, int level) {
        return amount;
    }

    public void onInventoryTick(ItemStack tool, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot, int traitLevel) {
    }

    private static String toRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(number);
        };
    }
}
