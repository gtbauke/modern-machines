package io.github.gtbauke.modernmachines.client.gui.editor.codegen;

import io.github.gtbauke.modernmachines.client.gui.editor.model.ElementDefinition;
import io.github.gtbauke.modernmachines.client.gui.editor.model.ScreenLayoutDefinition;
import io.github.gtbauke.modernmachines.client.gui.editor.model.TabDefinition;

import java.util.ArrayList;
import java.util.Comparator;

public class JavaCodeGenerator {

    public static String generateBuildContentMethod(ScreenLayoutDefinition layout) {
        var sb = new StringBuilder();
        sb.append("    @Override\n");
        sb.append("    protected UIElement buildContent() {\n");

        if (layout.getElements().isEmpty()) {
            sb.append("        return null;\n");
            sb.append("    }\n\n");
        } else {
            var sortedElements = new ArrayList<>(layout.getElements());
            sortedElements.sort(Comparator.comparingInt(ElementDefinition::getY));

            if (sortedElements.size() == 1 && sortedElements.get(0).getY() == 0
                    && (sortedElements.get(0).getType() == ElementDefinition.ElementType.COLUMN || sortedElements.get(0).getType() == ElementDefinition.ElementType.ROW)) {
                var rootEl = sortedElements.get(0);
                sb.append("        var root = ").append(generateElementSnippet(rootEl, "        ")).append(";\n\n");
            } else {
                sb.append("        var root = Column.of(0, AlignItems.CENTER");
                int currentY = 0;
                for (var el : sortedElements) {
                    int gap = el.getY() - currentY;
                    if (gap > 0) {
                        sb.append(",\n            Spacer.vertical(").append(gap).append(")");
                    }

                    sb.append(",\n            ").append(generateElementSnippet(el, "            "));
                    int elHeight = el.getType() == ElementDefinition.ElementType.PLAYER_INVENTORY ? 86 : el.getHeight();
                    currentY = el.getY() + elHeight;
                }

                sb.append("\n        );\n\n");
            }

            sb.append("        root.setSize(new Size(this.imageWidth, this.imageHeight));\n\n");
            sb.append("        return root;\n");
            sb.append("    }\n\n");
        }

        // Generate initWindows() if tabs exist
        if (!layout.getTabs().isEmpty()) {
            sb.append("    @Override\n");
            sb.append("    protected void initWindows() {\n");
            int tabIndex = 0;
            for (var tab : layout.getTabs()) {
                var varName = sanitizeVarName(tab.getTitle()) + "Window";
                var tabVarName = sanitizeVarName(tab.getTitle()) + "Tab";
                boolean isLeft = "LEFT".equalsIgnoreCase(tab.getSide());
                int winWidth = tab.getWindowWidth();
                int winHeight = tab.getWindowHeight();

                if (tab.getTitle().toLowerCase().contains("side config")) {
                    sb.append("        // Side Configuration Window & Tab\n");
                    sb.append("        if (this.menu.getSideConfigurable() != null) {\n");
                    sb.append("            var sideConfigPos = new Position(\n");
                    sb.append("                this.mainWindow.getPosition().x() - SideConfigWindow.WINDOW_WIDTH - 4,\n");
                    sb.append("                this.mainWindow.getPosition().y()\n");
                    sb.append("            );\n");
                    sb.append("            var sideConfigWindow = new SideConfigWindow(this.menu.getSideConfigurable(), sideConfigPos);\n");
                    sb.append("            this.windowManager.addWindow(sideConfigWindow);\n\n");
                    sb.append("            var sideConfigTab = new SideTabElement(\n");
                    sb.append("                this.mainWindow,\n");
                    sb.append("                sideConfigWindow,\n");
                    sb.append("                new ItemStack(ModItems.ENGINEERS_TABLET.get()),\n");
                    sb.append("                Component.literal(\"Side Configuration\"),\n");
                    sb.append("                true\n");
                    sb.append("            );\n");
                    sb.append("            sideConfigTab.updateDockedPosition(").append(tabIndex * 28).append(");\n");
                    sb.append("            this.mainWindow.addChild(sideConfigTab);\n");
                    sb.append("        }\n\n");
                    tabIndex++;
                    continue;
                }

                if (tab.getTitle().equalsIgnoreCase("Upgrades")) {
                    sb.append("        // Upgrade Window & Tab\n");
                    sb.append("        if (this.menu.slots.size() >= 8) {\n");
                    sb.append("            int winWidth = 80;\n");
                    sb.append("            int winHeight = 72;\n");
                    sb.append("            var upgradeWindow = new Window(\n");
                    sb.append("                Component.literal(\"Upgrades\"),\n");
                    sb.append("                new Bounds(\n");
                    sb.append("                    new Position(this.mainWindow.getPosition().x() - winWidth - 4, this.mainWindow.getPosition().y()),\n");
                    sb.append("                    new Size(winWidth, winHeight)\n");
                    sb.append("                ),\n");
                    sb.append("                new Padding(0)\n");
                    sb.append("            );\n");
                    sb.append("            upgradeWindow.setHasHeader(true);\n");
                    sb.append("            upgradeWindow.setHeaderHeight(18);\n");
                    sb.append("            upgradeWindow.setDraggable(true);\n");
                    sb.append("            upgradeWindow.setHasCloseButton(true);\n");
                    sb.append("            upgradeWindow.setVisible(false);\n\n");
                    sb.append("            var upgradeContent = Column.of(0, AlignItems.CENTER,\n");
                    sb.append("                Spacer.vertical(4),\n");
                    sb.append("                Column.of(4, AlignItems.CENTER,\n");
                    sb.append("                    Row.of(4,\n");
                    sb.append("                        new SlotElement(this.menu.slots.get(4)),\n");
                    sb.append("                        new SlotElement(this.menu.slots.get(5))\n");
                    sb.append("                    ),\n");
                    sb.append("                    Row.of(4,\n");
                    sb.append("                        new SlotElement(this.menu.slots.get(6)),\n");
                    sb.append("                        new SlotElement(this.menu.slots.get(7))\n");
                    sb.append("                    )\n");
                    sb.append("                )\n");
                    sb.append("            );\n");
                    sb.append("            upgradeContent.setSize(new Size(winWidth, winHeight - 18));\n");
                    sb.append("            upgradeWindow.setContent(upgradeContent);\n\n");
                    sb.append("            this.windowManager.addWindow(upgradeWindow);\n\n");
                    sb.append("            var tab = new SideTabElement(\n");
                    sb.append("                this.mainWindow,\n");
                    sb.append("                upgradeWindow,\n");
                    sb.append("                new ItemStack(ModItems.SPEED_UPGRADE.get()),\n");
                    sb.append("                Component.literal(\"Upgrades\"),\n");
                    sb.append("                true\n");
                    sb.append("            );\n");
                    sb.append("            tab.updateDockedPosition(").append(tabIndex * 28).append(");\n");
                    sb.append("            this.mainWindow.addChild(tab);\n");
                    sb.append("        }\n\n");
                    tabIndex++;
                    continue;
                }

                sb.append("        // Tab Window: ").append(tab.getTitle()).append("\n");
                sb.append("        int ").append(varName).append("Width = ").append(winWidth).append(";\n");
                sb.append("        int ").append(varName).append("Height = ").append(winHeight).append(";\n");
                sb.append("        var ").append(varName).append("Pos = new Position(\n");
                if (isLeft) {
                    sb.append("            this.mainWindow.getPosition().x() - ").append(varName).append("Width - 4,\n");
                } else {
                    sb.append("            this.mainWindow.getPosition().x() + this.mainWindow.getSize().width() + 4,\n");
                }
                sb.append("            this.mainWindow.getPosition().y()\n");
                sb.append("        );\n");
                sb.append("        var ").append(varName).append(" = new Window(\n");
                sb.append("            Component.literal(\"").append(escapeString(tab.getTitle())).append("\"),\n");
                sb.append("            new Bounds(").append(varName).append("Pos, new Size(").append(varName).append("Width, ").append(varName).append("Height)),\n");
                sb.append("            new Padding(0)\n");
                sb.append("        );\n");
                sb.append("        ").append(varName).append(".setHasHeader(true);\n");
                sb.append("        ").append(varName).append(".setHeaderHeight(18);\n");
                sb.append("        ").append(varName).append(".setDraggable(true);\n");
                sb.append("        ").append(varName).append(".setHasCloseButton(true);\n");
                sb.append("        ").append(varName).append(".setVisible(false);\n\n");

                if (!tab.getElements().isEmpty()) {
                    var sortedTabElements = new ArrayList<>(tab.getElements());
                    sortedTabElements.sort(Comparator.comparingInt(ElementDefinition::getY));

                    sb.append("        var ").append(varName).append("Content = Column.of(0, AlignItems.CENTER");
                    int currentY = 0;
                    for (var child : sortedTabElements) {
                        int gap = child.getY() - currentY;
                        if (gap > 0) {
                            sb.append(",\n            Spacer.vertical(").append(gap).append(")");
                        }
                        sb.append(",\n            ").append(generateElementSnippet(child, "            "));
                        currentY = child.getY() + child.getHeight();
                    }
                    sb.append("\n        );\n");
                    sb.append("        ").append(varName).append("Content.setSize(new Size(").append(varName).append("Width, ").append(varName).append("Height - 18));\n");
                    sb.append("        ").append(varName).append(".setContent(").append(varName).append("Content);\n\n");
                }

                sb.append("        this.windowManager.addWindow(").append(varName).append(");\n\n");

                sb.append("        var ").append(tabVarName).append(" = new SideTabElement(\n");
                sb.append("            this.mainWindow,\n");
                sb.append("            ").append(varName).append(",\n");
                sb.append("            new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(\"").append(tab.getIconItem()).append("\"))),\n");
                sb.append("            Component.literal(\"").append(escapeString(tab.getTooltip())).append("\"),\n");
                sb.append("            ").append(isLeft).append("\n");
                sb.append("        );\n");
                sb.append("        ").append(tabVarName).append(".updateDockedPosition(").append(tabIndex * 28).append(");\n");
                sb.append("        this.mainWindow.addChild(").append(tabVarName).append(");\n\n");

                tabIndex++;
            }

            sb.append("    }\n");
        }

        return sb.toString();
    }

