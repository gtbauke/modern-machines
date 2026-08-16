package io.github.gtbauke.modernmachines.client.gui.declarative;

public class GhostIcons {
    public record GhostIcon(int u, int v) {}

    // Atlas UV coordinates for ghost slot silhouettes
    public static final GhostIcon SOLID_FUEL = new GhostIcon(16, 96);
    public static final GhostIcon INGOT = new GhostIcon(0, 96);
    public static final GhostIcon UPGRADE = new GhostIcon(0, 96);
    public static final GhostIcon GEAR = new GhostIcon(32, 96);
    public static final GhostIcon INFO = new GhostIcon(64, 96);

    public static GhostIcon custom(int u, int v) {
        return new GhostIcon(u, v);
    }
}
