package io.github.gtbauke.modernmachines.machine.block;

import java.util.Map;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class AbstractPipeBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), map -> {
        map.put(Direction.NORTH, NORTH);
        map.put(Direction.SOUTH, SOUTH);
        map.put(Direction.EAST, EAST);
        map.put(Direction.WEST, WEST);
        map.put(Direction.UP, UP);
        map.put(Direction.DOWN, DOWN);
    });

    protected static final VoxelShape CORE_SHAPE = Block.box(6.0, 6.0, 6.0, 10.0, 10.0, 10.0);
    protected static final VoxelShape NORTH_SHAPE = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 6.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(6.0, 6.0, 10.0, 10.0, 10.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = Block.box(0.0, 6.0, 6.0, 6.0, 10.0, 10.0);
    protected static final VoxelShape EAST_SHAPE = Block.box(10.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    protected static final VoxelShape DOWN_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
    protected static final VoxelShape UP_SHAPE = Block.box(6.0, 10.0, 6.0, 10.0, 16.0, 10.0);

    protected static final VoxelShape STRAIGHT_X_SHAPE = Block.box(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    protected static final VoxelShape STRAIGHT_Y_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    protected static final VoxelShape STRAIGHT_Z_SHAPE = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);

    protected final VoxelShape[] shapeCache;

    public AbstractPipeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(WATERLOGGED, false));
        this.shapeCache = this.makeShapes();
    }

    protected VoxelShape[] makeShapes() {
        var shapes = new VoxelShape[64];
        for (int i = 0; i < 64; ++i) {
            var shape = CORE_SHAPE;
            if ((i & (1 << Direction.DOWN.get3DDataValue())) != 0) {
                shape = Shapes.or(shape, DOWN_SHAPE);
            }

            if ((i & (1 << Direction.UP.get3DDataValue())) != 0) {
                shape = Shapes.or(shape, UP_SHAPE);
            }

            if ((i & (1 << Direction.NORTH.get3DDataValue())) != 0) {
                shape = Shapes.or(shape, NORTH_SHAPE);
            }

            if ((i & (1 << Direction.SOUTH.get3DDataValue())) != 0) {
                shape = Shapes.or(shape, SOUTH_SHAPE);
            }

            if ((i & (1 << Direction.WEST.get3DDataValue())) != 0) {
                shape = Shapes.or(shape, WEST_SHAPE);
            }

            if ((i & (1 << Direction.EAST.get3DDataValue())) != 0) {
                shape = Shapes.or(shape, EAST_SHAPE);
            }

            shapes[i] = shape;
        }

        return shapes;
    }

    protected int getShapeIndex(BlockState state) {
        int index = 0;
        for (var direction : Direction.values()) {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(direction))) {
                index |= 1 << direction.get3DDataValue();
            }
        }

        return index;
    }

    @Override
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        int shapeIndex = this.getShapeIndex(state);
        return this.shapeCache[shapeIndex];
    }

    public boolean canConnectTo(BlockGetter level, BlockPos pos, Direction direction) {
        var targetPos = pos.relative(direction);
        var targetState = level.getBlockState(targetPos);
        return targetState.is(this);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var fluidState = level.getFluidState(pos);

        var state = this.defaultBlockState();
        for (var direction : Direction.values()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.canConnectTo(level, pos, direction));
        }

        return state.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected @NonNull BlockState updateShape(
            BlockState state,
            @NonNull LevelReader level,
            @NonNull ScheduledTickAccess ticks,
            @NonNull BlockPos pos,
            @NonNull Direction directionToNeighbour,
            @NonNull BlockPos neighbourPos,
            @NonNull BlockState neighbourState,
            @NonNull RandomSource random
    ) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        var property = PROPERTY_BY_DIRECTION.get(directionToNeighbour);
        if (property != null) {
            return state.setValue(property, this.canConnectTo(level, pos, directionToNeighbour));
        }

        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    protected @NonNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, WATERLOGGED);
    }
}
