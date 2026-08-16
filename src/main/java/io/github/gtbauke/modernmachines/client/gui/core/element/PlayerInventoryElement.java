package io.github.gtbauke.modernmachines.client.gui.core.element;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class PlayerInventoryElement extends UIElement {
    public static final int DEFAULT_X = 8;
    public static final int DEFAULT_Y = 84;
    public static final int WIDTH = 162;
    public static final int MAIN_INV_HEIGHT = 54;
    public static final int HOTBAR_GAP = 4;
    public static final int HOTBAR_HEIGHT = 18;
    public static final int TOTAL_SLOTS_HEIGHT = 76;
    public static final int LABEL_OFFSET = 12;

    private final AbstractContainerMenu menu;
    private final int playerInvStartIndex;
    private boolean showLabel;
    private Component label;
    private int labelColor = 0xFF404040;

    public PlayerInventoryElement(AbstractContainerMenu menu, int playerInvStartIndex, Position position, boolean showLabel) {
        super(new Bounds(position, new Size(WIDTH, TOTAL_SLOTS_HEIGHT + (showLabel ? LABEL_OFFSET : 0))));
        this.menu = menu;
        this.playerInvStartIndex = playerInvStartIndex;
        this.showLabel = showLabel;
        this.label = Component.translatable("container.inventory");
        rebuildSlots();
    }

    public PlayerInventoryElement(AbstractContainerMenu menu, int playerInvStartIndex, Position position) {
        this(menu, playerInvStartIndex, position, false);
    }

    public PlayerInventoryElement(AbstractContainerMenu menu, int playerInvStartIndex, boolean showLabel) {
        this(menu, playerInvStartIndex, new Position(DEFAULT_X, DEFAULT_Y), showLabel);
    }

    public PlayerInventoryElement(AbstractContainerMenu menu, int playerInvStartIndex) {
        this(menu, playerInvStartIndex, new Position(DEFAULT_X, DEFAULT_Y), false);
    }

    public PlayerInventoryElement(Position position) {
        this(null, 0, position, false);
    }

    public PlayerInventoryElement() {
        this(null, 0, new Position(DEFAULT_X, DEFAULT_Y), false);
    }

    public void rebuildSlots() {
        clearChildren();
        int labelY = showLabel ? LABEL_OFFSET : 0;
        setSize(new Size(WIDTH, TOTAL_SLOTS_HEIGHT + labelY));

        Column rootColumn = new Column(HOTBAR_GAP);
        rootColumn.setPosition(new Position(0, labelY));

        // 3x9 Main inventory grid (Column of 3 Rows)
        Column mainInv = new Column(0);
        for (int row = 0; row < 3; row++) {
            Row rowElement = new Row(0);
            for (int col = 0; col < 9; col++) {
                int slotIndex = playerInvStartIndex + col + row * 9;
                Slot slot = (menu != null && slotIndex >= 0 && slotIndex < menu.slots.size()) ? menu.slots.get(slotIndex) : null;
                rowElement.addChild(new SlotElement(slot));
            }
            mainInv.addChild(rowElement);
        }
        rootColumn.addChild(mainInv);

        // 1x9 Hotbar row
        Row hotbarRow = new Row(0);
        for (int col = 0; col < 9; col++) {
            int slotIndex = playerInvStartIndex + 27 + col;
            Slot slot = (menu != null && slotIndex >= 0 && slotIndex < menu.slots.size()) ? menu.slots.get(slotIndex) : null;
            hotbarRow.addChild(new SlotElement(slot));
        }
        rootColumn.addChild(hotbarRow);

        addChild(rootColumn);
    }

    public boolean isShowLabel() {
        return showLabel;
    }

    public PlayerInventoryElement setShowLabel(boolean showLabel) {
        if (this.showLabel != showLabel) {
            this.showLabel = showLabel;
            rebuildSlots();
        }
        return this;
    }

    public Component getLabel() {
        return label;
    }

    public PlayerInventoryElement setLabel(Component label) {
        this.label = label;
        markDirty();
        return this;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public PlayerInventoryElement setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        return this;
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        if (showLabel && label != null) {
            Font font = Minecraft.getInstance().font;
            graphics.text(font, label, absoluteBounds.position().x(), absoluteBounds.position().y(), labelColor, false);
        }
    }
}
