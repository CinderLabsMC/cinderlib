package net.cinderlabsmc.cinderlib.client.gui.element;

import java.util.List;

import org.jspecify.annotations.NonNull;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public interface IGuiElement {

  void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY);

  default @NonNull List<Component> tooltip(int mouseX, int mouseY) {
    return List.of();
  }
}
