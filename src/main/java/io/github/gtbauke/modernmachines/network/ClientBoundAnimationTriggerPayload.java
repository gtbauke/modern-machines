package io.github.gtbauke.modernmachines.network;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.animation.IAnimatedMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

public record ClientBoundAnimationTriggerPayload(@NonNull BlockPos pos, int triggerId) implements CustomPacketPayload {
    public static final Type<ClientBoundAnimationTriggerPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "animation_trigger"));

    public static final StreamCodec<FriendlyByteBuf, ClientBoundAnimationTriggerPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ClientBoundAnimationTriggerPayload::pos,
            ByteBufCodecs.VAR_INT,
            ClientBoundAnimationTriggerPayload::triggerId,
            ClientBoundAnimationTriggerPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientBoundAnimationTriggerPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level().isLoaded(payload.pos())) {
                var blockEntity = player.level().getBlockEntity(payload.pos());
                if (blockEntity instanceof IAnimatedMachine animatedMachine) {
                    animatedMachine.triggerAnimation(payload.triggerId());
                }
            }
        });
    }
}
