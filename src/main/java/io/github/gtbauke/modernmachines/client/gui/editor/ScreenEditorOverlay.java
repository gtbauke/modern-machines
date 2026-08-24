package io.github.gtbauke.modernmachines.client.gui.editor;

import io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Position;
import io.github.gtbauke.modernmachines.client.gui.core.layout.Size;
import io.github.gtbauke.modernmachines.client.gui.core.layout.UIElement;
import io.github.gtbauke.modernmachines.client.gui.core.render.GUIRenderHelper;
import io.github.gtbauke.modernmachines.client.gui.editor.canvas.EditorCanvas;
import io.github.gtbauke.modernmachines.client.gui.editor.codegen.JavaCodeGenerator;
import io.github.gtbauke.modernmachines.client.gui.editor.data.ScreenLayoutSerializer;
import io.github.gtbauke.modernmachines.client.gui.editor.inspector.PropertyInspector;
import io.github.gtbauke.modernmachines.client.gui.editor.model.ElementDefinition;
import io.github.gtbauke.modernmachines.client.gui.editor.model.ScreenLayoutDefinition;
import io.github.gtbauke.modernmachines.client.gui.editor.model.TabDefinition;
import io.github.gtbauke.modernmachines.client.gui.editor.palette.ElementPalette;
import io.github.gtbauke.modernmachines.client.gui.editor.picker.ItemPickerModal;
import io.github.gtbauke.modernmachines.client.gui.editor.picker.TabTemplateModal;
import io.github.gtbauke.modernmachines.client.gui.screen.ModularContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

public class ScreenEditorOverlay extends UIElement {
    public static final int TOOLBAR_HEIGHT = 26;

    public enum InteractionMode {
        MOVE,
        RESIZE
    }

    private final ModularContainerScreen<?> containerScreen;
    private ScreenLayoutDefinition activeLayout;
    private TabDefinition activeTab = null;
    private ElementDefinition selectedElement;
    private boolean snapToGrid = true;
    private InteractionMode interactionMode = InteractionMode.MOVE;
    private boolean previewMode = false;

    private final ElementPalette palette;
    private final EditorCanvas canvas;
    private final PropertyInspector inspector;
    private ItemPickerModal activeItemPicker = null;
    private TabTemplateModal activeTabTemplateModal = null;

    private final Deque<String> undoStack = new ArrayDeque<>();
    private final Deque<String> redoStack = new Deque<>() {
        private final ArrayDeque<String> delegate = new ArrayDeque<>();
        public void push(String s) { delegate.push(s); }
        public String pop() { return delegate.pop(); }
        public boolean isEmpty() { return delegate.isEmpty(); }
        public void clear() { delegate.clear(); }
        public int size() { return delegate.size(); }
        public String peek() { return delegate.peek(); }
        public boolean removeLastOccurrence(Object o) { return delegate.removeLastOccurrence(o); }
        public boolean removeFirstOccurrence(Object o) { return delegate.removeFirstOccurrence(o); }
        public boolean offerFirst(String s) { return delegate.offerFirst(s); }
        public boolean offerLast(String s) { return delegate.offerLast(s); }
        public String removeFirst() { return delegate.removeFirst(); }
        public String removeLast() { return delegate.removeLast(); }
        public String pollFirst() { return delegate.pollFirst(); }
        public String pollLast() { return delegate.pollLast(); }
        public String getFirst() { return delegate.getFirst(); }
        public String getLast() { return delegate.getLast(); }
        public String peekFirst() { return delegate.peekFirst(); }
        public String peekLast() { return delegate.peekLast(); }
        public boolean offer(String s) { return delegate.offer(s); }
        public String remove() { return delegate.remove(); }
        public String poll() { return delegate.poll(); }
        public String element() { return delegate.element(); }
        public java.util.Iterator<String> iterator() { return delegate.iterator(); }
        public java.util.Iterator<String> descendingIterator() { return delegate.descendingIterator(); }
        public boolean add(String s) { return delegate.add(s); }
        public void addFirst(String s) { delegate.addFirst(s); }
        public void addLast(String s) { delegate.addLast(s); }
        public boolean remove(Object o) { return delegate.remove(o); }
        public boolean contains(Object o) { return delegate.contains(o); }
        public Object[] toArray() { return delegate.toArray(); }
        public <T> T[] toArray(T[] a) { return delegate.toArray(a); }
        public boolean containsAll(java.util.Collection<?> c) { return delegate.containsAll(c); }
        public boolean addAll(java.util.Collection<? extends String> c) { return delegate.addAll(c); }
        public boolean removeAll(java.util.Collection<?> c) { return delegate.removeAll(c); }
        public boolean retainAll(java.util.Collection<?> c) { return delegate.retainAll(c); }
    };

