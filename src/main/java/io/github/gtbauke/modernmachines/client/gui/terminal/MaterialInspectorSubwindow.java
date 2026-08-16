package io.github.gtbauke.modernmachines.client.gui.terminal;

import java.util.List;

import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.client.gui.layout.AlignItems;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexDirection;
import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.layout.JustifyContent;
import io.github.gtbauke.modernmachines.client.gui.widget.ButtonWidget;
import io.github.gtbauke.modernmachines.client.gui.widget.FlexContainer;
import io.github.gtbauke.modernmachines.client.gui.widget.LabelWidget;
import io.github.gtbauke.modernmachines.client.gui.window.WindowWidget;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class MaterialInspectorSubwindow extends WindowWidget {
    private int selectedMaterialIndex = 0;
    private final List<Material> materials;

    private LabelWidget nameLabel;
    private LabelWidget typeLabel;
    private LabelWidget statsLabel;
    private LabelWidget formsLabel;
    private LabelWidget recipeLabel;

    public MaterialInspectorSubwindow() {
        super(Component.literal("📖 Material & Alloy Codex"), 230, 185);
        setClosable(true);
        setMinimizable(true);

        materials = ModMaterials.getAllMaterials().stream().toList();

        FlexContainer content = getContentContainer();
        content.getFlexNode().setGap(3);
        content.getFlexNode().setPadding(FlexInsets.all(4));
        content.getFlexNode().setAlignItems(AlignItems.CENTER);

        // 1. Material Carousel Switcher Row (< [Material Name] >)
        FlexContainer selectorRow = new FlexContainer(FlexDirection.ROW);
        selectorRow.getFlexNode().setSize(218, 20);
        selectorRow.getFlexNode().setAlignItems(AlignItems.CENTER);
        selectorRow.getFlexNode().setJustifyContent(JustifyContent.SPACE_BETWEEN);

        ButtonWidget prevBtn = new ButtonWidget(Component.literal("<"), b -> selectPrev());
        prevBtn.getFlexNode().setSize(22, 16);

        nameLabel = new LabelWidget(Component.literal("Material").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        nameLabel.getFlexNode().setSize(160, 16);
        nameLabel.setCentered(true);
        nameLabel.setShadow(false);

        ButtonWidget nextBtn = new ButtonWidget(Component.literal(">"), b -> selectNext());
        nextBtn.getFlexNode().setSize(22, 16);

        selectorRow.addChild(prevBtn);
        selectorRow.addChild(nameLabel);
        selectorRow.addChild(nextBtn);
        content.addChild(selectorRow);

        // 2. Material Info Details Card
        FlexContainer card = new FlexContainer(FlexDirection.COLUMN);
        card.getFlexNode().setSize(218, 108);
        card.getFlexNode().setGap(2);
        card.getFlexNode().setPadding(FlexInsets.all(4));

        typeLabel = new LabelWidget(Component.literal("Type: Base Metal"));
        typeLabel.setShadow(false);

        statsLabel = new LabelWidget(Component.literal("Hardness: 4.0 | Resistance: 6.0"));
        statsLabel.setShadow(false);

        formsLabel = new LabelWidget(Component.literal("Forms: Ingot, Plate, Rod, Wire, Gear"));
        formsLabel.setShadow(false);

        recipeLabel = new LabelWidget(Component.literal("Recipe: Alloy Smelter"));
        recipeLabel.setShadow(false);

        card.addChild(typeLabel);
        card.addChild(statsLabel);
        card.addChild(formsLabel);
        card.addChild(recipeLabel);
        content.addChild(card);

        updateMaterialDisplay();
    }

    private void selectPrev() {
        if (materials.isEmpty()) return;
        selectedMaterialIndex = (selectedMaterialIndex - 1 + materials.size()) % materials.size();
        updateMaterialDisplay();
    }

    private void selectNext() {
        if (materials.isEmpty()) return;
        selectedMaterialIndex = (selectedMaterialIndex + 1) % materials.size();
        updateMaterialDisplay();
    }

    private void updateMaterialDisplay() {
        if (materials.isEmpty()) return;
        Material mat = materials.get(selectedMaterialIndex);

        int color = 0xFF000000 | mat.colorHex();
        nameLabel.setLabel(Component.literal(mat.displayName()).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        nameLabel.setColor(color);

        typeLabel.setLabel(Component.literal("Category: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(mat.type().name()).withStyle(ChatFormatting.WHITE)));

        statsLabel.setLabel(Component.literal("Hardness: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.format("%.1f", mat.hardness())).withStyle(ChatFormatting.AQUA))
                .append(Component.literal("  |  XP: ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.format("%.1f", mat.smeltingXp())).withStyle(ChatFormatting.GREEN)));

        StringBuilder forms = new StringBuilder();
        int count = 0;
        for (ResourceForm form : mat.supportedForms()) {
            if (count > 0) forms.append(", ");
            forms.append(form.name().toLowerCase());
            count++;
            if (count >= 5) {
                forms.append("...");
                break;
            }
        }
        formsLabel.setLabel(Component.literal("Supported: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(forms.toString()).withStyle(ChatFormatting.YELLOW)));

        String formula = switch (mat.name()) {
            case "bronze" -> "Formula: 3 Copper + 1 Tin";
            case "invar" -> "Formula: 2 Iron + 1 Nickel";
            case "electrum" -> "Formula: 1 Gold + 1 Silver";
            case "brass" -> "Formula: 1 Copper + 1 Zinc";
            case "constantan" -> "Formula: 1 Copper + 1 Nickel";
            case "steel" -> "Formula: 1 Iron + 2 Coal (Alloy Smelter)";
            default -> "Origin: Geological Ore Deposit / Smelting";
        };
        recipeLabel.setLabel(Component.literal(formula).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
