package io.github.gtbauke.modernmachines.network;

import io.github.gtbauke.modernmachines.api.modular.MaterialStatsManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModNetworking::registerPayloads);
        NeoForge.EVENT_BUS.register(new SyncEventHandler());
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0.0");

        registrar.playToClient(
                ClientBoundSyncMaterialStatsPayload.TYPE,
                ClientBoundSyncMaterialStatsPayload.STREAM_CODEC,
                ClientBoundSyncMaterialStatsPayload::handle
        );

        registrar.playToServer(
                ServerboundSideConfigPayload.TYPE,
                ServerboundSideConfigPayload.STREAM_CODEC,
                ServerboundSideConfigPayload::handle
        );
    }

    public static class SyncEventHandler {
        @SubscribeEvent
        public void onDatapackSync(OnDatapackSyncEvent event) {
            if (event.getPlayer() == null) {
                return;
            }

            var payload = new ClientBoundSyncMaterialStatsPayload(MaterialStatsManager.getAllStats());
            var player = event.getPlayer();

            if (player != null) {
                PacketDistributor.sendToPlayer(player, payload);
            } else {
                PacketDistributor.sendToAllPlayers(payload);
            }
        }
    }
}
