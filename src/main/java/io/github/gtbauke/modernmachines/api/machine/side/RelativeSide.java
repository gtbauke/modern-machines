package io.github.gtbauke.modernmachines.api.machine.side;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum RelativeSide implements StringRepresentable {
    FRONT("front"),
    BACK("back"),
    LEFT("left"),
    RIGHT("right"),
    TOP("top"),
    BOTTOM("bottom");

    private final String name;

    RelativeSide(String name) {
        this.name = name;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }

    /**
     * Converts this relative side to a world Direction based on the machine's front horizontal facing.
     */
    public Direction toAbsolute(Direction facing) {
        if (facing == null || facing.getAxis() == Direction.Axis.Y) {
            facing = Direction.NORTH;
        }

        return switch (this) {
            case FRONT -> facing;
            case BACK -> facing.getOpposite();
            case LEFT -> facing.getCounterClockWise();
            case RIGHT -> facing.getClockWise();
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
        };
    }

    /**
     * Converts a world Direction to a relative side given the machine's front horizontal facing.
     */
    public static RelativeSide fromAbsolute(Direction facing, Direction side) {
        if (side == Direction.UP) return TOP;
        if (side == Direction.DOWN) return BOTTOM;
        if (facing == null || facing.getAxis() == Direction.Axis.Y) {
            facing = Direction.NORTH;
        }

        if (side == facing) return FRONT;
        if (side == facing.getOpposite()) return BACK;
        if (side == facing.getCounterClockWise()) return LEFT;
        if (side == facing.getClockWise()) return RIGHT;

        return FRONT;
    }
}
