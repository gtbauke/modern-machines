package io.github.gtbauke.modernmachines.client.gui.declarative;

import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class MetricCard extends Card {

    public MetricCard(Component header, UiWidget... metrics) {
        super(createColumn(header, metrics));
        padding(FlexInsets.of(6, 6, 6, 6));
        matchParentWidth();
    }

    private static Column createColumn(Component header, UiWidget... metrics) {
        Column col = Column.of().gap(2).center();
        if (header != null) {
            col.addChild(LabelWidget.of(header).centered());
        }
        for (UiWidget m : metrics) {
            if (m != null) col.addChild(m);
        }
        return col;
    }

    public static MetricCard of(Component header, UiWidget... metrics) {
        return new MetricCard(header, metrics);
    }

    public static MetricCard of(String header, UiWidget... metrics) {
        return new MetricCard(Component.literal(header).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), metrics);
    }

    public static MetricCard of(UiWidget... metrics) {
        return new MetricCard(null, metrics);
    }
}
