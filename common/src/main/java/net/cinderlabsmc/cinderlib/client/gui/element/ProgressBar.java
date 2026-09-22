package net.cinderlabsmc.cinderlib.client.gui.element;

import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.cinderlabsmc.cinderlib.client.gui.theme.ICinderTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public record ProgressBar(ICinderTheme theme, Bounds bounds, LongSupplier value, LongSupplier max, int color, BarDirection direction, Supplier<List<Component>> tooltip) implements IGuiElement {

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        fill(graphics, bounds, theme.barBackground());
        fill(graphics, filled(), color);
    }

    @Override
    public @NonNull List<Component> tooltip(int mouseX, int mouseY) {
        if (!bounds.contains(mouseX, mouseY)) {
            return List.of();
        }

        return tooltip.get();
    }

    private Bounds filled() {
        return switch (direction) {
            case UP -> {
                int height = scale(bounds.height());
                yield new Bounds(bounds.left(), bounds.bottom() - height, bounds.width(), height);
            }
            case RIGHT -> bounds.withSize(scale(bounds.width()), bounds.height());
        };
    }

    private static void fill(GuiGraphicsExtractor graphics, Bounds area, int fillColor) {
        graphics.fill(area.left(), area.top(), area.right(), area.bottom(), fillColor);
    }

    private int scale(int size) {
        long total = Math.max(max.getAsLong(), 1);
        long current = Math.clamp(value.getAsLong(), 0, total);

        return (int) (size * current / total);
    }
}