    private String toastMessage = null;
    private long toastExpiryTime = 0;

    public ScreenEditorOverlay(ModularContainerScreen<?> containerScreen) {
        super(new Bounds(Position.ZERO, new Size(containerScreen.width, containerScreen.height)));
        this.containerScreen = containerScreen;

        this.activeLayout = initializeDefaultLayout(containerScreen);

        this.palette = new ElementPalette(this, this::spawnElement);
        this.canvas = new EditorCanvas(this);
        this.inspector = new PropertyInspector(this);

        addChild(this.palette);
        addChild(this.canvas);
        addChild(this.inspector);

        recalculateLayoutBounds(containerScreen.width, containerScreen.height);
    }

    private ScreenLayoutDefinition initializeDefaultLayout(ModularContainerScreen<?> screen) {
        var screenName = screen.getClass().getSimpleName();
        var existing = ScreenLayoutSerializer.loadLayout(screenName);
        ScreenLayoutDefinition layout;
        if (existing.isPresent()) {
            layout = existing.get();
            layout.setImageWidth(screen.getImageWidth());
            layout.setImageHeight(screen.getImageHeight());
            layout.setTitle(screen.getTitle().getString());
        } else {
            layout = new ScreenLayoutDefinition(screenName, screen.getTitle().getString(), screen.getImageWidth(), screen.getImageHeight());

            var mainCol = new ElementDefinition(ElementDefinition.ElementType.COLUMN);
            mainCol.setWidth(screen.getImageWidth() - 16);
            mainCol.setHeight(screen.getImageHeight() - 16);
            mainCol.setX(8);
            mainCol.setY(16);

            var playerInv = new ElementDefinition(ElementDefinition.ElementType.PLAYER_INVENTORY);
            playerInv.setX(8);
            playerInv.setY(84);

            layout.addElement(mainCol);
            layout.addElement(playerInv);
        }

        // Extract and recognize any existing SideTabElements from the machine screen
        if (screen.getMainWindow() != null) {
            for (var child : screen.getMainWindow().getChildren()) {
                if (child instanceof io.github.gtbauke.modernmachines.client.gui.core.element.SideTabElement sideTab) {
                    var win = sideTab.getTargetWindow();
                    var title = (win != null && win.getTitle() != null) ? win.getTitle().getString() : (sideTab.getTooltip() != null ? sideTab.getTooltip().getString() : "Tab");

                    boolean exists = layout.getTabs().stream().anyMatch(t -> t.getTitle().equalsIgnoreCase(title));
                    if (!exists) {
                        String iconId = "minecraft:redstone";
                        if (sideTab.getIconItem() != null && !sideTab.getIconItem().isEmpty()) {
                            var regKey = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(sideTab.getIconItem().getItem());
                            if (regKey != null) {
                                iconId = regKey.toString();
                            }
                        }

                        int winW = win != null ? win.getBounds().size().width() : 120;
                        int winH = win != null ? win.getBounds().size().height() : 100;
                        String side = sideTab.isLeftSided() ? "LEFT" : "RIGHT";
                        String tooltip = sideTab.getTooltip() != null ? sideTab.getTooltip().getString() : title;

                        var tabDef = new TabDefinition(null, title, iconId, tooltip, side, winW, winH);
                        layout.addTab(tabDef);
                    }
                }
            }
        }

        return layout;
    }

