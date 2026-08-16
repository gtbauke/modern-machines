package io.github.gtbauke.modernmachines.api.machine.stat;

public record MachineStatModifier(String id, Operation operation, double value) {
    public enum Operation {
        ADD_FLAT,
        MULTIPLY_BASE,
        MULTIPLY_TOTAL
    }

    public static MachineStatModifier addFlat(String id, double value) {
        return new MachineStatModifier(id, Operation.ADD_FLAT, value);
    }

    public static MachineStatModifier multiplyBase(String id, double value) {
        return new MachineStatModifier(id, Operation.MULTIPLY_BASE, value);
    }

    public static MachineStatModifier multiplyTotal(String id, double value) {
        return new MachineStatModifier(id, Operation.MULTIPLY_TOTAL, value);
    }
}
