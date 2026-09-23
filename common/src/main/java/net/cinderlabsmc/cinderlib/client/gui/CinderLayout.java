package net.cinderlabsmc.cinderlib.client.gui;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;

import net.cinderlabsmc.cinderlib.client.gui.element.BarDirection;
import net.cinderlabsmc.cinderlib.client.gui.element.IGuiElement;
import net.cinderlabsmc.cinderlib.client.gui.element.Label;
import net.cinderlabsmc.cinderlib.client.gui.element.ProgressBar;
import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.cinderlabsmc.cinderlib.client.gui.theme.ICinderTheme;
import net.cinderlabsmc.cinderlib.client.gui.widget.Scrollbar;
import net.cinderlabsmc.cinderlib.client.gui.widget.SearchField;
import net.cinderlabsmc.cinderlib.client.gui.widget.TextButton;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class CinderLayout {

  private final CinderScreen<?> screen;
  private final ICinderTheme theme;
  private final int originLeft;
  private final int originTop;

  CinderLayout(@NonNull CinderScreen<?> screen, @NonNull ICinderTheme theme, int originLeft, int originTop) {
    this.screen = screen;
    this.theme = theme;
    this.originLeft = originLeft;
    this.originTop = originTop;
  }

  public @NonNull Font font() {
    return screen.getFont();
  }

  public @NonNull ICinderTheme theme() {
    return theme;
  }

  public void label(int left, int top, @NonNull Component text) {
    label(left, top, () -> text);
  }

  public void label(int left, int top, @NonNull Supplier<Component> text) {
    element(new Label(font(), originLeft + left, originTop + top, text, theme.textColor()));
  }

  public void bar(@NonNull Bounds bounds, @NonNull LongSupplier value, @NonNull LongSupplier max, int color,
      @NonNull BarDirection direction) {
    bar(bounds, value, max, color, direction, List::of);
  }

  public void bar(@NonNull Bounds bounds, @NonNull LongSupplier value, @NonNull LongSupplier max, int color,
      @NonNull BarDirection direction, @NonNull Supplier<List<Component>> tooltip) {
    element(new ProgressBar(theme, absolute(bounds), value, max, color, direction, tooltip));
  }

  public @NonNull TextButton<Component> button(@NonNull Bounds bounds, @NonNull Component text,
      @NonNull Runnable action) {
    return cycle(bounds, List.of(text), text, Function.identity(), _ -> action.run());
  }

  public @NonNull TextButton<Component> button(@NonNull Bounds bounds, @NonNull Component text,
      @NonNull Component tooltip, @NonNull Runnable action) {
    var button = button(bounds, text, action);

    button.setTooltip(Tooltip.create(tooltip));

    return button;
  }

  public @NonNull TextButton<Boolean> toggle(@NonNull Bounds bounds, @NonNull Component text, boolean initial,
      @NonNull Consumer<Boolean> onChange) {
    return cycle(bounds, List.of(false, true), initial, enabled -> CommonComponents.optionStatus(text, enabled),
        onChange);
  }

  public <Value> @NonNull TextButton<Value> cycle(@NonNull Bounds bounds, @NonNull List<Value> values,
      @NonNull Value initial, @NonNull Function<Value, Component> names, @NonNull Consumer<Value> onChange) {
    return screen.addCinderWidget(new TextButton<>(absolute(bounds), theme, values, initial, names, onChange));
  }

  public @NonNull SearchField search(@NonNull Bounds bounds, @NonNull Consumer<String> onChange) {
    return screen.addCinderWidget(new SearchField(font(), absolute(bounds), theme, onChange));
  }

  public @NonNull Toolbar toolbar(int left, int top) {
    return new Toolbar(this, left, top);
  }

  public @NonNull Scrollbar scrollbar(int left, int top, int height) {
    return screen.addCinderWidget(new Scrollbar(originLeft + left, originTop + top, height, theme));
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