    public void recalculateLayoutBounds(int screenWidth, int screenHeight) {
        setSize(new Size(screenWidth, screenHeight));

        if (previewMode) {
            canvas.setPosition(Position.ZERO);
            canvas.setSize(new Size(screenWidth, screenHeight));
            canvas.calculateLayout();
            return;
        }

        int availHeight = screenHeight - TOOLBAR_HEIGHT;

        // Palette on the left
        palette.setPosition(new Position(4, TOOLBAR_HEIGHT + 4));
        palette.setSize(new Size(ElementPalette.PALETTE_WIDTH, availHeight - 8));

        // Inspector on the right
        inspector.setPosition(new Position(screenWidth - PropertyInspector.INSPECTOR_WIDTH - 4, TOOLBAR_HEIGHT + 4));
        inspector.setSize(new Size(PropertyInspector.INSPECTOR_WIDTH, availHeight - 8));

        // Canvas in center
        int canvasX = ElementPalette.PALETTE_WIDTH + 8;
        int canvasW = screenWidth - ElementPalette.PALETTE_WIDTH - PropertyInspector.INSPECTOR_WIDTH - 16;
        canvas.setPosition(new Position(canvasX, TOOLBAR_HEIGHT + 4));
        canvas.setSize(new Size(canvasW, availHeight - 8));

        if (activeItemPicker != null) {
            int modalX = (screenWidth - ItemPickerModal.MODAL_WIDTH) / 2;
            int modalY = (screenHeight - ItemPickerModal.MODAL_HEIGHT) / 2;
            activeItemPicker.setPosition(new Position(modalX, modalY));
            activeItemPicker.calculateLayout();
        }

        palette.calculateLayout();
        inspector.calculateLayout();
        canvas.calculateLayout();
    }

    public boolean isPreviewMode() {
        return previewMode;
    }

    public void setPreviewMode(boolean previewMode) {
        this.previewMode = previewMode;
        if (previewMode) {
            this.selectedElement = null;
        }

        recalculateLayoutBounds(this.bounds.size().width(), this.bounds.size().height());
        showToast(previewMode ? "In-Game Preview (Press P or Esc to exit)" : "Returned to Editor");
        markDirty();
    }

    public ScreenLayoutDefinition getActiveLayout() {
        return activeLayout;
    }

    public TabDefinition getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(TabDefinition activeTab) {
        this.activeTab = activeTab;
        this.selectedElement = null;
        markDirty();
    }

    public ElementDefinition getSelectedElement() {
        return selectedElement;
    }

    public void setSelectedElement(ElementDefinition selectedElement) {
        this.selectedElement = selectedElement;
        markDirty();
    }

    public boolean isSnapToGrid() {
        return snapToGrid;
    }

    public void setSnapToGrid(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }

    public InteractionMode getInteractionMode() {
        return interactionMode;
    }

    public void setInteractionMode(InteractionMode interactionMode) {
        this.interactionMode = interactionMode != null ? interactionMode : InteractionMode.MOVE;
        showToast("Mode: " + this.interactionMode.name());
        markDirty();
    }

    public void openItemPicker(Consumer<String> onSelect) {
        this.activeItemPicker = new ItemPickerModal(
            onSelect,
            () -> {
                this.activeItemPicker = null;
                markDirty();
            }
        );
        int modalX = (this.bounds.size().width() - ItemPickerModal.MODAL_WIDTH) / 2;
        int modalY = (this.bounds.size().height() - ItemPickerModal.MODAL_HEIGHT) / 2;
        this.activeItemPicker.setPosition(new Position(modalX, modalY));
        this.activeItemPicker.calculateLayout();
        markDirty();
    }

    public boolean isItemPickerOpen() {
        return activeItemPicker != null;
    }

    public void closeItemPicker() {
        this.activeItemPicker = null;
        markDirty();
    }

    public ItemPickerModal getActiveItemPicker() {
        return activeItemPicker;
    }

