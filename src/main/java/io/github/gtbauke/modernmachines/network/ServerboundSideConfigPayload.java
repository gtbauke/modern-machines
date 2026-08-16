package io.github.gtbauke.modernmachines.network;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.machine.capability.MachineCapabilityType;
import io.github.gtbauke.modernmachines.api.machine.side.ISideConfigurable;
import io.github.gtbauke.modernmachines.api.machine.side.RelativeSide;
import io.github.gtbauke.modernmachines.api.machine.side.SideIoMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

public record ServerboundSideConfigPayload(
        BlockPos pos,
        MachineCapabilityType cap,
        RelativeSide side,
        SideIoMode mode,
        boolean autoEject,
        boolean autoPull,
        byte action
) implements CustomPacketPayload {
    public static final Type<ServerboundSideConfigPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "side_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSideConfigPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBlockPos(payload.pos());
                buf.writeByte(payload.cap().ordinal());
                buf.writeByte(payload.side().ordinal());
                buf.writeByte(payload.mode().ordinal());
                buf.writeBoolean(payload.autoEject());
                buf.writeBoolean(payload.autoPull());
                buf.writeByte(payload.action());
            },
            buf -> {
                BlockPos pos = buf.readBlockPos();
                MachineCapabilityType cap = MachineCapabilityType.values()[buf.readByte()];
                RelativeSide side = RelativeSide.values()[buf.readByte()];
                SideIoMode mode = SideIoMode.fromIndex(buf.readByte());
                boolean autoEject = buf.readBoolean();
                boolean autoPull = buf.readBoolean();
                byte action = buf.readByte();
                return new ServerboundSideConfigPayload(pos, cap, side, mode, autoEject, autoPull, action);
            }
    );

    public static ServerboundSideConfigPayload setSide(BlockPos pos, MachineCapabilityType cap, RelativeSide side, SideIoMode mode) {
        return new ServerboundSideConfigPayload(pos, cap, side, mode, false, false, (byte) 0);
    }

    public static ServerboundSideConfigPayload setAutoEject(BlockPos pos, MachineCapabilityType cap, boolean value) {
        return new ServerboundSideConfigPayload(pos, cap, RelativeSide.FRONT, SideIoMode.NONE, value, false, (byte) 1);
    }

    public static ServerboundSideConfigPayload setAutoPull(BlockPos pos, MachineCapabilityType cap, boolean value) {
        return new ServerboundSideConfigPayload(pos, cap, RelativeSide.FRONT, SideIoMode.NONE, false, value, (byte) 2);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundSideConfigPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null || player.level() == null) return;
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 64.0) return;

            if (player.level().getBlockEntity(payload.pos()) instanceof ISideConfigurable sideConfigurable) {
                if (payload.action() == 0) {
                    sideConfigurable.getSideConfig().setMode(payload.cap(), payload.side(), payload.mode());
                } else if (payload.action() == 1) {
                    sideConfigurable.getSideConfig().setAutoEject(payload.cap(), payload.autoEject());
                } else if (payload.action() == 2) {
                    sideConfigurable.getSideConfig().setAutoPull(payload.cap(), payload.autoPull());
                }
                sideConfigurable.onSideConfigChanged();
            }
        });
    }
}
