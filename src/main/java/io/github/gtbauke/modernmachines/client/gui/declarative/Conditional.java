package io.github.gtbauke.modernmachines.client.gui.declarative;

import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

import io.github.gtbauke.modernmachines.client.gui.theme.GuiTheme;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Conditional extends UiWidget {
    private final BooleanSupplier condition;
    private final UiWidget trueBranch;
    private final UiWidget falseBranch;

    public Conditional(BooleanSupplier condition, UiWidget trueBranch, UiWidget falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;

        if (trueBranch != null) {
            trueBranch.setParent(this);
            this.flexNode.addChild(trueBranch.getFlexNode());
        }
        if (falseBranch != null) {
            falseBranch.setParent(this);
            this.flexNode.addChild(falseBranch.getFlexNode());
        }
    }

    public static Conditional of(BooleanSupplier condition, UiWidget trueBranch, UiWidget falseBranch) {
        return new Conditional(condition, trueBranch, falseBranch);
    }

    public static Conditional of(BooleanSupplier condition, UiWidget trueBranch) {
        return new Conditional(condition, trueBranch, null);
    }

    public static Conditional of(boolean condition, UiWidget trueBranch, UiWidget falseBranch) {
        return new Conditional(() -> condition, trueBranch, falseBranch);
    }

    public static Conditional of(boolean condition, UiWidget trueBranch) {
        return new Conditional(() -> condition, trueBranch, null);
    }

    private UiWidget getActiveBranch() {
        boolean match = condition != null && condition.getAsBoolean();
        return match ? trueBranch : falseBranch;
    }

    @Override
    public List<UiWidget> getChildren() {
        UiWidget active = getActiveBranch();
        return active != null ? List.of(active) : Collections.emptyList();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        UiWidget active = getActiveBranch();
        if (active != null && active.isVisible()) {
            active.extractBackground(graphics, font, theme, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor graphics, Font font, GuiTheme theme, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        UiWidget active = getActiveBranch();
        if (active != null && active.isVisible()) {
            active.extractForeground(graphics, font, theme, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void updateHoverState(double mouseX, double mouseY) {
        super.updateHoverState(mouseX, mouseY);
        UiWidget active = getActiveBranch();
        if (active != null) {
            active.updateHoverState(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isVisible()) return false;
        UiWidget active = getActiveBranch();
        if (active != null && active.isVisible() && active.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