    public void openTabTemplateModal(Consumer<TabDefinition> onSelect) {
        this.activeTabTemplateModal = new TabTemplateModal(
            onSelect,
            () -> {
                this.activeTabTemplateModal = null;
                markDirty();
            }
        );
        int modalX = (this.bounds.size().width() - TabTemplateModal.MODAL_WIDTH) / 2;
        int modalY = (this.bounds.size().height() - TabTemplateModal.MODAL_HEIGHT) / 2;
        this.activeTabTemplateModal.setPosition(new Position(modalX, modalY));
        this.activeTabTemplateModal.calculateLayout();
        markDirty();
    }

    public boolean isTabTemplateModalOpen() {
        return activeTabTemplateModal != null;
    }

    public void closeTabTemplateModal() {
        this.activeTabTemplateModal = null;
        markDirty();
    }

    public TabTemplateModal getActiveTabTemplateModal() {
        return activeTabTemplateModal;
    }

    public PropertyInspector getInspector() {
        return inspector;
    }

    public void spawnElement(ElementDefinition def) {
        saveUndoState();

        if (selectedElement != null && (selectedElement.getType() == ElementDefinition.ElementType.COLUMN || selectedElement.getType() == ElementDefinition.ElementType.ROW)) {
            selectedElement.addChild(def);
            this.selectedElement = def;
            showToast("Added " + def.getType().name() + " to " + selectedElement.getType().name());
            markDirty();
            return;
        }

        var bounds = canvas.getCanvasActiveBounds();
        def.setX(bounds.size().width() / 2 - def.getWidth() / 2);
        def.setY(bounds.size().height() / 2 - def.getHeight() / 2);

        if (activeTab != null) {
            activeTab.addElement(def);
        } else {
            activeLayout.addElement(def);
        }

        this.selectedElement = def;
        markDirty();
    }

    public void deleteSelectedElement() {
        if (selectedElement != null) {
            saveUndoState();
            var targetList = activeTab != null ? activeTab.getElements() : activeLayout.getElements();
            removeElementRecursive(targetList, selectedElement);
            selectedElement = null;
            markDirty();
        }
    }

    private boolean removeElementRecursive(java.util.List<ElementDefinition> list, ElementDefinition target) {
        if (list.remove(target)) {
            return true;
        }

        for (var el : list) {
            if (removeElementRecursive(el.getChildren(), target)) {
                return true;
            }
        }

        return false;
    }

    public void saveUndoState() {
        undoStack.push(ScreenLayoutSerializer.toJsonString(activeLayout));
        redoStack.clear();
        if (undoStack.size() > 20) {
            undoStack.removeLast();
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(ScreenLayoutSerializer.toJsonString(activeLayout));
            activeLayout = ScreenLayoutSerializer.fromJsonString(undoStack.pop());
            selectedElement = null;
            activeTab = null;
            showToast("Undo");
            markDirty();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(ScreenLayoutSerializer.toJsonString(activeLayout));
            activeLayout = ScreenLayoutSerializer.fromJsonString(redoStack.pop());
            selectedElement = null;
            activeTab = null;
            showToast("Redo");
            markDirty();
        }
    }

    public void showToast(String message) {
        this.toastMessage = message;
        this.toastExpiryTime = System.currentTimeMillis() + 3000;
    }

    public void saveLayoutToFile() {
        boolean saved = ScreenLayoutSerializer.saveLayout(activeLayout);
        if (saved) {
            showToast("Saved to config/modernmachines/screens/" + activeLayout.getScreenId() + ".json");
        } else {
            showToast("Failed to save layout!");
        }
    }

