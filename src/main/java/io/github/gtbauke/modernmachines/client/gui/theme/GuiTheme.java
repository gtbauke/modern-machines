package io.github.gtbauke.modernmachines.client.gui.theme;

public record GuiTheme(
        String name,
        int windowBgColor,
        int windowBorderColor,
        int headerBgColor,
        int headerTextColor,
        int panelBgColor,
        int panelBorderColor,
        int slotBgColor,
        int slotBorderColor,
        int accentColor,
        int accentHoverColor,
        int dropShadowColor
) {
    public static final GuiTheme INDUSTRIAL_DARK = new GuiTheme(
            "industrial_dark",
            0xFF232529, // windowBgColor
            0xFF383C45, // windowBorderColor
            0xFF1B1C1E, // headerBgColor
            0xFFE0E0E0, // headerTextColor
            0xFF2B2D33, // panelBgColor
            0xFF424752, // panelBorderColor
            0xFF18191B, // slotBgColor
            0xFF383C45, // slotBorderColor
            0xFF4AE252, // accentColor (Energy / Tech green)
            0xFF68F070, // accentHoverColor
            0x66000000  // dropShadowColor
    );

    public static final GuiTheme STEEL_TECH = new GuiTheme(
            "steel_tech",
            0xFF32363F,
            0xFF4E5563,
            0xFF25282F,
            0xFFFFFFFF,
            0xFF3A3F4A,
            0xFF5B6373,
            0xFF1E2026,
            0xFF4E5563,
            0xFF38BDF8, // Cyan accent
            0xFF60A5FA,
            0x77000000
    );

    public static final GuiTheme BRONZE_RETRO = new GuiTheme(
            "bronze_retro",
            0xFF2D231E,
            0xFF523F34,
            0xFF1F1714,
            0xFFF5D6BA,
            0xFF3D302A,
            0xFF695143,
            0xFF181210,
            0xFF523F34,
            0xFFE58F37, // Amber / Bronze accent
            0xFFFBBF24,
            0x66000000
    );
}
