package net.cinderlabsmc.cinderlib.client.gui.format;

import java.text.NumberFormat;
import java.util.Locale;

import org.jspecify.annotations.NonNull;

public final class AmountFormat {

  private static final String[] SUFFIXES = { "K", "M", "G", "T", "P", "E" };
  private static final long STEP = 1000;
  private static final int DECIMAL_LIMIT = 10;
  private static final double DECIMAL_FACTOR = 10;

  private AmountFormat() {
  }

  public static @NonNull String compact(long amount) {
    if (amount < STEP) {
      return Long.toString(amount);
    }

    double value = amount;
    int suffix = -1;

    while (value >= STEP && suffix < SUFFIXES.length - 1) {
      value /= STEP;
      suffix++;
    }

    if (value >= DECIMAL_LIMIT) {
      return (long) value + SUFFIXES[suffix];
    }

    return Math.floor(value * DECIMAL_FACTOR) / DECIMAL_FACTOR + SUFFIXES[suffix];
  }

  public static @NonNull String full(long amount) {
    return NumberFormat.getIntegerInstance(Locale.ROOT).format(amount);
  }
}
