package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class MetricRow extends FlexContainer {
    private final Supplier<Component> labelSupplier;
    private final Supplier<Component> contextSupplier;
    private final LabelWidget mainLabel;
    private final LabelWidget subLabel;

    public MetricRow(Supplier<Component> labelSupplier, Supplier<Component> contextSupplier) {
        super(FlexDirection.COLUMN);
        this.labelSupplier = labelSupplier;
        this.contextSupplier = contextSupplier;
        getFlexNode().setGap(1);
        getFlexNode().setAlignItems(AlignItems.CENTER);

        this.mainLabel = new LabelWidget(Component.empty()).setCentered(true);
        this.subLabel = new LabelWidget(Component.empty()).setCentered(true);

        addChild(mainLabel);
        if (contextSupplier != null) {
            addChild(subLabel);
        }
    }

    public static MetricRow of(Supplier<Component> labelSupplier, Supplier<Component> contextSupplier) {
        return new MetricRow(labelSupplier, contextSupplier);
    }

    public static MetricRow of(Supplier<Component> labelSupplier) {
        return new MetricRow(labelSupplier, null);
    }

    public static MetricRow of(String label, Supplier<String> valueSupplier, Supplier<String> contextSupplier) {
        return new MetricRow(
            () -> Component.literal(label + " " + (valueSupplier != null ? valueSupplier.get() : "")),
            contextSupplier != null ? () -> Component.literal(contextSupplier.get()).withStyle(ChatFormatting.GRAY) : null
        );
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;

        if (labelSupplier != null) {
            mainLabel.setText(labelSupplier.get());
        }
        if (contextSupplier != null) {
            subLabel.setText(contextSupplier.get());
        }

        super.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
    }
}
