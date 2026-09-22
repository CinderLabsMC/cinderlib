package net.cinderlabsmc.cinderlib.client.gui;

import net.cinderlabsmc.cinderlib.client.gui.element.BarDirection;
import net.cinderlabsmc.cinderlib.client.gui.element.IGuiElement;
import net.cinderlabsmc.cinderlib.client.gui.element.Label;
import net.cinderlabsmc.cinderlib.client.gui.element.ProgressBar;
import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.cinderlabsmc.cinderlib.client.gui.render.GuiTheme;
import net.cinderlabsmc.cinderlib.client.gui.widget.Scrollbar;
import net.cinderlabsmc.cinderlib.client.gui.widget.SearchField;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public final class CinderLayout {

    private final CinderScreen<?> screen;
    private final int originLeft;
    private final int originTop;

    CinderLayout(@NonNull CinderScreen<?> screen, int originLeft, int originTop) {
        this.screen = screen;
        this.originLeft = originLeft;
        this.originTop = originTop;
    }

    public @NonNull Font font() {
        return screen.getFont();
    }

    public void label(int left, int top, @NonNull Component text) {
        label(left, top, () -> text);
    }

    public void label(int left, int top, @NonNull Supplier<Component> text) {
        element(new Label(font(), originLeft + left, originTop + top, text, GuiTheme.TEXT));
    }

    public void bar(@NonNull Bounds bounds, @NonNull LongSupplier value, @NonNull LongSupplier max, int color, @NonNull BarDirection direction) {
        bar(bounds, value, max, color, direction, List::of);
    }

    public void bar(@NonNull Bounds bounds, @NonNull LongSupplier value, @NonNull LongSupplier max, int color, @NonNull BarDirection direction, @NonNull Supplier<List<Component>> tooltip) {
        element(new ProgressBar(absolute(bounds), value, max, color, direction, tooltip));
    }

    public @NonNull Button button(@NonNull Bounds bounds, @NonNull Component text, @NonNull Runnable action) {
        var target = absolute(bounds);

        return screen.addCinderWidget(Button.builder(text, _ -> action.run()).bounds(target.left(), target.top(), target.width(), target.height()).build());
    }

    public @NonNull Button button(@NonNull Bounds bounds, @NonNull Component text, @NonNull Component tooltip, @NonNull Runnable action) {
        var button = button(bounds, text, action);

        button.setTooltip(Tooltip.create(tooltip));

        return button;
    }

    public @NonNull CycleButton<Boolean> toggle(@NonNull Bounds bounds, @NonNull Component text, boolean initial, @NonNull Consumer<Boolean> onChange) {
        var target = absolute(bounds);
        var toggle = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF, initial).create(target.left(), target.top(), target.width(), target.height(), text, (_, value) -> onChange.accept(value));

        return screen.addCinderWidget(toggle);
    }

    public <Value> @NonNull CycleButton<Value> cycle(@NonNull Bounds bounds, @NonNull List<Value> values, @NonNull Value initial, @NonNull Function<Value, Component> names, @NonNull Consumer<Value> onChange) {
        var target = absolute(bounds);
        var cycle = CycleButton.builder(names, initial).withValues(values).displayOnlyValue().create(target.left(), target.top(), target.width(), target.height(), Component.empty(), (_, value) -> onChange.accept(value));

        return screen.addCinderWidget(cycle);
    }

    public @NonNull SearchField search(@NonNull Bounds bounds, @NonNull Consumer<String> onChange) {
        var target = absolute(bounds);

        return screen.addCinderWidget(new SearchField(font(), target.left(), target.top(), target.width(), target.height(), onChange));
    }

    public @NonNull Scrollbar scrollbar(int left, int top, int height) {
        return screen.addCinderWidget(new Scrollbar(originLeft + left, originTop + top, height));
    }

    public <Widget extends AbstractWidget> @NonNull Widget widget(int left, int top, @NonNull Widget widget) {
        widget.setPosition(originLeft + left, originTop + top);

        return screen.addCinderWidget(widget);
    }

    public void element(@NonNull IGuiElement element) {
        screen.addElement(element);
    }

    public @NonNull Bounds absolute(@NonNull Bounds bounds) {
        return bounds.offset(originLeft, originTop);
    }
}
