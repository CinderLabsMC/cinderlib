package net.cinderlabsmc.cinderlib.client.gui.theme;

import org.jspecify.annotations.NonNull;

import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.cinderlabsmc.cinderlib.client.gui.render.GuiSizes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public final class VanillaTheme implements ICinderTheme {

  public static final VanillaTheme INSTANCE = new VanillaTheme();

  private static final Identifier PANEL_TEXTURE = Identifier
      .withDefaultNamespace("textures/gui/container/generic_54.png");
  private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");
  private static final Identifier HIGHLIGHT_BACK_SPRITE = Identifier
      .withDefaultNamespace("container/slot_highlight_back");
  private static final Identifier HIGHLIGHT_FRONT_SPRITE = Identifier
      .withDefaultNamespace("container/slot_highlight_front");
  private static final Identifier SCROLLER_SPRITE = Identifier
      .withDefaultNamespace("container/creative_inventory/scroller");
  private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier
      .withDefaultNamespace("container/creative_inventory/scroller_disabled");
  private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
      Identifier.withDefaultNamespace("widget/button"), Identifier.withDefaultNamespace("widget/button_disabled"),
      Identifier.withDefaultNamespace("widget/button_highlighted"));

  private static final int TEXTURE_SIZE = 256;
  private static final int PANEL_WIDTH = 176;
  private static final int PANEL_HEIGHT = 222;
  private static final int CORNER = 4;
  private static final int CENTER = 1;
  private static final int HIGHLIGHT_SIZE = 24;
  private static final int HIGHLIGHT_OFFSET = 4;

  private final FlatTheme flat = FlatTheme.LIGHT;

  private VanillaTheme() {
  }

  @Override
  public void panel(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds) {
    int left = bounds.left();
    int top = bounds.top();
    int innerWidth = bounds.width() - 2 * CORNER;
    int innerHeight = bounds.height() - 2 * CORNER;
    int right = bounds.right() - CORNER;
    int bottom = bounds.bottom() - CORNER;
    int sourceRight = PANEL_WIDTH - CORNER;
    int sourceBottom = PANEL_HEIGHT - CORNER;

    panelPart(graphics, new Bounds(left, top, CORNER, CORNER), 0, 0, CORNER, CORNER);
    panelPart(graphics, new Bounds(right, top, CORNER, CORNER), sourceRight, 0, CORNER, CORNER);
    panelPart(graphics, new Bounds(left, bottom, CORNER, CORNER), 0, sourceBottom, CORNER, CORNER);
    panelPart(graphics, new Bounds(right, bottom, CORNER, CORNER), sourceRight, sourceBottom, CORNER, CORNER);
    panelPart(graphics, new Bounds(left + CORNER, top, innerWidth, CORNER), CORNER, 0, CENTER, CORNER);
    panelPart(graphics, new Bounds(left + CORNER, bottom, innerWidth, CORNER), CORNER, sourceBottom, CENTER, CORNER);
    panelPart(graphics, new Bounds(left, top + CORNER, CORNER, innerHeight), 0, CORNER, CORNER, CENTER);
    panelPart(graphics, new Bounds(right, top + CORNER, CORNER, innerHeight), sourceRight, CORNER, CORNER, CENTER);
    panelPart(graphics, new Bounds(left + CORNER, top + CORNER, innerWidth, innerHeight), CORNER, CORNER, CENTER,
        CENTER);
  }

  @Override
  public void slot(@NonNull GuiGraphicsExtractor graphics, int itemLeft, int itemTop) {
    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, itemLeft - GuiSizes.SLOT_BORDER,
        itemTop - GuiSizes.SLOT_BORDER, GuiSizes.SLOT_SIZE, GuiSizes.SLOT_SIZE);
  }

  @Override
  public void inset(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds) {
    flat.inset(graphics, bounds);
  }

  @Override
  public void button(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds, boolean hovered, boolean active) {
    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_SPRITES.get(active, hovered), bounds.left(), bounds.top(),
        bounds.width(), bounds.height());
  }

  @Override
  public void scrollHandle(@NonNull GuiGraphicsExtractor graphics, @NonNull Bounds bounds, boolean active) {
    var sprite = active ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;

    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, bounds.left(), bounds.top(), bounds.width(),
        bounds.height());
  }

  @Override
  public void highlightBack(@NonNull GuiGraphicsExtractor graphics, int itemLeft, int itemTop) {
    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HIGHLIGHT_BACK_SPRITE, itemLeft - HIGHLIGHT_OFFSET,
        itemTop - HIGHLIGHT_OFFSET, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE);
  }

  @Override
  public void highlightFront(@NonNull GuiGraphicsExtractor graphics, int itemLeft, int itemTop) {
    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HIGHLIGHT_FRONT_SPRITE, itemLeft - HIGHLIGHT_OFFSET,
        itemTop - HIGHLIGHT_OFFSET, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE);
  }

  @Override
  public int textColor() {
    return flat.textColor();
  }

  @Override
  public int buttonTextColor() {
    return flat.buttonTextColor();
  }

  @Override
  public int amountColor() {
    return flat.amountColor();
  }

  @Override
  public int barBackground() {
    return flat.barBackground();
  }

  private static void panelPart(GuiGraphicsExtractor graphics, Bounds target, int sourceLeft, int sourceTop,
      int sourceWidth, int sourceHeight) {
    if (target.width() <= 0 || target.height() <= 0) {
      return;
    }

    graphics.blit(RenderPipelines.GUI_TEXTURED, PANEL_TEXTURE, target.left(), target.top(), sourceLeft, sourceTop,
        target.width(), target.height(), sourceWidth, sourceHeight, TEXTURE_SIZE, TEXTURE_SIZE);
  }
}
