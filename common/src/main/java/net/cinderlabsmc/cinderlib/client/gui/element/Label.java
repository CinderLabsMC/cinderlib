package net.cinderlabsmc.cinderlib.client.gui.element;

import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public record Label(Font font, int left, int top, Supplier<Component> text, int color) implements IGuiElement {

  @Override
  public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    graphics.text(font, text.get(), left, top, color, false);
  }
}
