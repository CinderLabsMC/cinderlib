package net.cinderlabsmc.cinderlib.client.gui.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

public final class GuiPainter {

    public static final int SLOT_SIZE = 18;
    public static final int ITEM_SIZE = 16;

    private static final int BORDER = 1;
    private static final int BEVEL = 2;

    private GuiPainter() {}

    public static void panel(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds) {
        int left = bounds.left();
        int top = bounds.top();
        int right = bounds.right();
        int bottom = bounds.bottom();

        graphics.fill(left, top, right, bottom, GuiTheme.PANEL_BORDER);
        graphics.fill(left + BORDER, top + BORDER, right - BORDER, bottom - BORDER, GuiTheme.PANEL_DARK);
        graphics.fill(left + BORDER, top + BORDER, right - BEVEL, bottom - BEVEL, GuiTheme.PANEL_LIGHT);
        graphics.fill(left + BEVEL, top + BEVEL, right - BEVEL, bottom - BEVEL, GuiTheme.PANEL);
    }

    public static void slot(@NonNull GuiGraphicsExtractor graphics, int itemLeft, int itemTop) {
        int left = itemLeft - BORDER;
        int top = itemTop - BORDER;
        int right = left + SLOT_SIZE;
        int bottom = top + SLOT_SIZE;

        graphics.fill(left, top, right, bottom, GuiTheme.SLOT_LIGHT);
        graphics.fill(left, top, right - BORDER, bottom - BORDER, GuiTheme.SLOT_DARK);
        graphics.fill(left + BORDER, top + BORDER, right - BORDER, bottom - BORDER, GuiTheme.SLOT);
    }

    public static void fill(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds, int color) {
        graphics.fill(bounds.left(), bounds.top(), bounds.right(), bounds.bottom(), color);
    }

    public static void highlight(@NonNull GuiGraphicsExtractor graphics, int itemLeft, int itemTop) {
        graphics.fill(itemLeft, itemTop, itemLeft + ITEM_SIZE, itemTop + ITEM_SIZE, GuiTheme.SLOT_HIGHLIGHT);
    }
}
