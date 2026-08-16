package io.github.gtbauke.modernmachines.network;

import io.github.gtbauke.modernmachines.ModernMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

public record ServerBoundMachineConfigPayload(@NonNull BlockPos pos, byte configType, int param1) implements CustomPacketPayload {
    public static final Type<ServerBoundMachineConfigPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "machine_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerBoundMachineConfigPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ServerBoundMachineConfigPayload::pos,
            ByteBufCodecs.BYTE,
            ServerBoundMachineConfigPayload::configType,
            ByteBufCodecs.VAR_INT,
            ServerBoundMachineConfigPayload::param1,
            ServerBoundMachineConfigPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerBoundMachineConfigPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();

            if (level != null && level.isLoaded(payload.pos())) {
                var be = level.getBlockEntity(payload.pos());

                if (be != null) {
                    be.setChanged();
                }
            }
        });
    }
}
