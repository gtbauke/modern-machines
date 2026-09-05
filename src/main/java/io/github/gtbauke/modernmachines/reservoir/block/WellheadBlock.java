package io.github.gtbauke.modernmachines.reservoir.block;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import io.github.gtbauke.modernmachines.core.registry.ModBlockEntities;
import io.github.gtbauke.modernmachines.reservoir.blockentity.WellheadBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;

public class WellheadBlock extends BaseEntityBlock {
    public static final MapCodec<WellheadBlock> CODEC = simpleCodec(WellheadBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public WellheadBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public WellheadBlock() {
        this(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(4.5f, 6.0f)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops());
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new WellheadBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            @NonNull Level level,
            @NonNull BlockState state,
            @NonNull BlockEntityType<T> blockEntityType
    ) {
        if (level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.WELLHEAD.get(), WellheadBlockEntity::serverTick);
    }

    @Override
    public @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
