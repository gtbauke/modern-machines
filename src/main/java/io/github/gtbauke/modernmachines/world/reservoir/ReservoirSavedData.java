package io.github.gtbauke.modernmachines.world.reservoir;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import io.github.gtbauke.modernmachines.ModernMachines;
import net.minecraft.core.BlockPos;

public class ReservoirSavedData {
    public static final String DATA_NAME = ModernMachines.MOD_ID + "_reservoirs";
    private static final ReservoirSavedData INSTANCE = new ReservoirSavedData();

    private final Map<UUID, ReservoirInstance> reservoirs = new ConcurrentHashMap<>();

    public static @NonNull ReservoirSavedData get() {
        return INSTANCE;
    }

    public void addReservoir(@NonNull ReservoirInstance reservoir) {
        reservoirs.put(reservoir.getId(), reservoir);
    }

    public @Nullable ReservoirInstance getReservoirAt(int x, int y, int z) {
        for (var reservoir : reservoirs.values()) {
            if (reservoir.contains(x, y, z)) {
                return reservoir;
            }
        }

        return null;
    }

    public @Nullable ReservoirInstance getReservoirAt(@NonNull BlockPos pos) {
        return getReservoirAt(pos.getX(), pos.getY(), pos.getZ());
    }

    public @Nullable ReservoirInstance getReservoirById(@NonNull UUID id) {
        return reservoirs.get(id);
    }

    public @NonNull Map<UUID, ReservoirInstance> getAllReservoirs() {
        return Collections.unmodifiableMap(reservoirs);
    }

    public void clear() {
        reservoirs.clear();
    }
}
