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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;

public class ModularHoeItem extends ModularToolItem {

    public ModularHoeItem(Properties properties) {
        super(BlockTags.MINEABLE_WITH_HOE, 0.0f, -1.0f, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (context.getClickedFace() != Direction.DOWN) {
            Player player = context.getPlayer();
            BlockState modifiedState = state.getToolModifiedState(context, ItemAbilities.HOE_TILL, false);

            if (modifiedState != null && level.getBlockState(pos.above()).isAir()) {
                level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!level.isClientSide()) {
                    level.setBlock(pos, modifiedState, Block.UPDATE_ALL_IMMEDIATE);
                    if (player != null) {
                        context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }
}
