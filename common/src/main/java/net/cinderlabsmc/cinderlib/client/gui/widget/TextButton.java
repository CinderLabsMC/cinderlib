package net.cinderlabsmc.cinderlib.client.gui.widget;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.cinderlabsmc.cinderlib.client.gui.theme.ICinderTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class TextButton<Value> extends ThemedButton<Value> {

  private static final int HALF = 2;

  public TextButton(@NonNull Bounds bounds, @NonNull ICinderTheme theme, @NonNull List<Value> values,
      @NonNull Value initial, @NonNull Function<Value, Component> names, @NonNull Consumer<Value> onChange) {
    super(bounds, theme, values, initial, names, onChange);
  }

  @Override
  protected void extractLabel(@NonNull GuiGraphicsExtractor graphics) {
    var font = Minecraft.getInstance().font;
    int centerLeft = getX() + width / HALF;
    int centerTop = getY() + (height - font.lineHeight) / HALF + 1;

    graphics.centeredText(font, getMessage(), centerLeft, centerTop, theme.buttonTextColor());
  }
}
