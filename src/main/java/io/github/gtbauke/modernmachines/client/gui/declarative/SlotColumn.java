package io.github.gtbauke.modernmachines.client.gui.declarative;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import net.minecraft.world.inventory.Slot;

public class SlotColumn extends FlexContainer {
    public SlotColumn(int gap, SlotWidget... slots) {
        super(FlexDirection.COLUMN);
        getFlexNode().setGap(gap);
        for (SlotWidget s : slots) {
            if (s != null) addChild(s);
        }
    }

    public static SlotColumn of(SlotWidget... slots) {
        return new SlotColumn(3, slots);
    }

    public static SlotColumn of(Slot... slots) {
        SlotWidget[] widgets = new SlotWidget[slots.length];
        for (int i = 0; i < slots.length; i++) {
            widgets[i] = SlotWidget.of(slots[i]);
        }
        return new SlotColumn(3, widgets);
    }

    public static SlotColumn of(int gap, SlotWidget... slots) {
        return new SlotColumn(gap, slots);
    }

    public SlotColumn gap(int gap) {
        getFlexNode().setGap(gap);
        return this;
    }

    public SlotColumn center() {
        getFlexNode().setAlignItems(AlignItems.CENTER);
        getFlexNode().setJustifyContent(JustifyContent.CENTER);
        return this;
    }
}
