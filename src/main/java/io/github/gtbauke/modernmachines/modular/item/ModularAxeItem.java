package io.github.gtbauke.modernmachines.modular.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jspecify.annotations.NonNull;

public class ModularAxeItem extends ModularToolItem {

    public ModularAxeItem(Properties properties) {
        super(BlockTags.MINEABLE_WITH_AXE, 5.0f, -3.0f, properties);
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var player = context.getPlayer();
        var state = level.getBlockState(pos);
        var stack = context.getItemInHand();

        var modifiedState = state.getToolModifiedState(context, ItemAbilities.AXE_STRIP, false);
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
