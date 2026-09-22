package net.cinderlabsmc.cinderlib.client.gui.theme;

import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.cinderlabsmc.cinderlib.client.gui.render.GuiSizes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

public record FlatTheme(Palette palette) implements ICinderTheme {

    public static final FlatTheme DARK = new FlatTheme(Palette.DARK);
    public static final FlatTheme LIGHT = new FlatTheme(Palette.VANILLA);

    private static final int LINE = 1;

    @Override
    public void panel(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds) {
        int left = bounds.left();
        int top = bounds.top();
        int right = bounds.right();
        int bottom = bounds.bottom();

        graphics.fill(left + LINE, top, right - LINE, bottom, palette.border());
        graphics.fill(left, top + LINE, right, bottom - LINE, palette.border());
        bevel(graphics, new Bounds(left + LINE, top + LINE, bounds.width() - 2 * LINE, bounds.height() - 2 * LINE), palette.panelLight(), palette.panelDark(), palette.panel());
    }

    @Override
    public void slot(@NonNull GuiGraphicsExtractor graphics, int itemLeft, int itemTop) {
        inset(graphics, new Bounds(itemLeft - GuiSizes.SLOT_BORDER, itemTop - GuiSizes.SLOT_BORDER, GuiSizes.SLOT_SIZE, GuiSizes.SLOT_SIZE));
    }

    @Override
    public void inset(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds) {
        bevel(graphics, bounds, palette.slotDark(), palette.slotLight(), palette.slot());
    }

    @Override
    public void button(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds, boolean hovered, boolean active) {
        graphics.fill(bounds.left(), bounds.top(), bounds.right(), bounds.bottom(), palette.border());

        var inner = new Bounds(bounds.left() + LINE, bounds.top() + LINE, bounds.width() - 2 * LINE, bounds.height() - 2 * LINE);

        bevel(graphics, inner, palette.panelLight(), palette.panelDark(), buttonFill(hovered, active));
    }

    @Override
    public void scrollHandle(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds, boolean active) {
        bevel(graphics, bounds, palette.panelLight(), palette.panelDark(), active ? palette.accent() : palette.panel());
    }

    @Override
    public void highlightBack(@NonNull GuiGraphicsExtractor graphics, int itemLeft, int itemTop) {
    }

    @Override
    public void highlightFront(@NonNull GuiGraphicsExtractor graphics, int itemLeft, int itemTop) {
        graphics.fill(itemLeft, itemTop, itemLeft + GuiSizes.ITEM_SIZE, itemTop + GuiSizes.ITEM_SIZE, palette.highlight());
    }

    @Override
    public int textColor() {
        return palette.text();
    }

    @Override
    public int buttonTextColor() {
        return palette.buttonText();
    }

    @Override
    public int amountColor() {
        return palette.amount();
    }

    @Override
    public int barBackground() {
        return palette.slotDark();
    }

    private int buttonFill(boolean hovered, boolean active) {
        if (!active) {
            return palette.slotDark();
        }

        return hovered ? palette.accent() : palette.panel();
    }

    private static void bevel(GuiGraphicsExtractor graphics, Bounds bounds, int topLeft, int bottomRight, int fill) {
        int left = bounds.left();
        int top = bounds.top();
        int right = bounds.right();
        int bottom = bounds.bottom();

        graphics.fill(left, top, right, bottom, bottomRight);
        graphics.fill(left, top, right - LINE, bottom - LINE, topLeft);
        graphics.fill(left + LINE, top + LINE, right - LINE, bottom - LINE, fill);
    }
}
