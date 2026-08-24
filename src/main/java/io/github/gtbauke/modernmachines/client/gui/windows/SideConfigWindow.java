package io.github.gtbauke.modernmachines.client.gui.windows;

import io.github.gtbauke.modernmachines.api.machine.capability.MachineCapabilityType;
import io.github.gtbauke.modernmachines.api.machine.side.ISideConfigurable;
import io.github.gtbauke.modernmachines.api.machine.side.RelativeSide;
import io.github.gtbauke.modernmachines.api.machine.side.SideIoMode;
import io.github.gtbauke.modernmachines.client.gui.core.element.BlockFaceElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.ButtonElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Column;
import io.github.gtbauke.modernmachines.client.gui.core.element.LabelElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Row;
import io.github.gtbauke.modernmachines.client.gui.core.element.Spacer;
import io.github.gtbauke.modernmachines.client.gui.core.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Padding;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.network.ServerboundSideConfigPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SideConfigWindow extends Window {
    public static final int WINDOW_WIDTH = 158;
    public static final int WINDOW_HEIGHT = 148;

    private final ISideConfigurable sideConfigurable;
    private final List<MachineCapabilityType> supportedCapabilities = new ArrayList<>();
    private MachineCapabilityType selectedCapability;

    public SideConfigWindow(ISideConfigurable sideConfigurable, Position initialPosition) {
        super(Component.literal("Side Configuration"), new Bounds(initialPosition, new Size(WINDOW_WIDTH, WINDOW_HEIGHT)), new Padding(0));
        this.sideConfigurable = sideConfigurable;

        setHasHeader(true);
        setHeaderHeight(18);
        setDraggable(true);
        setHasCloseButton(true);
        setVisible(false);
        setPadding(new Padding(8));

        if (sideConfigurable != null) {
            supportedCapabilities.addAll(sideConfigurable.getSupportedCapabilities());
            if (!supportedCapabilities.isEmpty()) {
                selectedCapability = supportedCapabilities.getFirst();
            }
        }

        rebuildContent();
    }

    public void rebuildContent() {
        if (sideConfigurable == null) {
            return;
        }

        var capTabs = getUiElements();
        var capSelectorRow = Row.of(2, AlignItems.CENTER, JustifyContent.CENTER, capTabs.toArray(new UIElement[0]));

        var topFace = new BlockFaceElement(RelativeSide.TOP, () -> getMode(RelativeSide.TOP), this::setMode);
        var bottomFace = new BlockFaceElement(RelativeSide.BOTTOM, () -> getMode(RelativeSide.BOTTOM), this::setMode);
        var leftFace = new BlockFaceElement(RelativeSide.LEFT, () -> getMode(RelativeSide.LEFT), this::setMode);
        var frontFace = new BlockFaceElement(RelativeSide.FRONT, () -> getMode(RelativeSide.FRONT), this::setMode);
        var rightFace = new BlockFaceElement(RelativeSide.RIGHT, () -> getMode(RelativeSide.RIGHT), this::setMode);
        var backFace = new BlockFaceElement(RelativeSide.BACK, () -> getMode(RelativeSide.BACK), this::setMode);

        int slotSize = BlockFaceElement.FACE_SIZE;
        var topRow = Row.of(2, AlignItems.CENTER,
            Spacer.square(slotSize),
            topFace,
            Spacer.square(slotSize),
            Spacer.square(slotSize)
        );

        var middleRow = Row.of(2, AlignItems.CENTER,
            rightFace,
            frontFace,
            leftFace,
            backFace
        );

        var bottomRow = Row.of(2, AlignItems.CENTER,
            Spacer.square(slotSize),
            bottomFace,
            Spacer.square(slotSize),
            Spacer.square(slotSize)
        );

        var crossGrid = Column.of(2, AlignItems.CENTER, topRow, middleRow, bottomRow);

        var autoPullLabel = new LabelElement("Auto Pull").setAlignment(LabelElement.TextAlignment.CENTER).setColor(GUIRenderHelper.ORE_TEXT_MUTED);
        var autoPullBtn = new ButtonElement(
            new Size(46, 14),
            () -> Component.literal(sideConfigurable.getSideConfig().isAutoPull(selectedCapability) ? "ON" : "OFF"),
            () -> sideConfigurable.getSideConfig().isAutoPull(selectedCapability),
            this::toggleAutoPull
        );
        var pullCol = Column.of(2, AlignItems.CENTER, autoPullLabel, autoPullBtn);

        var autoEjectLabel = new LabelElement("Auto Eject").setAlignment(LabelElement.TextAlignment.CENTER).setColor(GUIRenderHelper.ORE_TEXT_MUTED);
        var autoEjectBtn = new ButtonElement(
            new Size(46, 14),
            () -> Component.literal(sideConfigurable.getSideConfig().isAutoEject(selectedCapability) ? "ON" : "OFF"),
            () -> sideConfigurable.getSideConfig().isAutoEject(selectedCapability),
            this::toggleAutoEject
        );
        var ejectCol = Column.of(2, AlignItems.CENTER, autoEjectLabel, autoEjectBtn);

        var autoToggleRow = Row.of(4, AlignItems.CENTER, JustifyContent.CENTER, pullCol, ejectCol);

        var root = Column.of(3, AlignItems.CENTER, JustifyContent.CENTER,
            capSelectorRow,
            Spacer.vertical(2),
            crossGrid,
            Spacer.vertical(2),
            autoToggleRow
        );

        root.setPadding(new Padding(0));
        root.setSize(new Size(
            WINDOW_WIDTH - this.padding.left() - this.padding.right(),
            WINDOW_HEIGHT - this.padding.top() - this.padding.bottom()
        ));

        setContent(root);
    }

    private @NonNull ArrayList<UIElement> getUiElements() {
        var capTabs = new ArrayList<UIElement>();

        for (var cap : supportedCapabilities) {
            int tabW = Math.max(28, (WINDOW_WIDTH - 12) / Math.max(1, supportedCapabilities.size()) - 2);
            var capBtn = new ButtonElement(
                new Size(tabW, 14),
                () -> Component.literal(cap.getDisplayName()),
                () -> selectedCapability == cap,
                () -> {
                    selectedCapability = cap;
                    calculateSize();
                    calculateLayout();
                    markDirty();
                }
            );
            capTabs.add(capBtn);
        }

        return capTabs;
    }

    private SideIoMode getMode(RelativeSide side) {
        if (sideConfigurable == null) {
            return SideIoMode.NONE;
        }

        return sideConfigurable.getSideConfig().getMode(selectedCapability, side);
    }

    private void setMode(RelativeSide side, SideIoMode mode) {
        if (sideConfigurable == null) {
            return;
        }

        sideConfigurable.getSideConfig().setMode(selectedCapability, side, mode);
        if (sideConfigurable.getMachinePos() != null) {
            sendPayload(ServerboundSideConfigPayload.setSide(
                sideConfigurable.getMachinePos(),
                selectedCapability,
                side,
                mode
            ));
        }

        sideConfigurable.onSideConfigChanged();
        markDirty();
    }

    private void toggleAutoPull() {
        if (sideConfigurable == null) {
            return;
        }

        boolean next = !sideConfigurable.getSideConfig().isAutoPull(selectedCapability);
        sideConfigurable.getSideConfig().setAutoPull(selectedCapability, next);
        if (sideConfigurable.getMachinePos() != null) {
            sendPayload(ServerboundSideConfigPayload.setAutoPull(
                sideConfigurable.getMachinePos(),
                selectedCapability,
                next
            ));
        }

        sideConfigurable.onSideConfigChanged();
        markDirty();
    }

    private void toggleAutoEject() {
        if (sideConfigurable == null) {
            return;
        }

        boolean next = !sideConfigurable.getSideConfig().isAutoEject(selectedCapability);
        sideConfigurable.getSideConfig().setAutoEject(selectedCapability, next);
        if (sideConfigurable.getMachinePos() != null) {
            sendPayload(ServerboundSideConfigPayload.setAutoEject(
                sideConfigurable.getMachinePos(),
                selectedCapability,
                next
            ));
        }

        sideConfigurable.onSideConfigChanged();
        markDirty();
    }

    private void sendPayload(CustomPacketPayload payload) {
        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(new ServerboundCustomPayloadPacket(payload));
        }
    }
}
