package io.github.gtbauke.modernmachines.client.gui.declarative;

import io.github.gtbauke.modernmachines.client.gui.layout.FlexInsets;
import io.github.gtbauke.modernmachines.client.gui.render.NineSliceRenderer;
import io.github.gtbauke.modernmachines.client.gui.widget.UiWidget;

public class Card extends Container {
    public static final FlexInsets DEFAULT_CARD_PADDING = FlexInsets.all(4);

    public Card() {
        super();
        this.background(NineSliceRenderer.WINDOW_DARK);
        this.padding(DEFAULT_CARD_PADDING);
    }

    public Card(UiWidget child) {
        super(child);
        this.background(NineSliceRenderer.WINDOW_DARK);
        this.padding(DEFAULT_CARD_PADDING);
    }

    public static Card of(UiWidget child) {
        return new Card(child);
    }

    public static Card of(FlexInsets padding, UiWidget child) {
        Card card = new Card(child);
        card.padding(padding);
        return card;
    }

    public static Card of(int padding, UiWidget child) {
        return of(FlexInsets.all(padding), child);
    }

    public static Card empty() {
        return new Card();
    }
}
