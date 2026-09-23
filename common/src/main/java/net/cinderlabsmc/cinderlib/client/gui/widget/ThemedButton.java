package net.cinderlabsmc.cinderlib.client.gui.widget;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.platform.InputConstants;

import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.cinderlabsmc.cinderlib.client.gui.theme.ICinderTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;

public abstract class ThemedButton<Value> extends AbstractButton {

  private static final int FORWARD = 1;
  private static final int BACKWARD = -1;

  protected final ICinderTheme theme;

  private final List<Value> values;
  private final Function<Value, Component> names;
  private final Consumer<Value> onChange;

  private int index;

  protected ThemedButton(@NonNull Bounds bounds, @NonNull ICinderTheme theme, @NonNull List<Value> values,
      @NonNull Value initial, @NonNull Function<Value, Component> names, @NonNull Consumer<Value> onChange) {
    super(bounds.left(), bounds.top(), bounds.width(), bounds.height(), names.apply(initial));
    this.theme = theme;
    this.values = List.copyOf(values);
    this.names = names;
    this.onChange = onChange;
    this.index = Math.max(this.values.indexOf(initial), 0);

    updateMessage();
  }

  public @NonNull Value value() {
    return values.get(index);
  }

  @Override
  public void onPress(@NonNull InputWithModifiers input) {
    cycle(FORWARD);
  }

  @Override
  public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
    cycle(event.button() == InputConstants.MOUSE_BUTTON_RIGHT ? BACKWARD : FORWARD);
  }

  @Override
  protected boolean isValidClickButton(@NonNull MouseButtonInfo button) {
    return button.button() == InputConstants.MOUSE_BUTTON_LEFT || button.button() == InputConstants.MOUSE_BUTTON_RIGHT;
  }

  @Override
  protected final void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY,
      float partialTick) {
    theme.button(graphics, new Bounds(getX(), getY(), width, height), isHoveredOrFocused(), active);
    extractLabel(graphics);
  }

  @Override
  protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
    defaultButtonNarrationText(output);
  }

  protected abstract void extractLabel(@NonNull GuiGraphicsExtractor graphics);

  protected void updateMessage() {
    setMessage(names.apply(value()));
  }

  private void cycle(int step) {
    index = Math.floorMod(index + step, values.size());
    updateMessage();
    onChange.accept(value());
  }
}
