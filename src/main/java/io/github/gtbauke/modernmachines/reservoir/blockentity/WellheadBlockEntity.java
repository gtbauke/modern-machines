package io.github.gtbauke.modernmachines.reservoir.blockentity;

import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import io.github.gtbauke.modernmachines.core.registry.ModBlockEntities;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.world.reservoir.ReservoirInstance;
import io.github.gtbauke.modernmachines.world.reservoir.ReservoirSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class WellheadBlockEntity extends BlockEntity implements ResourceHandler<FluidResource> {
    private static final int MAX_PROBE_DEPTH = 64;
    private static final int TICK_INTERVAL = 20;

    private int tickCount = 0;
    private @Nullable UUID connectedReservoirId = null;
    private @Nullable String cachedFluidId = null;
    private float cachedPressure = 0.0f;
    private long cachedVolume = 0L;

    public WellheadBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WELLHEAD.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WellheadBlockEntity blockEntity) {
        blockEntity.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        tickCount++;
        if (tickCount % TICK_INTERVAL != 0) {
            return;
        }

        var reservoir = findConnectedReservoir(level, pos);
        if (reservoir == null) {
            this.connectedReservoirId = null;
            this.cachedFluidId = null;
            this.cachedPressure = 0.0f;
            this.cachedVolume = 0L;
            return;
        }

        this.connectedReservoirId = reservoir.getId();
        this.cachedFluidId = reservoir.getFluidId();
        this.cachedPressure = reservoir.getCurrentPressure();
        this.cachedVolume = reservoir.getCurrentVolume();

        if (reservoir.getCurrentVolume() <= 0 || reservoir.getCurrentPressure() <= 0.0f) {
            return;
        }

        float pressureRatio = reservoir.getCurrentPressure() / Math.max(0.1f, reservoir.getInitialPressure());
        int flowRate = (int) Math.clamp(250.0f * Math.sqrt(Math.max(0.01f, pressureRatio)), 10.0f, 2000.0f);

        pushFluidToNeighbours(level, pos, reservoir, flowRate);
    }

    private void pushFluidToNeighbours(Level level, BlockPos pos, ReservoirInstance reservoir, int maxAmount) {
        var fluid = resolveFluid(reservoir.getFluidId());
        if (fluid == Fluids.EMPTY) {
            return;
        }

        var resource = FluidResource.of(fluid);

        for (var direction : Direction.values()) {
            if (direction == Direction.DOWN) {
                continue;
            }

            var neighbourPos = pos.relative(direction);
            var targetHandler = level.getCapability(Capabilities.Fluid.BLOCK, neighbourPos, direction.getOpposite());
            if (targetHandler == null) {
                continue;
            }

            try (var tx = Transaction.open(null)) {
                int inserted = targetHandler.insert(resource, maxAmount, tx);
                if (inserted > 0) {
                    long drained = reservoir.drain(inserted);
                    if (drained > 0) {
                        tx.commit();
                        break;
                    }
                }
            }
        }
    }

    private @Nullable ReservoirInstance findConnectedReservoir(Level level, BlockPos pos) {
        var mutablePos = new BlockPos.MutableBlockPos();
        mutablePos.set(pos);

        for (int i = 1; i <= MAX_PROBE_DEPTH; i++) {
            mutablePos.move(Direction.DOWN);
            var belowState = level.getBlockState(mutablePos);

            if (belowState.is(ModBlocks.DRILL_CASING.get())) {
                continue;
            }

            return ReservoirSavedData.get().getReservoirAt(mutablePos);
        }

        return null;
    }

    private @NonNull Fluid resolveFluid(@Nullable String fluidId) {
        if (fluidId == null || fluidId.isBlank()) {
            return Fluids.EMPTY;
        }

        var loc = Identifier.tryParse(fluidId);
        if (loc == null) {
            return Fluids.EMPTY;
        }

        var opt = BuiltInRegistries.FLUID.getOptional(loc);
        return opt.orElse(Fluids.EMPTY);
    }

    public @Nullable ReservoirInstance getConnectedReservoir() {
        if (level == null || connectedReservoirId == null) {
            return null;
        }

        return ReservoirSavedData.get().getReservoirById(connectedReservoirId);
    }

    public float getCachedPressure() {
        return cachedPressure;
    }

    public long getCachedVolume() {
        return cachedVolume;
    }

    public @Nullable String getCachedFluidId() {
        return cachedFluidId;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public @NonNull FluidResource getResource(int index) {
        var reservoir = getConnectedReservoir();
        if (reservoir == null || reservoir.getCurrentVolume() <= 0) {
            return FluidResource.EMPTY;
        }

        var fluid = resolveFluid(reservoir.getFluidId());
        if (fluid == Fluids.EMPTY) {
            return FluidResource.EMPTY;
        }

        return FluidResource.of(fluid);
    }

    @Override
    public long getAmountAsLong(int index) {
        var reservoir = getConnectedReservoir();
        if (reservoir == null || reservoir.getCurrentVolume() <= 0) {
            return 0L;
        }

        return reservoir.getCurrentVolume();
    }

    @Override
    public long getCapacityAsLong(int index, @NonNull FluidResource resource) {
        var reservoir = getConnectedReservoir();
        if (reservoir == null) {
            return 100_000L;
        }

        return reservoir.getMaxVolume();
    }

    @Override
    public boolean isValid(int index, @NonNull FluidResource resource) {
        var reservoir = getConnectedReservoir();
        if (reservoir == null) {
            return false;
        }

        var fluid = resolveFluid(reservoir.getFluidId());
        return resource.getFluid() == fluid || resource.getFluid() == Fluids.WATER;
    }

    @Override
    public int insert(int index, @NonNull FluidResource resource, int amount, @Nullable TransactionContext transaction) {
        return (int) insert(index, resource, (long) amount, transaction);
    }

    @Override
    public int extract(int index, @NonNull FluidResource resource, int amount, @Nullable TransactionContext transaction) {
        return (int) extract(index, resource, (long) amount, transaction);
    }

    public long insert(int index, @NonNull FluidResource resource, long amount, @Nullable TransactionContext transaction) {
        if (amount <= 0 || resource.isEmpty()) {
            return 0L;
        }

        var reservoir = getConnectedReservoir();
        if (reservoir == null) {
            return 0L;
        }

        if (resource.getFluid() == Fluids.WATER) {
            reservoir.inject(amount);
            return amount;
        }

        return 0L;
    }

    public long extract(int index, @NonNull FluidResource resource, long amount, @Nullable TransactionContext transaction) {
        if (amount <= 0 || resource.isEmpty()) {
            return 0L;
        }

        var reservoir = getConnectedReservoir();
        if (reservoir == null || reservoir.getCurrentVolume() <= 0) {
            return 0L;
        }

        var fluid = resolveFluid(reservoir.getFluidId());
        if (fluid == Fluids.EMPTY || resource.getFluid() != fluid) {
            return 0L;
        }

        long toDrain = Math.min(amount, reservoir.getCurrentVolume());
        if (toDrain <= 0) {
            return 0L;
        }

        reservoir.drain(toDrain);
        return toDrain;
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        if (connectedReservoirId != null) {
            output.putString("ConnectedReservoir", connectedReservoirId.toString());
        }

        if (cachedFluidId != null) {
            output.putString("CachedFluid", cachedFluidId);
        }

        output.putFloat("CachedPressure", cachedPressure);
        output.putLong("CachedVolume", cachedVolume);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        var idStr = input.getStringOr("ConnectedReservoir", "");
        if (!idStr.isEmpty()) {
            this.connectedReservoirId = UUID.fromString(idStr);
        }

        this.cachedFluidId = input.getStringOr("CachedFluid", null);
        this.cachedPressure = input.getFloatOr("CachedPressure", 0.0f);
        this.cachedVolume = input.getLongOr("CachedVolume", 0L);
    }
}
