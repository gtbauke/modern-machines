package io.github.gtbauke.modernmachines.api.machine.stat;

import net.minecraft.network.chat.Component;

public enum MachineStatType {
    SPEED("speed", Component.translatable("stat.modernmachines.speed"), 1.0, 0.1, 10.0),
    ENERGY_EFFICIENCY("energy_efficiency", Component.translatable("stat.modernmachines.energy_efficiency"), 1.0, 0.1, 5.0),
    ENERGY_CAPACITY("energy_capacity", Component.translatable("stat.modernmachines.energy_capacity"), 1.0, 0.1, 10.0);

    private final String id;
    private final Component displayName;
    private final double defaultValue;
    private final double minValue;
    private final double maxValue;

    MachineStatType(String id, Component displayName, double defaultValue, double minValue, double maxValue) {
        this.id = id;
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }
}
