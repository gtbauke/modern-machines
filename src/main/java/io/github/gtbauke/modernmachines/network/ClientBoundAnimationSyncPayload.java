package io.github.gtbauke.modernmachines.network;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.animation.AnimationParameterContainer;
import io.github.gtbauke.modernmachines.api.animation.IAnimatedMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

public record ClientBoundAnimationSyncPayload(@NonNull BlockPos pos, @NonNull AnimationParameterContainer parameters) implements CustomPacketPayload {
    public static final Type<ClientBoundAnimationSyncPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "animation_sync"));

    public static final StreamCodec<FriendlyByteBuf, ClientBoundAnimationSyncPayload> STREAM_CODEC = StreamCodec.ofMember(
            ClientBoundAnimationSyncPayload::write,
            ClientBoundAnimationSyncPayload::read
    );

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        parameters.encode(buf);
    }

    public static ClientBoundAnimationSyncPayload read(FriendlyByteBuf buf) {
        var pos = buf.readBlockPos();
        var container = new AnimationParameterContainer();
        container.decode(buf);
        return new ClientBoundAnimationSyncPayload(pos, container);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientBoundAnimationSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level().isLoaded(payload.pos())) {
                var blockEntity = player.level().getBlockEntity(payload.pos());
                if (blockEntity instanceof IAnimatedMachine animatedMachine) {
                    var container = animatedMachine.getAnimationParameters();
                    container.fromCompoundTag(payload.parameters().toCompoundTag());
                }
            }
        });
    }
}
