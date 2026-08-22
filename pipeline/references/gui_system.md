# Custom Flexbox GUI Framework Guide (Layer 3 Reference)

Modern Machines implements a declarative Flexbox layout engine and windowing system for Minecraft screens (`io.github.gtbauke.modernmachines.client.gui.*`).

---

## 1. Core Architecture

Screens extend `ModularContainerScreen<T>`:
```java
public class ElectricCrusherScreen extends ModularContainerScreen<ElectricCrusherMenu> {
    public ElectricCrusherScreen(ElectricCrusherMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
    }

    @Override
    protected UIElement buildContent() {
        return new Column(
            new Row(
                new SlotElement(0, 0), // Input slot
                new ProgressBarElement(this::getProgressRatio),
                new SlotElement(0, 1)  // Output slot
            ).setJustifyContent(JustifyContent.SPACE_BETWEEN),
            new Spacer(10),
            new PlayerInventoryElement()
        );
    }
}
```

---

## 2. Flexbox Layout Components

Located in `io.github.gtbauke.modernmachines.client.gui.core.element.*`:
- **`Row`** / **`Column`**: Flex containers arranging children horizontally or vertically.
- **`SlotElement`**: Renders standard Minecraft 18x18 item slot frames.
- **`ProgressBarElement`**: Dynamic progress arrow or energy gauge linked to a supplier.
- **`BurningElement`**: Animated flame icon for thermal/fuel combustion.
- **`ButtonElement`**: Interactive button with click handlers and hover states.
- **`SideTabElement`**: Collapsible tab anchored to the screen border (used for side config, power stats).
- **`BlockFaceElement`**: Interactive 3D/2D block face icon for IO mode toggling.
- **`PlayerInventoryElement`**: Automatically places the 3x9 player inventory and 1x9 hotbar grid.

---

## 3. UI Atlas & NineSlice Rendering

The GUI relies on a 256x256 sprite atlas generated via `scripts/generate_gui_atlas.py`:
- Texture Location: `textures/gui/gui_atlas.png`
- **`NineSliceRenderer`**: Dynamically scales panels, window frames, and dialog boxes without distorting corner borders.
- Standard slice borders: 4px corners for small panels, 6px corners for main windows.