    public static String generateElementSnippet(ElementDefinition el, String indent) {
        var base = generateBaseElementSnippet(el, indent);
        if (el.getFlowAmount() > 0) {
            base += ".setFlowWeight(" + el.getFlowAmount() + ")";
        }

        if (el.isFitParentWidth()) {
            base += ".setFillParentWidth(true)";
        }

        if (el.isFitParentHeight()) {
            base += ".setFillParentHeight(true)";
        }

        return base;
    }

    private static String generateBaseElementSnippet(ElementDefinition el, String indent) {
        switch (el.getType()) {
            case SLOT -> {
                return "new SlotElement(this.menu.slots.get(" + el.getSlotIndex() + "))";
            }
            case SLOT_GRID -> {
                var sb = new StringBuilder();
                sb.append("Column.of(").append(el.getGap()).append(", AlignItems.CENTER");
                for (int r = 0; r < el.getGridRows(); r++) {
                    sb.append(",\n").append(indent).append("    Row.of(").append(el.getGap());
                    for (int c = 0; c < el.getGridCols(); c++) {
                        int idx = el.getSlotIndex() + r * el.getGridCols() + c;
                        sb.append(", new SlotElement(this.menu.slots.get(").append(idx).append("))");
                    }

                    sb.append(")");
                }

                sb.append("\n").append(indent).append(")");
                return sb.toString();
            }
            case PLAYER_INVENTORY -> {
                return "new PlayerInventoryElement(this.menu, this.menu.getPlayerInventoryStart())";
            }
            case PROGRESS_ARROW -> {
                return "ProgressBarElement.arrow(() -> (double) this.menu.getProgressScaled(100) / 100.0)";
            }
            case PROGRESS_LINEAR -> {
                return "ProgressBarElement.linear(() -> (double) this.menu.getProgressScaled(100) / 100.0)";
            }
            case FLAME -> {
                return "BurningElement.flame(() -> (double) this.menu.getBurnProgressScaled(100) / 100.0)";
            }
            case SPACER -> {
                if (el.getWidth() == el.getHeight()) {
                    return "Spacer.square(" + el.getWidth() + ")";
                } else if (el.getWidth() > el.getHeight()) {
                    return "Spacer.horizontal(" + el.getWidth() + ")";
                } else {
                    return "Spacer.vertical(" + el.getHeight() + ")";
                }
            }
            case BUTTON -> {
                return "new ButtonElement(" + el.getWidth() + ", " + el.getHeight() + ", Component.literal(\"" + escapeString(el.getText()) + "\"), () -> {})";
            }
            case TEXT -> {
                var sb = new StringBuilder();
                if (el.getLabelSource() == ElementDefinition.LabelSourceType.TRANSLATABLE) {
                    sb.append("new LabelElement(Component.translatable(\"").append(escapeString(el.getText())).append("\"))");
                } else if (el.getLabelSource() == ElementDefinition.LabelSourceType.MENU_DATA) {
                    var fmt = el.getLabelFormat() != null ? el.getLabelFormat() : "%d";
                    sb.append("new LabelElement(() -> Component.literal(String.format(\"").append(escapeString(fmt)).append("\", this.menu.getData(").append(el.getDataIndex()).append("))))");
                } else {
                    sb.append("new LabelElement(Component.literal(\"").append(escapeString(el.getText())).append("\"))");
                }

                if (!"LEFT".equalsIgnoreCase(el.getLabelAlign())) {
                    sb.append(".setAlignment(LabelElement.TextAlignment.").append(el.getLabelAlign()).append(")");
                }

                if (!el.isShadow()) {
                    sb.append(".setShadow(false)");
                }

                return sb.toString();
            }
            case ICON -> {
                var sb = new StringBuilder();
                sb.append("new IconElement(new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(\"").append(el.getIconItem()).append("\"))))");
                if (el.getOpacity() < 0.99f) {
                    sb.append(".setOpacity(").append(el.getOpacity()).append("f)");
                }

                if ("GRAYSCALE".equalsIgnoreCase(el.getColorMode())) {
                    sb.append(".setColorMode(IconElement.ColorMode.GRAYSCALE)");
                }

                return sb.toString();
            }
            case BLOCK_FACE -> {
                String side = el.getRelativeSide() != null ? el.getRelativeSide().toUpperCase() : "FRONT";
                return "new BlockFaceElement(RelativeSide." + side + ", () -> getMode(RelativeSide." + side + "), this::setMode)";
            }
            case SIDE_CONFIG_GRID -> {
                return "// Configured via initWindows() Side Configuration tab";
            }
            case COLUMN -> {
                var sb = new StringBuilder();
                sb.append("Column.of(").append(el.getGap()).append(", AlignItems.").append(el.getAlign());
                if (!"START".equalsIgnoreCase(el.getJustifyContent())) {
                    sb.append(", JustifyContent.").append(el.getJustifyContent());
                }

                for (var child : el.getChildren()) {
                    sb.append(",\n").append(indent).append("    ").append(generateElementSnippet(child, indent + "    "));
                }

                sb.append("\n").append(indent).append(")");
                if (!el.isFitParentWidth() && !el.isFitParentHeight() && el.getFlowAmount() == 0 && el.getWidth() > 0 && el.getHeight() > 0) {
                    sb.append(".setSize(new Size(").append(el.getWidth()).append(", ").append(el.getHeight()).append("))");
                }

                return sb.toString();
            }
            case ROW -> {
                var sb = new StringBuilder();
                sb.append("Row.of(").append(el.getGap()).append(", AlignItems.").append(el.getAlign());
                if (!"START".equalsIgnoreCase(el.getJustifyContent())) {
                    sb.append(", JustifyContent.").append(el.getJustifyContent());
                }

                for (var child : el.getChildren()) {
                    sb.append(",\n").append(indent).append("    ").append(generateElementSnippet(child, indent + "    "));
                }

                sb.append("\n").append(indent).append(")");
                if (!el.isFitParentWidth() && !el.isFitParentHeight() && el.getFlowAmount() == 0 && el.getWidth() > 0 && el.getHeight() > 0) {
                    sb.append(".setSize(new Size(").append(el.getWidth()).append(", ").append(el.getHeight()).append("))");
                }

                return sb.toString();
            }
            case SIDE_TAB -> {
                return "// Configured via initWindows() side tabs";
            }
            default -> {
                return "Spacer.square(10)";
            }
        }
    }

    private static String sanitizeVarName(String str) {
        if (str == null || str.isEmpty()) {
            return "tab";
        }

        var cleaned = str.replaceAll("[^a-zA-Z0-9]", "");
        if (cleaned.isEmpty()) {
            return "tab";
        }

        return Character.toLowerCase(cleaned.charAt(0)) + (cleaned.length() > 1 ? cleaned.substring(1) : "");
    }

    private static String escapeString(String str) {
        if (str == null) {
            return "";
        }

        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
