package io.github.gtbauke.modernmachines.client.gui.windows;

import io.github.gtbauke.modernmachines.api.machine.capability.MachineCapabilityType;
import io.github.gtbauke.modernmachines.api.machine.side.ISideConfigurable;
import io.github.gtbauke.modernmachines.api.machine.side.RelativeSide;
import io.github.gtbauke.modernmachines.api.machine.side.SideIoMode;
import io.github.gtbauke.modernmachines.client.gui.core.element.BlockFaceElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.ButtonElement;
import io.github.gtbauke.modernmachines.client.gui.core.element.Column;
import io.github.gtbauke.modernmachines.client.gui.core.element.Row;
import io.github.gtbauke.modernmachines.client.gui.core.element.Spacer;
import io.github.gtbauke.modernmachines.client.gui.core.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Padding;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.network.ServerboundSideConfigPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SideConfigWindow extends Window {
    public static final int WINDOW_WIDTH = 110;
    public static final int WINDOW_HEIGHT = 142;

    private final ISideConfigurable sideConfigurable;
    private MachineCapabilityType selectedCapability = MachineCapabilityType.ITEM;
    private final List<MachineCapabilityType> supportedCapabilities = new ArrayList<>();

    public SideConfigWindow(ISideConfigurable sideConfigurable, Position initialPosition) {
        super(Component.literal("Side Config"), new Bounds(initialPosition, new Size(WINDOW_WIDTH, WINDOW_HEIGHT)), new Padding(0));
        this.sideConfigurable = sideConfigurable;

        if (sideConfigurable != null) {
            Set<MachineCapabilityType> caps = sideConfigurable.getSupportedCapabilities();
            if (caps != null && !caps.isEmpty()) {
                this.supportedCapabilities.addAll(caps);
                this.selectedCapability = supportedCapabilities.get(0);
            } else {
                this.supportedCapabilities.add(MachineCapabilityType.ITEM);
            }
        } else {
            this.supportedCapabilities.add(MachineCapabilityType.ITEM);
        }

        setHasHeader(true);
        setHeaderHeight(18);
        setDraggable(true);
        setHasCloseButton(true);
        setVisible(false);

        rebuildContent();
    }

    public void rebuildContent() {
        if (sideConfigurable == null) return;

        // 1. Capability selector tabs
        List<UIElement> capTabs = new ArrayList<>();
        for (MachineCapabilityType cap : supportedCapabilities) {
            int tabW = Math.max(28, (WINDOW_WIDTH - 8) / Math.max(1, supportedCapabilities.size()) - 2);
            ButtonElement capBtn = new ButtonElement(
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
        Row capSelectorRow = Row.of(2, AlignItems.CENTER, capTabs.toArray(new UIElement[0]));

        // 2. Planified (Unfolded Cube) 6-face cross grid
        BlockFaceElement topFace = new BlockFaceElement(RelativeSide.TOP, () -> getMode(RelativeSide.TOP), this::setMode);
        BlockFaceElement bottomFace = new BlockFaceElement(RelativeSide.BOTTOM, () -> getMode(RelativeSide.BOTTOM), this::setMode);
        BlockFaceElement leftFace = new BlockFaceElement(RelativeSide.LEFT, () -> getMode(RelativeSide.LEFT), this::setMode);
        BlockFaceElement frontFace = new BlockFaceElement(RelativeSide.FRONT, () -> getMode(RelativeSide.FRONT), this::setMode);
        BlockFaceElement rightFace = new BlockFaceElement(RelativeSide.RIGHT, () -> getMode(RelativeSide.RIGHT), this::setMode);
        BlockFaceElement backFace = new BlockFaceElement(RelativeSide.BACK, () -> getMode(RelativeSide.BACK), this::setMode);

        Row row1 = Row.of(2, AlignItems.CENTER, Spacer.horizontal(24), topFace, Spacer.horizontal(48));
        Row row2 = Row.of(2, AlignItems.CENTER, leftFace, frontFace, rightFace, backFace);
        Row row3 = Row.of(2, AlignItems.CENTER, Spacer.horizontal(24), bottomFace, Spacer.horizontal(48));

        Column cubeNet = Column.of(2, AlignItems.CENTER, row1, row2, row3);

        // 3. Auto-Pull & Auto-Eject toggles
        ButtonElement autoPullBtn = new ButtonElement(
            new Size(48, 14),
            () -> {
                boolean active = sideConfigurable.getSideConfig().isAutoPull(selectedCapability);
                return Component.literal("Pull: " + (active ? "ON" : "OFF")).withStyle(active ? ChatFormatting.GREEN : ChatFormatting.RED);
            },
            () -> sideConfigurable.getSideConfig().isAutoPull(selectedCapability),
            () -> toggleAutoPull()
        ).withTooltip(Component.literal("Toggle Auto-Input from adjacent blocks"));

        ButtonElement autoEjectBtn = new ButtonElement(
            new Size(48, 14),
            () -> {
                boolean active = sideConfigurable.getSideConfig().isAutoEject(selectedCapability);
                return Component.literal("Eject: " + (active ? "ON" : "OFF")).withStyle(active ? ChatFormatting.GREEN : ChatFormatting.RED);
            },
            () -> sideConfigurable.getSideConfig().isAutoEject(selectedCapability),
            () -> toggleAutoEject()
        ).withTooltip(Component.literal("Toggle Auto-Output to adjacent blocks"));

        Row autoToggleRow = Row.of(4, AlignItems.CENTER, autoPullBtn, autoEjectBtn);

        // Assemble root content
        Column root = Column.of(3, AlignItems.CENTER,
            Spacer.vertical(2),
            capSelectorRow,
            Spacer.vertical(2),
            cubeNet,
            Spacer.vertical(2),
            autoToggleRow
        );
        root.setSize(new Size(WINDOW_WIDTH, WINDOW_HEIGHT - 18));

        setContent(root);
    }

    private SideIoMode getMode(RelativeSide side) {
        if (sideConfigurable == null) return SideIoMode.NONE;
        return sideConfigurable.getSideConfig().getMode(selectedCapability, side);
    }

    private void setMode(RelativeSide side, SideIoMode mode) {
        if (sideConfigurable == null) return;
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
        if (sideConfigurable == null) return;
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
        if (sideConfigurable == null) return;
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
