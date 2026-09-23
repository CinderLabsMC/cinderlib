package net.cinderlabsmc.cinderlib.client.gui.widget;

import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.platform.InputConstants;

import net.cinderlabsmc.cinderlib.CinderLib;
import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.cinderlabsmc.cinderlib.client.gui.theme.ICinderTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;

public final class SearchField extends EditBox {

  private static final Component HINT = Component.translatable("gui." + CinderLib.MOD_ID + ".search");
  private static final String EMPTY = "";
  private static final int MAX_LENGTH = 256;
  private static final int PADDING_LEFT = 3;
  private static final int PADDING_TOP = 2;

  private final ICinderTheme theme;

  public SearchField(@NonNull Font font, @NonNull Bounds bounds, @NonNull ICinderTheme theme,
      @NonNull Consumer<String> onChange) {
    super(font, bounds.left() + PADDING_LEFT, bounds.top() + PADDING_TOP, bounds.width() - 2 * PADDING_LEFT,
        bounds.height() - PADDING_TOP, HINT);
    this.theme = theme;
    setBordered(false);
    setHint(HINT);
    setMaxLength(MAX_LENGTH);
    setResponder(onChange);
  }

  public void clear() {
    setValue(EMPTY);
  }

  @Override
  public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY,
      float partialTick) {
    theme.inset(graphics,
        new Bounds(getX() - PADDING_LEFT, getY() - PADDING_TOP, width + 2 * PADDING_LEFT, height + PADDING_TOP));
    super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
  }

  @Override
  public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
    if (event.button() != InputConstants.MOUSE_BUTTON_RIGHT) {
      super.onClick(event, doubleClick);
      return;
    }

    clear();
    setFocused(true);
  }

  @Override
  protected boolean isValidClickButton(@NonNull MouseButtonInfo button) {
    return button.button() == InputConstants.MOUSE_BUTTON_LEFT || button.button() == InputConstants.MOUSE_BUTTON_RIGHT;
  }
}
