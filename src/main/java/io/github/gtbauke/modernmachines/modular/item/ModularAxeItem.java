package io.github.gtbauke.modernmachines.modular.item;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;

public class ModularAxeItem extends ModularToolItem {

    public ModularAxeItem(Properties properties) {
        super(BlockTags.MINEABLE_WITH_AXE, 5.0f, -3.0f, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();

        // 1. Try stripping block
        BlockState modifiedState = state.getToolModifiedState(context, ItemAbilities.AXE_STRIP, false);
        if (modifiedState != null) {
            level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!level.isClientSide()) {
                level.setBlock(pos, modifiedState, Block.UPDATE_ALL_IMMEDIATE);
                if (player != null) {
                    applyDamage(stack, 1, player);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // 2. Try scraping copper
        modifiedState = state.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, false);
        if (modifiedState != null) {
            level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3005, pos, 0);
            if (!level.isClientSide()) {
                level.setBlock(pos, modifiedState, Block.UPDATE_ALL_IMMEDIATE);
                if (player != null) {
                    applyDamage(stack, 1, player);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // 3. Try removing wax
        modifiedState = state.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, false);
        if (modifiedState != null) {
            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, pos, 0);
            if (!level.isClientSide()) {
                level.setBlock(pos, modifiedState, Block.UPDATE_ALL_IMMEDIATE);
                if (player != null) {
                    applyDamage(stack, 1, player);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }
}
