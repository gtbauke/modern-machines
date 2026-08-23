package io.github.gtbauke.modernmachines.modular.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;

public class ModularShovelItem extends ModularToolItem {

    public ModularShovelItem(Properties properties) {
        super(BlockTags.MINEABLE_WITH_SHOVEL, 1.5f, -3.0f, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (context.getClickedFace() != Direction.DOWN) {
            Player player = context.getPlayer();
            BlockState modifiedState = state.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, false);

            if (modifiedState != null && level.getBlockState(pos.above()).isAir()) {
                level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!level.isClientSide()) {
                    level.setBlock(pos, modifiedState, Block.UPDATE_ALL_IMMEDIATE);
                    if (player != null) {
                        applyDamage(context.getItemInHand(), 1, player);
                    }
                }
                return InteractionResult.SUCCESS;
            } else if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
                if (!level.isClientSide()) {
                    level.levelEvent(null, 1009, pos, 0);
                }
                CampfireBlock.dowse(context.getPlayer(), level, pos, state);
                if (!level.isClientSide()) {
                    level.setBlock(pos, state.setValue(CampfireBlock.LIT, false), Block.UPDATE_ALL_IMMEDIATE);
                    if (player != null) {
                        applyDamage(context.getItemInHand(), 1, player);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }
}