    public void exportJavaCode() {
        var code = JavaCodeGenerator.generateBuildContentMethod(activeLayout);
        Minecraft.getInstance().keyboardHandler.setClipboard(code);
        showToast("Java code copied to clipboard!");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (activeItemPicker != null) {
            return activeItemPicker.mouseClicked(mouseX, mouseY, button);
        }

        if (activeTabTemplateModal != null) {
            return activeTabTemplateModal.mouseClicked(mouseX, mouseY, button);
        }

        if (previewMode) {
            int mx = (int) mouseX;
            int my = (int) mouseY;

            // Exit Preview banner button at top center
            var exitBtn = new Bounds(new Position(this.bounds.size().width() / 2 - 80, 6), new Size(160, 18));
            if (exitBtn.contains(new Position(mx, my))) {
                setPreviewMode(false);
                return true;
            }

            return canvas.mouseClicked(mouseX, mouseY, button);
        }

        if (button == 0 && mouseY < TOOLBAR_HEIGHT) {
            int mx = (int) mouseX;
            int my = (int) mouseY;

            // 1. Grid Snap Toggle
            var snapBtn = new Bounds(new Position(6, 4), new Size(60, 18));
            if (snapBtn.contains(new Position(mx, my))) {
                snapToGrid = !snapToGrid;
                showToast("Grid Snap: " + (snapToGrid ? "ON" : "OFF"));
                return true;
            }

            // 2. Interaction Mode Toggle (Move vs Resize)
            var modeBtn = new Bounds(new Position(70, 4), new Size(74, 18));
            if (modeBtn.contains(new Position(mx, my))) {
                setInteractionMode(interactionMode == InteractionMode.MOVE ? InteractionMode.RESIZE : InteractionMode.MOVE);
                return true;
            }

            // 3. View Switcher: [Main Screen]
            var mainViewBtn = new Bounds(new Position(148, 4), new Size(62, 18));
            if (mainViewBtn.contains(new Position(mx, my))) {
                setActiveTab(null);
                showToast("Editing: Main Screen");
                return true;
            }

            // Tabs buttons
            int tabBtnX = 214;
            for (var tab : activeLayout.getTabs()) {
                var tabBtn = new Bounds(new Position(tabBtnX, 4), new Size(54, 18));
                if (tabBtn.contains(new Position(mx, my))) {
                    setActiveTab(tab);
                    showToast("Editing Tab: " + tab.getTitle());
                    return true;
                }

                tabBtnX += 58;
            }

            // [+ Tab] Add new configured side tab
            var addTabBtn = new Bounds(new Position(tabBtnX, 4), new Size(38, 18));
            if (addTabBtn.contains(new Position(mx, my))) {
                openTabTemplateModal(selectedTab -> {
                    saveUndoState();
                    activeLayout.addTab(selectedTab);
                    setActiveTab(selectedTab);
                    showToast("Added " + selectedTab.getTitle());
                });
                return true;
            }

            // Preview button (👁 Preview)
            var previewBtn = new Bounds(new Position(this.bounds.size().width() - 248, 4), new Size(68, 18));
            if (previewBtn.contains(new Position(mx, my))) {
                setPreviewMode(true);
                return true;
            }

            // Save JSON button
            var saveBtn = new Bounds(new Position(this.bounds.size().width() - 176, 4), new Size(54, 18));
            if (saveBtn.contains(new Position(mx, my))) {
                saveLayoutToFile();
                return true;
            }

            // Export Java button
            var exportBtn = new Bounds(new Position(this.bounds.size().width() - 118, 4), new Size(68, 18));
            if (exportBtn.contains(new Position(mx, my))) {
                exportJavaCode();
                return true;
            }

            // Close button
            var closeBtn = new Bounds(new Position(this.bounds.size().width() - 22, 4), new Size(18, 18));
            if (closeBtn.contains(new Position(mx, my))) {
                containerScreen.toggleScreenEditor();
                return true;
            }

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (activeItemPicker != null) {
            return activeItemPicker.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        if (activeTabTemplateModal != null) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void renderSelf(GuiGraphicsExtractor graphics, Bounds absoluteBounds, int mouseX, int mouseY, float partialTick) {
        var font = Minecraft.getInstance().font;

        // Preview Mode
        if (previewMode) {
            graphics.fill(0, 0, absoluteBounds.right(), absoluteBounds.bottom(), 0x70000000);

            // Floating Top Banner
            var banner = new Bounds(new Position(absoluteBounds.size().width() / 2 - 90, 6), new Size(180, 18));
            boolean bannerHov = banner.contains(new Position(mouseX, mouseY));
            GUIRenderHelper.drawOreUIPanel(graphics, banner);
            GUIRenderHelper.drawRectOutline(graphics, banner, bannerHov ? GUIRenderHelper.ORE_GREEN_HOVER : GUIRenderHelper.ORE_GREEN_PRIMARY);
            graphics.text(font, Component.literal("👁 In-Game Preview (Exit: P)"), banner.position().x() + 10, banner.position().y() + 5, GUIRenderHelper.ORE_TEXT_TITLE, true);

            // Toast notification banner in preview
            if (toastMessage != null && System.currentTimeMillis() < toastExpiryTime) {
                int toastW = font.width(toastMessage) + 16;
                var toastBounds = new Bounds(new Position(absoluteBounds.size().width() / 2 - toastW / 2, absoluteBounds.bottom() - 28), new Size(toastW, 18));
                GUIRenderHelper.drawOreUIPanel(graphics, toastBounds);
                GUIRenderHelper.drawRectOutline(graphics, toastBounds, GUIRenderHelper.ORE_GREEN_PRIMARY);
                graphics.text(font, Component.literal(toastMessage), toastBounds.position().x() + 8, toastBounds.position().y() + 5, GUIRenderHelper.ORE_TEXT_TITLE, true);
            }

            return;
        }

        // 1. Dark workspace backdrop
        graphics.fill(0, 0, absoluteBounds.right(), absoluteBounds.bottom(), 0xD0101012);

        // 2. Top Toolbar bar
        var toolbarBounds = new Bounds(Position.ZERO, new Size(absoluteBounds.size().width(), TOOLBAR_HEIGHT));
        GUIRenderHelper.drawOreUIPanel(graphics, toolbarBounds);

        // Snap toggle button
        var snapBtn = new Bounds(new Position(6, 4), new Size(60, 18));
        boolean snapHov = snapBtn.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, snapBtn, snapHov, snapToGrid, snapToGrid);
        graphics.text(font, Component.literal("Snap: " + (snapToGrid ? "ON" : "OFF")), snapBtn.position().x() + 6, snapBtn.position().y() + 5, snapToGrid ? 0xFFFFFFFF : GUIRenderHelper.ORE_TEXT_MUTED, false);

        // Mode toggle button
        var modeBtn = new Bounds(new Position(70, 4), new Size(74, 18));
        boolean modeHov = modeBtn.contains(new Position(mouseX, mouseY));
        boolean isResize = interactionMode == InteractionMode.RESIZE;
        GUIRenderHelper.drawOreUIButton(graphics, modeBtn, modeHov, isResize, isResize);
        graphics.text(font, Component.literal("Mode: " + interactionMode.name()), modeBtn.position().x() + 5, modeBtn.position().y() + 5, isResize ? 0xFFFFFFFF : GUIRenderHelper.ORE_TEXT_TITLE, false);

        // [Main Screen] View Tab
        var mainViewBtn = new Bounds(new Position(148, 4), new Size(62, 18));
        boolean mainHov = mainViewBtn.contains(new Position(mouseX, mouseY));
        boolean mainActive = activeTab == null;
        GUIRenderHelper.drawOreUIButton(graphics, mainViewBtn, mainHov, mainActive, mainActive);
        graphics.text(font, Component.literal("Main UI"), mainViewBtn.position().x() + 8, mainViewBtn.position().y() + 5, mainActive ? 0xFFFFFFFF : GUIRenderHelper.ORE_TEXT_MUTED, false);

        // Configured Tabs
        int tabBtnX = 214;
        for (var tab : activeLayout.getTabs()) {
            var tabBtn = new Bounds(new Position(tabBtnX, 4), new Size(54, 18));
            boolean tabHov = tabBtn.contains(new Position(mouseX, mouseY));
            boolean tabActive = activeTab == tab;
            GUIRenderHelper.drawOreUIButton(graphics, tabBtn, tabHov, tabActive, tabActive);
            String titleShort = tab.getTitle().length() > 6 ? tab.getTitle().substring(0, 6) : tab.getTitle();
            graphics.text(font, Component.literal(titleShort), tabBtn.position().x() + 5, tabBtn.position().y() + 5, tabActive ? 0xFFFFFFFF : GUIRenderHelper.ORE_TEXT_MUTED, false);
            tabBtnX += 58;
        }

        // [+ Tab] Button
        var addTabBtn = new Bounds(new Position(tabBtnX, 4), new Size(38, 18));
        boolean addTabHov = addTabBtn.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, addTabBtn, addTabHov, false, false);
        graphics.text(font, Component.literal("+ Tab"), addTabBtn.position().x() + 4, addTabBtn.position().y() + 5, addTabHov ? 0xFFFFFFFF : GUIRenderHelper.ORE_TEXT_MUTED, false);

        // Preview button
        var previewBtn = new Bounds(new Position(absoluteBounds.size().width() - 248, 4), new Size(68, 18));
        boolean prevHov = previewBtn.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, previewBtn, prevHov, false, false);
        graphics.text(font, Component.literal("👁 Preview"), previewBtn.position().x() + 6, previewBtn.position().y() + 5, prevHov ? 0xFFFFFFFF : GUIRenderHelper.ORE_TEXT_TITLE, false);

        // Save JSON button
        var saveBtn = new Bounds(new Position(absoluteBounds.size().width() - 176, 4), new Size(54, 18));
        boolean saveHov = saveBtn.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, saveBtn, saveHov, false, true);
        graphics.text(font, Component.literal("Save"), saveBtn.position().x() + 10, saveBtn.position().y() + 5, 0xFFFFFFFF, false);

