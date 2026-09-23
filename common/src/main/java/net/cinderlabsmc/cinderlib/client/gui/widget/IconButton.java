package net.cinderlabsmc.cinderlib.client.gui.widget;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.cinderlabsmc.cinderlib.client.gui.theme.ICinderTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class IconButton<Value> extends ThemedButton<Value> {

  public static final int SIZE = 18;

  private static final int ICON_OFFSET = 1;

  private final Function<Value, ItemStack> icons;

  public IconButton(int left, int top, @NonNull ICinderTheme theme, @NonNull List<Value> values, @NonNull Value initial,
      @NonNull Function<Value, ItemStack> icons, @NonNull Function<Value, Component> names,
      @NonNull Consumer<Value> onChange) {
    super(new Bounds(left, top, SIZE, SIZE), theme, values, initial, names, onChange);
    this.icons = icons;
  }

  @Override
  protected void extractLabel(@NonNull GuiGraphicsExtractor graphics) {
    graphics.item(icons.apply(value()), getX() + ICON_OFFSET, getY() + ICON_OFFSET);
  }

  @Override
  protected void updateMessage() {
    super.updateMessage();
    setTooltip(Tooltip.create(getMessage()));
  }
}
