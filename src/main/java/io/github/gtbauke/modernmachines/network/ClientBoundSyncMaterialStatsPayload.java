package io.github.gtbauke.modernmachines.network;

import java.util.HashMap;
import java.util.Map;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.modular.MaterialStatsManager;
import io.github.gtbauke.modernmachines.api.modular.MaterialToolStats;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

public record ClientBoundSyncMaterialStatsPayload(@NonNull Map<Identifier, MaterialToolStats> stats) implements CustomPacketPayload {
    public static final Type<ClientBoundSyncMaterialStatsPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "sync_material_stats"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundSyncMaterialStatsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.<RegistryFriendlyByteBuf, Identifier, MaterialToolStats, Map<Identifier, MaterialToolStats>>map(
                    HashMap::new,
                    Identifier.STREAM_CODEC,
                    MaterialToolStats.STREAM_CODEC
            ),
            ClientBoundSyncMaterialStatsPayload::stats,
            ClientBoundSyncMaterialStatsPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientBoundSyncMaterialStatsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            MaterialStatsManager.setClientStats(payload.stats());
        });
    }
}
