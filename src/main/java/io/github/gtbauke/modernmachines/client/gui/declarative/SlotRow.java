package io.github.gtbauke.modernmachines.client.gui.declarative;

import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import net.minecraft.world.inventory.Slot;

public class SlotRow extends FlexContainer {
    public SlotRow(int gap, SlotWidget... slots) {
        super(FlexDirection.ROW);
        getFlexNode().setGap(gap);
        for (SlotWidget s : slots) {
            if (s != null) addChild(s);
        }
    }

    public static SlotRow of(SlotWidget... slots) {
        return new SlotRow(3, slots);
    }

    public static SlotRow of(Slot... slots) {
        SlotWidget[] widgets = new SlotWidget[slots.length];
        for (int i = 0; i < slots.length; i++) {
            widgets[i] = SlotWidget.of(slots[i]);
        }
        return new SlotRow(3, widgets);
    }

    public static SlotRow of(int gap, SlotWidget... slots) {
        return new SlotRow(gap, slots);
    }

    public SlotRow gap(int gap) {
        getFlexNode().setGap(gap);
        return this;
    }

    public SlotRow center() {
        getFlexNode().setAlignItems(AlignItems.CENTER);
        getFlexNode().setJustifyContent(JustifyContent.CENTER);
        return this;
    }
}
