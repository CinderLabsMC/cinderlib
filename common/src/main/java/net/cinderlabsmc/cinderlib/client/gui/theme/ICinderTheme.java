package net.cinderlabsmc.cinderlib.client.gui.theme;

import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

public interface ICinderTheme {

    void panel(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds);

    void slot(@NonNull GuiGraphicsExtractor graphics, int itemLeft, int itemTop);

    void inset(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds);

    void button(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds, boolean hovered, boolean active);

    void scrollHandle(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds, boolean active);

    void highlightBack(@NonNull GuiGraphicsExtractor graphics, int itemLeft, int itemTop);

    void highlightFront(@NonNull GuiGraphicsExtractor graphics, int itemLeft, int itemTop);

    int textColor();

    int buttonTextColor();

    int amountColor();

    int barBackground();
}