        // Export Java button
        var exportBtn = new Bounds(new Position(absoluteBounds.size().width() - 118, 4), new Size(68, 18));
        boolean expHov = exportBtn.contains(new Position(mouseX, mouseY));
        GUIRenderHelper.drawOreUIButton(graphics, exportBtn, expHov, false, false);
        graphics.text(font, Component.literal("Export Java"), exportBtn.position().x() + 4, exportBtn.position().y() + 5, expHov ? 0xFFFFFFFF : GUIRenderHelper.ORE_TEXT_TITLE, false);

        // Close button (top right)
        var closeBtn = new Bounds(new Position(absoluteBounds.size().width() - 22, 4), new Size(18, 18));
        boolean closeHov = closeBtn.contains(new Position(mouseX, mouseY));
        graphics.fill(closeBtn.position().x(), closeBtn.position().y(), closeBtn.right(), closeBtn.bottom(), closeHov ? 0xFFE81123 : GUIRenderHelper.ORE_BUTTON_BG);
        GUIRenderHelper.drawRectOutline(graphics, closeBtn, GUIRenderHelper.ORE_BORDER_DARK);
        graphics.text(font, Component.literal("✕"), closeBtn.position().x() + 5, closeBtn.position().y() + 5, closeHov ? 0xFFFFFFFF : GUIRenderHelper.ORE_TEXT_MUTED, false);

