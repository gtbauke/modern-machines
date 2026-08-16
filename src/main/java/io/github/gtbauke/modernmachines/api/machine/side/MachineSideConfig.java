package io.github.gtbauke.modernmachines.api.machine.side;

import java.util.EnumMap;
import java.util.Map;

import io.github.gtbauke.modernmachines.api.machine.capability.MachineCapabilityType;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MachineSideConfig {
    private final Map<MachineCapabilityType, Map<RelativeSide, SideIoMode>> sides = new EnumMap<>(MachineCapabilityType.class);
    private final Map<MachineCapabilityType, Boolean> autoEject = new EnumMap<>(MachineCapabilityType.class);
    private final Map<MachineCapabilityType, Boolean> autoPull = new EnumMap<>(MachineCapabilityType.class);

    public MachineSideConfig() {
        for (MachineCapabilityType type : MachineCapabilityType.values()) {
            Map<RelativeSide, SideIoMode> sideMap = new EnumMap<>(RelativeSide.class);
            for (RelativeSide side : RelativeSide.values()) {
                sideMap.put(side, SideIoMode.NONE);
            }
            sides.put(type, sideMap);
            autoEject.put(type, false);
            autoPull.put(type, false);
        }

        // Standard Default Configuration
        // Items: Top & Left/Right = INPUT, Bottom = OUTPUT
        setMode(MachineCapabilityType.ITEM, RelativeSide.TOP, SideIoMode.INPUT);
        setMode(MachineCapabilityType.ITEM, RelativeSide.LEFT, SideIoMode.INPUT);
        setMode(MachineCapabilityType.ITEM, RelativeSide.RIGHT, SideIoMode.INPUT);
        setMode(MachineCapabilityType.ITEM, RelativeSide.BACK, SideIoMode.INPUT);
        setMode(MachineCapabilityType.ITEM, RelativeSide.BOTTOM, SideIoMode.OUTPUT);
        setMode(MachineCapabilityType.ITEM, RelativeSide.FRONT, SideIoMode.NONE);

        // Energy: All sides accept INPUT by default except front
        for (RelativeSide side : RelativeSide.values()) {
            setMode(MachineCapabilityType.ENERGY, side, side == RelativeSide.FRONT ? SideIoMode.NONE : SideIoMode.INPUT);
        }

        // Fluids: Left/Top = INPUT, Right/Bottom = OUTPUT
        setMode(MachineCapabilityType.FLUID, RelativeSide.TOP, SideIoMode.INPUT);
        setMode(MachineCapabilityType.FLUID, RelativeSide.LEFT, SideIoMode.INPUT);
        setMode(MachineCapabilityType.FLUID, RelativeSide.RIGHT, SideIoMode.OUTPUT);
        setMode(MachineCapabilityType.FLUID, RelativeSide.BOTTOM, SideIoMode.OUTPUT);
    }

    public SideIoMode getMode(MachineCapabilityType cap, RelativeSide side) {
        return sides.get(cap).getOrDefault(side, SideIoMode.NONE);
    }

    public SideIoMode getModeAbsolute(MachineCapabilityType cap, Direction facing, Direction side) {
        RelativeSide relSide = RelativeSide.fromAbsolute(facing, side);
        return getMode(cap, relSide);
    }

    public void setMode(MachineCapabilityType cap, RelativeSide side, SideIoMode mode) {
        sides.get(cap).put(side, mode != null ? mode : SideIoMode.NONE);
    }

    public SideIoMode cycleMode(MachineCapabilityType cap, RelativeSide side, boolean forward) {
        SideIoMode current = getMode(cap, side);
        SideIoMode next = forward ? current.next() : current.previous();
        setMode(cap, side, next);
        return next;
    }

    public boolean isAutoEject(MachineCapabilityType cap) {
        return autoEject.getOrDefault(cap, false);
    }

    public void setAutoEject(MachineCapabilityType cap, boolean value) {
        autoEject.put(cap, value);
    }

    public boolean toggleAutoEject(MachineCapabilityType cap) {
        boolean val = !isAutoEject(cap);
        setAutoEject(cap, val);
        return val;
    }

    public boolean isAutoPull(MachineCapabilityType cap) {
        return autoPull.getOrDefault(cap, false);
    }

    public void setAutoPull(MachineCapabilityType cap, boolean value) {
        autoPull.put(cap, value);
    }

    public boolean toggleAutoPull(MachineCapabilityType cap) {
        boolean val = !isAutoPull(cap);
        setAutoPull(cap, val);
        return val;
    }

    public void save(ValueOutput output) {
        ValueOutput sideOutput = output.child("side_config");
        for (MachineCapabilityType cap : MachineCapabilityType.values()) {
            ValueOutput capOutput = sideOutput.child(cap.getSerializedName());
            Map<RelativeSide, SideIoMode> capSides = sides.get(cap);
            for (RelativeSide side : RelativeSide.values()) {
                capOutput.putString(side.getSerializedName(), capSides.get(side).getSerializedName());
            }
            capOutput.putBoolean("auto_eject", isAutoEject(cap));
            capOutput.putBoolean("auto_pull", isAutoPull(cap));
        }
    }

    public void load(ValueInput input) {
        input.child("side_config").ifPresent(sideInput -> {
            for (MachineCapabilityType cap : MachineCapabilityType.values()) {
                sideInput.child(cap.getSerializedName()).ifPresent(capInput -> {
                    for (RelativeSide side : RelativeSide.values()) {
                        String modeName = capInput.getStringOr(side.getSerializedName(), "none");
                        for (SideIoMode mode : SideIoMode.values()) {
                            if (mode.getSerializedName().equalsIgnoreCase(modeName)) {
                                setMode(cap, side, mode);
                                break;
                            }
                        }
                    }
                    setAutoEject(cap, capInput.getBooleanOr("auto_eject", false));
                    setAutoPull(cap, capInput.getBooleanOr("auto_pull", false));
                });
            }
        });
    }

    public void encode(FriendlyByteBuf buf) {
        for (MachineCapabilityType cap : MachineCapabilityType.values()) {
            for (RelativeSide side : RelativeSide.values()) {
                buf.writeByte(getMode(cap, side).ordinal());
            }
            buf.writeBoolean(isAutoEject(cap));
            buf.writeBoolean(isAutoPull(cap));
        }
    }

    public void decode(FriendlyByteBuf buf) {
        for (MachineCapabilityType cap : MachineCapabilityType.values()) {
            for (RelativeSide side : RelativeSide.values()) {
                byte modeIndex = buf.readByte();
                setMode(cap, side, SideIoMode.fromIndex(modeIndex));
            }
            setAutoEject(cap, buf.readBoolean());
            setAutoPull(cap, buf.readBoolean());
        }
    }
}
