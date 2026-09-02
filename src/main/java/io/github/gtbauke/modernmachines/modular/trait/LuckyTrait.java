package io.github.gtbauke.modernmachines.modular.trait;

import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LuckyTrait extends ToolTrait {
    public LuckyTrait(Identifier id) {
        super(id);
    }

    @Override
    public void onMineBlock(ItemStack tool, Level level, BlockState state, BlockPos pos, LivingEntity miner, int traitLevel) {
        if (level instanceof ServerLevel serverLevel && !state.isAir() && state.getDestroySpeed(level, pos) > 0) {
            if (miner.getRandom().nextFloat() < 0.40f) {
                ExperienceOrb.award(serverLevel, miner.position(), traitLevel * 3);
            }
        }
    }

    @Override
    public void onAttack(ItemStack tool, LivingEntity target, LivingEntity attacker, int level) {
        if (target.level() instanceof ServerLevel serverLevel && target.isDeadOrDying()) {
            ExperienceOrb.award(serverLevel, target.position(), level * 4);
        }
    }
}
