package io.github.gtbauke.modernmachines.world.reservoir;

import java.util.UUID;
import org.jspecify.annotations.NonNull;

public class ReservoirInstance {
    private final UUID id;
    private final String reservoirType;
    private final String fluidId;
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private final long maxVolume;
    private long currentVolume;
    private final float initialPressure;
    private float currentPressure;
    private long injectedVolume;

    public ReservoirInstance(
            UUID id,
            String reservoirType,
            String fluidId,
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ,
            long maxVolume,
            long currentVolume,
            float initialPressure,
            float currentPressure,
            long injectedVolume
    ) {
        this.id = id;
        this.reservoirType = reservoirType;
        this.fluidId = fluidId;
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
        this.maxVolume = Math.max(1_000L, maxVolume);
        this.currentVolume = Math.clamp(currentVolume, 0L, this.maxVolume);
        this.initialPressure = Math.max(0.1f, initialPressure);
        this.currentPressure = currentPressure;
        this.injectedVolume = Math.max(0L, injectedVolume);

        recalculatePressure();
    }

    public static @NonNull ReservoirInstance createNew(
            String reservoirType,
            String fluidId,
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ,
            long maxVolume,
            float initialPressure
    ) {
        return new ReservoirInstance(
                UUID.randomUUID(),
                reservoirType,
                fluidId,
                minX,
                minY,
                minZ,
                maxX,
                maxY,
                maxZ,
                maxVolume,
                maxVolume,
                initialPressure,
                initialPressure,
                0L
        );
    }

    public void recalculatePressure() {
        if (maxVolume <= 0) {
            this.currentPressure = 0.0f;
            return;
        }

        float effectiveVolumeRatio = (float) (currentVolume + injectedVolume) / (float) maxVolume;
        this.currentPressure = Math.clamp(initialPressure * effectiveVolumeRatio, 0.0f, initialPressure * 2.0f);
    }

    public long drain(long maxDrain) {
        if (maxDrain <= 0 || currentVolume <= 0) {
            return 0;
        }

        long drained = Math.min(maxDrain, currentVolume);
        this.currentVolume -= drained;
        recalculatePressure();

        return drained;
    }

    public long inject(long amount) {
        if (amount <= 0) {
            return 0;
        }

        this.injectedVolume += amount;
        recalculatePressure();

        return amount;
    }

    public boolean contains(int x, int y, int z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public @NonNull UUID getId() {
        return id;
    }

    public @NonNull String getReservoirType() {
        return reservoirType;
    }

    public @NonNull String getFluidId() {
        return fluidId;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public long getMaxVolume() {
        return maxVolume;
    }

    public long getCurrentVolume() {
        return currentVolume;
    }

    public float getInitialPressure() {
        return initialPressure;
    }

    public float getCurrentPressure() {
        return currentPressure;
    }

    public long getInjectedVolume() {
        return injectedVolume;
    }
}
