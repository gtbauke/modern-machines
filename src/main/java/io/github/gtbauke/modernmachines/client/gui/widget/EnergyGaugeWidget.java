package io.github.gtbauke.modernmachines.client.gui.widget;

import java.util.List;
import java.util.function.IntSupplier;

import io.github.gtbauke.modernmachines.client.gui.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public class EnergyGaugeWidget extends UiWidget {
    private final IntSupplier energySupplier;
    private final IntSupplier maxEnergySupplier;

    public EnergyGaugeWidget(IntSupplier energySupplier, IntSupplier maxEnergySupplier) {
        this.energySupplier = energySupplier;
        this.maxEnergySupplier = maxEnergySupplier;
        flexNode.setSize(14, 52);
    }

    public int getEnergy() {
        return energySupplier.getAsInt();
    }

    public int getMaxEnergy() {
        return maxEnergySupplier.getAsInt();
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        int energy = getEnergy();
        int max = getMaxEnergy();
        int percent = max > 0 ? (int) ((energy * 100L) / max) : 0;

        return List.of(
                Component.literal("Energy Buffer").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                Component.literal(String.format("%,d / %,d FE (%d%%)", energy, max, percent)).withStyle(ChatFormatting.GREEN)
        );
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        Bounds b = getBounds();
        int energy = getEnergy();
        int max = getMaxEnergy();

        // 1. Draw frame
        graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, b.x(), b.y(), 0.0F, 160.0F, 14, 52, 256, 256);

        // 2. Draw energy fill
        if (max > 0 && energy > 0) {
            int fillHeight = Math.min(50, Math.max(1, (int) ((long) energy * 50 / max)));
            graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS,
                    b.x() + 1, b.y() + 1 + (50 - fillHeight),
                    14.0F, 160.0F + (50 - fillHeight),
                    12, fillHeight, 256, 256);
        }

        // 3. Draw energy icon overlay
        graphics.blit(RenderPipelines.GUI_TEXTURED, NineSliceRenderer.GUI_ATLAS, b.x() - 1, b.y() + 18, 0.0F, 96.0F, 16, 16, 256, 256);
    }
}
