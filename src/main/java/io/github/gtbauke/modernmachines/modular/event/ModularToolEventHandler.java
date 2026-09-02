package io.github.gtbauke.modernmachines.modular.event;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.modular.item.ModularToolItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = ModernMachines.MOD_ID)
public class ModularToolEventHandler {

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        var stack = event.getEntity().getMainHandItem();
        if (!(stack.getItem() instanceof ModularToolItem)) {
            return;
        }

        var speed = event.getNewSpeed();
        var state = event.getState();
        var player = event.getEntity();

        for (var entry : ModularToolItem.getActiveTraits(stack).entrySet()) {
            speed = entry.getKey().modifyMiningSpeed(stack, state, player, speed, entry.getValue());
        }

        event.setNewSpeed(speed);
    }
}
