package io.github.gtbauke.modernmachines.modular.client;

import io.github.gtbauke.modernmachines.api.client.gui.core.Color;
import io.github.gtbauke.modernmachines.api.client.gui.core.Padding;
import io.github.gtbauke.modernmachines.api.client.gui.core.Size;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.AlignItems;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.FlexContainer;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.FlexDirection;
import io.github.gtbauke.modernmachines.api.client.gui.core.layout.JustifyContent;
import io.github.gtbauke.modernmachines.api.client.gui.elements.UIElement;
import io.github.gtbauke.modernmachines.api.client.gui.screen.ModularContainerScreen;
import io.github.gtbauke.modernmachines.modular.menu.PartBuilderMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PartBuilderScreen extends ModularContainerScreen<PartBuilderMenu> {
    public PartBuilderScreen(PartBuilderMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public UIElement buildContent() {
        var root = new FlexContainer()
                .setBackgroundColor(Color.BLUE)
                .setBorderColor(Color.GREEN)
                .setPadding(new Padding(4))
                .setGap(4)
                .setSize(new Size(this.imageWidth, this.imageHeight));

        root.addChild(
                new FlexContainer()
                        .setBackgroundColor(Color.RED)
                        .setBorderColor(Color.BLACK)
                        .setFlowWeight(1)
        ).addChild(
                new FlexContainer()
                        .setBackgroundColor(Color.GREEN)
                        .setBorderColor(Color.BLACK)
                        .setFlowWeight(2)
                        .setGap(2)
                        .setJustifyContent(JustifyContent.CENTER)
                        .setAlignItems(AlignItems.CENTER)
                        .setFlexDirection(FlexDirection.ROW)
                        .addChild(
                                new FlexContainer()
                                        .setBackgroundColor(Color.WHITE)
                                        .setBorderColor(Color.BLACK)
                                        .setFlowWeight(1)
                        ).addChild(
                                new FlexContainer()
                                        .setBackgroundColor(Color.WHITE)
                                        .setBorderColor(Color.BLACK)
                                        .setFlowWeight(1)
                        )
        );

        return root;
    }
}