        // 3. Toast notification banner
        if (toastMessage != null && System.currentTimeMillis() < toastExpiryTime) {
            int toastW = font.width(toastMessage) + 16;
            var toastBounds = new Bounds(new Position(absoluteBounds.size().width() / 2 - toastW / 2, absoluteBounds.bottom() - 28), new Size(toastW, 18));
            GUIRenderHelper.drawOreUIPanel(graphics, toastBounds);
            GUIRenderHelper.drawRectOutline(graphics, toastBounds, GUIRenderHelper.ORE_GREEN_PRIMARY);
            graphics.text(font, Component.literal(toastMessage), toastBounds.position().x() + 8, toastBounds.position().y() + 5, GUIRenderHelper.ORE_TEXT_TITLE, true);
        }
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, Position parentOrigin, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, parentOrigin, mouseX, mouseY, partialTick);

        // Render modals on top of ALL children (palette, canvas, inspector)
        if (activeItemPicker != null) {
            graphics.fill(0, 0, this.bounds.size().width(), this.bounds.size().height(), 0x70000000);
            activeItemPicker.render(graphics, Position.ZERO, mouseX, mouseY, partialTick);
        }

        if (activeTabTemplateModal != null) {
            activeTabTemplateModal.render(graphics, Position.ZERO, mouseX, mouseY, partialTick);
        }
    }
}
