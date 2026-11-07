package com.shatteredseals.client;

/**
 * Pixel-safe vanilla HUD geometry (GUI-scaled pixels, not raw monitor pixels).
 *
 * Minecraft 1.21.1's default survival hotbar spans 182 GUI pixels around the
 * viewport midpoint. The class/resource line sits directly above the
 * vanilla hearts and hunger, and no ability panel intersects the hotbar.
 */
public final class CombatHudLayout {
    private static final int HOTBAR_HALF_WIDTH = 91;
    private static final int GAP_FROM_HOTBAR = 5;
    private static final int EDGE_PADDING = 4;
    private static final int MAX_PANEL_WIDTH = 54;
    public static final int PANEL_HEIGHT = 22;
    private static final int PANEL_VERTICAL_GAP = 3;

    public static Positions measure(int viewportWidth, int viewportHeight) {
        int center = viewportWidth / 2;
        int hotbarLeft = center - HOTBAR_HALF_WIDTH;
        int hotbarRight = center + HOTBAR_HALF_WIDTH;
        int leftSpace = hotbarLeft - GAP_FROM_HOTBAR - EDGE_PADDING;
        int rightSpace = viewportWidth - hotbarRight - GAP_FROM_HOTBAR - EDGE_PADDING;
        int panelWidth = Math.max(0, Math.min(MAX_PANEL_WIDTH, Math.min(leftSpace, rightSpace)));
        int leftX = hotbarLeft - GAP_FROM_HOTBAR - panelWidth;
        int rightX = hotbarRight + GAP_FROM_HOTBAR;

        int firstY = viewportHeight - 50;
        int secondY = firstY + PANEL_HEIGHT + PANEL_VERTICAL_GAP;
        return new Positions(hotbarLeft, hotbarRight, leftX, rightX,
            panelWidth, firstY, secondY, viewportHeight - 66, viewportHeight - 52);
    }

    public record Positions(int hotbarLeft, int hotbarRight,
                            int leftX, int rightX, int panelWidth,
                            int firstY, int secondY, int titleY, int barY) {
        public int abilityX(int slot) { return slot < 2 ? leftX : rightX; }
        public int abilityY(int slot) { return slot % 2 == 0 ? firstY : secondY; }
    }

    private CombatHudLayout() {}
}
