# Modern Machines: API Client GUI System Implementation Plan

This document details the architectural analysis of the `io.github.gtbauke.modernmachines.api.client.gui` package and outlines the implementation guide for the new Modular UI System.

---

## 1. Subsystem Analysis (`api.client.gui`)

| Class / Component | Current State | Required Implementations |
| :--- | :--- | :--- |
| **`UIElement`** | Holds `position`, `size`, `padding`, `visibility`, `backgroundColor`, `borderColor`, `flowWeight`, and `children`. | • Add `public int flowWeight()` getter.<br>• Add `protected boolean autoSize` flag (defaults to `true`; set to `false` when explicit size is assigned).<br>• Add `calculateSize()` (measurement pass) and `calculateLayout()` (layout pass) lifecycle methods.<br>• Add `parent` element tracking (`getParent()` / `setParent()`).<br>• Add hit-testing / hover query utility (`isHovered(Position mousePos)`). |
| **`FlexContainer`** | Holds `flexDirection`, `justifyContent`, `alignItems`, and `gap`. Positions children sequentially in `renderChildren()`. | • **Implement two-phase layout engine**:<br>&nbsp;&nbsp;1. `calculateSize()`: Recursive bottom-up sizing for content-based shrink-to-fit.<br>&nbsp;&nbsp;2. `calculateLayout()`: Proportional `flowWeight` distribution, cross-axis stretching/alignment (`AlignItems`), and main-axis justification (`JustifyContent`).<br>• Decouple layout calculation from render passes. |
| **`ModularContainerScreen`** | Calculates `leftPos` and `topPos` in `init()`; calls `buildContent()`. Overrides `extractBackground()`. | • Set root position to `(leftPos, topPos)` in `init()`.<br>• Trigger initial `calculateSize()` and `calculateLayout()` on `root` during `init()`.<br>• Render `root` in `extractBackground(graphics, mousePos, partialTick)`.<br>• Suppress default container title/inventory text labels (`titleLabelX = -9999`, `inventoryLabelX = -9999`).<br>• Forward mouse/keyboard input events to the UI tree. |
| **`NineSliceRenderer`** | 9-slice texture sprite drawing helper. | • Fix import: Change `io.github.gtbauke.modernmachines.client.gui.core.layout.Bounds` to `io.github.gtbauke.modernmachines.api.client.gui.core.Bounds`. |
| **`GUIRenderHelper`** | Draw utilities for boxes, borders, Ore UI panels, vanilla slots, and tabs. | • Fully functional and ready to use. |

---

## 2. Layout Engine Architecture (Two-Phase Pipeline)

### Phase 1: Measurement Pass (`calculateSize()` — Bottom-Up)
1. **Recursive Child Traversal**: Recursively execute `calculateSize()` on all child `FlexContainer`s first.
2. **Auto-Sizing Calculation** (when `autoSize == true`):
   * **Main Axis Dimension**:
     $$\text{mainSize} = \sum(\text{visibleChild.mainSize}) + \max(0, \text{visibleCount} - 1) \times \text{gap} + \text{mainAxisPadding}$$
   * **Cross Axis Dimension**:
     $$\text{crossSize} = \max(\text{visibleChild.crossSize}) + \text{crossAxisPadding}$$
3. **Explicit Sizing** (when `autoSize == false`):
   * Retain explicit width and height set via `.setSize(...)`.

---

### Phase 2: Layout Pass (`calculateLayout()` — Top-Down)
1. **Inner Available Space**:
   * Calculate content bounds: $\text{contentWidth} = \text{width} - (\text{padding.left} + \text{padding.right})$ and $\text{contentHeight} = \text{height} - (\text{padding.top} + \text{padding.bottom})$.
2. **`flowWeight` Distribution**:
   * Calculate available free space: $\text{freeSpace} = \max(0, \text{contentMainSize} - \text{fixedChildrenMainSize} - \text{totalGaps})$.
   * For children with `flowWeight > 0`:
     $$\text{childMainSize} = \text{round}\left(\frac{\text{child.flowWeight}}{\sum \text{flowWeight}} \times \text{freeSpace}\right)$$
3. **Cross-Axis Alignment (`AlignItems`)**:
   * `STRETCH`: Resize child cross-axis to match inner container cross dimension.
   * `CENTER`: Center child along cross axis: $\text{crossOffset} = \frac{\text{contentCrossSize} - \text{childCrossSize}}{2}$.
   * `START` / `END`: Align child to leading or trailing edge.
4. **Main-Axis Justification (`JustifyContent`)**:
   * Determine starting cursor offset and extra spacing for `START`, `CENTER`, `END`, `SPACE_BETWEEN`, `SPACE_AROUND`, and `SPACE_EVENLY`.
5. **Child Positioning**:
   * Set absolute `Position` on each child.
   * Recursively invoke `child.calculateLayout()`.

---

## 3. Required Components (`api.client.gui.elements`)

### A. Slot Synchronization & Containers
* **`SlotElement`**:
  * Wraps a Minecraft container `Slot`.
  * In `calculateLayout()`, updates the physical `Slot.x` and `Slot.y` coordinates in the menu using `SlotAccessor`.
  * Renders slot background (`GUIRenderHelper.drawOreUISlot` / `NineSliceRenderer.SLOT`).
  * Supports ghost item icons / outlines when the slot is empty.
* **`PlayerInventoryElement`**:
  * Composite element creating the standard 36-slot player inventory layout ($9 \times 3$ main inventory $+ 9$ hotbar slots) using `SlotElement` grids.

---

### B. Standard UI Widgets & Helpers
* **Layout Helpers**:
  * `Row`: Preset `FlexContainer` with `FlexDirection.ROW` and `AlignItems.CENTER`.
  * `Column`: Preset `FlexContainer` with `FlexDirection.COLUMN` and `AlignItems.CENTER`.
  * `Spacer`: Fixed-size or flexible (`flowWeight(1)`) empty spacing element.
* **Display Widgets**:
  * `LabelElement`: Text component rendering with font metrics, colors, and shadows.
  * `ProgressBarElement`: Progress bars and animated arrows/flames with dynamic progress suppliers ($0.0 \to 1.0$).
  * `ButtonElement`: Interactive clickable buttons with hover, active, and disabled states.

---

## 4. Step-by-Step Implementation Sequence

1. **Step 1 — Upgrade `UIElement`**:
   * Add `flowWeight()`, `autoSize`, `parent` tracking, and abstract `calculateSize()` / `calculateLayout()` methods.
2. **Step 2 — Implement Two-Phase Layout in `FlexContainer`**:
   * Implement measurement pass (content shrink-to-fit) and layout pass (`flowWeight`, alignment, justification).
3. **Step 3 — Update `ModularContainerScreen`**:
   * Wire root positioning, run layout passes in `init()`, and render `root` in `extractBackground()`.
4. **Step 4 — Implement `SlotElement` & `PlayerInventoryElement`**:
   * Enable slot coordinate synchronization with `SlotAccessor`.
5. **Step 5 — Implement Core Widgets**:
   * Add `LabelElement`, `ProgressBarElement`, `Row`, `Column`, and `Spacer`.
6. **Step 6 — Port `PartBuilderScreen`**:
   * Assemble `PartBuilderScreen` declaratively using the new API.
