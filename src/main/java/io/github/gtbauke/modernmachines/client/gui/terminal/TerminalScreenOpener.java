package io.github.gtbauke.modernmachines.client.gui.terminal;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TerminalScreenOpener {
    public static void openTerminal() {
        Minecraft.getInstance().setScreenAndShow(new EngineersTerminalScreen(Component.literal("Engineer's Terminal")));
    }
}
