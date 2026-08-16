package io.github.gtbauke.modernmachines.api.machine.stat;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MachineStats {
    private final Map<MachineStatType, Double> baseValues = new EnumMap<>(MachineStatType.class);
    private final Map<MachineStatType, List<MachineStatModifier>> modifiers = new EnumMap<>(MachineStatType.class);
    private final Map<MachineStatType, Double> cachedValues = new EnumMap<>(MachineStatType.class);

    public MachineStats() {
        for (MachineStatType type : MachineStatType.values()) {
            baseValues.put(type, type.getDefaultValue());
            modifiers.put(type, new ArrayList<>());
            cachedValues.put(type, type.getDefaultValue());
        }
    }

    public void setBaseValue(MachineStatType type, double value) {
        baseValues.put(type, value);
        recalculate(type);
    }

    public double getBaseValue(MachineStatType type) {
        return baseValues.getOrDefault(type, type.getDefaultValue());
    }

    public void addModifier(MachineStatType type, MachineStatModifier modifier) {
        modifiers.computeIfAbsent(type, k -> new ArrayList<>()).add(modifier);
        recalculate(type);
    }

    public void clearModifiers() {
        for (List<MachineStatModifier> list : modifiers.values()) {
            list.clear();
        }
        for (MachineStatType type : MachineStatType.values()) {
            recalculate(type);
        }
    }

    public double getStat(MachineStatType type) {
        return cachedValues.getOrDefault(type, type.getDefaultValue());
    }

    public double getSpeedMultiplier() {
        return getStat(MachineStatType.SPEED);
    }

    public double getEfficiencyMultiplier() {
        return getStat(MachineStatType.ENERGY_EFFICIENCY);
    }

    public double getEnergyCostMultiplier() {
        double efficiency = getEfficiencyMultiplier();
        return efficiency > 0 ? (1.0 / efficiency) : 1.0;
    }

    public double getCapacityMultiplier() {
        return getStat(MachineStatType.ENERGY_CAPACITY);
    }

    public void recalculateAll() {
        for (MachineStatType type : MachineStatType.values()) {
            recalculate(type);
        }
    }

    private void recalculate(MachineStatType type) {
        double base = baseValues.getOrDefault(type, type.getDefaultValue());
        List<MachineStatModifier> list = modifiers.get(type);

        if (list == null || list.isEmpty()) {
            cachedValues.put(type, clamp(type, base));
            return;
        }

        double result = base;

        // 1. ADD_FLAT
        for (MachineStatModifier mod : list) {
            if (mod.operation() == MachineStatModifier.Operation.ADD_FLAT) {
                result += mod.value();
            }
        }

        // 2. MULTIPLY_BASE
        double baseMultiplier = 1.0;
        for (MachineStatModifier mod : list) {
            if (mod.operation() == MachineStatModifier.Operation.MULTIPLY_BASE) {
                baseMultiplier += mod.value();
            }
        }
        result *= Math.max(0, baseMultiplier);

        // 3. MULTIPLY_TOTAL
        for (MachineStatModifier mod : list) {
            if (mod.operation() == MachineStatModifier.Operation.MULTIPLY_TOTAL) {
                result *= mod.value();
            }
        }

        cachedValues.put(type, clamp(type, result));
    }

    private double clamp(MachineStatType type, double value) {
        return Math.clamp(value, type.getMinValue(), type.getMaxValue());
    }
}
