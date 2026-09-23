package net.cinderlabsmc.cinderlib.client.gui.theme;

import org.jspecify.annotations.NonNull;

public record Palette(int panel, int panelLight, int panelDark, int border, int slot, int slotLight, int slotDark,
    int highlight, int accent, int text, int buttonText, int amount) {

  public static final Palette VANILLA = new Palette(0xFFC6C6C6, 0xFFFFFFFF, 0xFF555555, 0xFF000000, 0xFF8B8B8B,
      0xFFFFFFFF, 0xFF373737, 0x80FFFFFF, 0xFFA0A0A0, 0xFF404040, 0xFFFFFFFF, 0xFFFFFFFF);

  public static final Palette DARK = new Palette(0xFF2B2D31, 0xFF3C3F45, 0xFF1E1F22, 0xFF000000, 0xFF1E1F22, 0xFF3C3F45,
      0xFF111214, 0x40FFFFFF, 0xFF4F8CFF, 0xFFE0E0E0, 0xFFFFFFFF, 0xFFFFFFFF);

  public @NonNull Palette withAccent(int accent) {
    return new Palette(panel, panelLight, panelDark, border, slot, slotLight, slotDark, highlight, accent, text,
        buttonText, amount);
  }

  public @NonNull Palette withText(int text) {
    return new Palette(panel, panelLight, panelDark, border, slot, slotLight, slotDark, highlight, accent, text,
        buttonText, amount);
  }

  public @NonNull Palette withPanel(int panel, int panelLight, int panelDark) {
    return new Palette(panel, panelLight, panelDark, border, slot, slotLight, slotDark, highlight, accent, text,
        buttonText, amount);
  }

  public @NonNull Palette withSlot(int slot, int slotLight, int slotDark) {
    return new Palette(panel, panelLight, panelDark, border, slot, slotLight, slotDark, highlight, accent, text,
        buttonText, amount);
  }
}
