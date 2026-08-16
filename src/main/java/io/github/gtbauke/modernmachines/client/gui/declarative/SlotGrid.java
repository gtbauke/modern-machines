package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.List;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.SlotWidget;
import net.minecraft.world.inventory.Slot;

public class SlotGrid extends FlexContainer {
    public SlotGrid(int cols, int rows, int gap, SlotWidget... slots) {
        super(FlexDirection.COLUMN);
        getFlexNode().setGap(gap);

        int idx = 0;
        for (int r = 0; r < rows; r++) {
            FlexContainer row = new FlexContainer(FlexDirection.ROW);
            row.getFlexNode().setGap(gap);
            for (int c = 0; c < cols; c++) {
                if (idx < slots.length && slots[idx] != null) {
                    row.addChild(slots[idx]);
                }
                idx++;
            }
            addChild(row);
        }
    }

    public static SlotGrid of(int cols, int rows, SlotWidget... slots) {
        return new SlotGrid(cols, rows, 3, slots);
    }

    public static SlotGrid of(int cols, int rows, int gap, SlotWidget... slots) {
        return new SlotGrid(cols, rows, gap, slots);
    }

    public static SlotGrid of(int cols, int rows, List<Slot> slots) {
        SlotWidget[] widgets = new SlotWidget[slots.size()];
        for (int i = 0; i < slots.size(); i++) {
            widgets[i] = SlotWidget.of(slots.get(i));
        }
        return new SlotGrid(cols, rows, 3, widgets);
    }
}
