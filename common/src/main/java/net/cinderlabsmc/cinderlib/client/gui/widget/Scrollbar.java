package net.cinderlabsmc.cinderlib.client.gui.widget;

import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.cinderlabsmc.cinderlib.client.gui.theme.ICinderTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public final class Scrollbar extends AbstractWidget {

    public static final int WIDTH = 14;

    private static final int HANDLE_WIDTH = 12;
    private static final int HANDLE_HEIGHT = 15;
    private static final int INSET = 1;

    private final ICinderTheme theme;

    private int offset;
    private int maxOffset;

    public Scrollbar(int left, int top, int height, @NonNull ICinderTheme theme) {
        super(left, top, WIDTH, height, Component.empty());
        this.theme = theme;
    }

    public int offset() {
        return offset;
    }

    public void setMaxOffset(int maxOffset) {
        this.maxOffset = Math.max(maxOffset, 0);
        setOffset(offset);
    }

    public void scroll(int rows) {
        setOffset(offset + rows);
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        theme.inset(graphics, new Bounds(getX(), getY(), width, height));
        theme.scrollHandle(graphics, new Bounds(getX() + INSET, handleTop(), HANDLE_WIDTH, HANDLE_HEIGHT), maxOffset > 0);
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        scrollTo(event.y());
    }

    @Override
    protected void onDrag(@NonNull MouseButtonEvent event, double dragX, double dragY) {
        scrollTo(event.y());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY == 0) {
            return false;
        }

        scroll((int) -Math.signum(scrollY));
        return true;
    }

    @Override
    public void playDownSound(@NonNull SoundManager soundManager) {
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
    }

    private void setOffset(int offset) {
        this.offset = Math.clamp(offset, 0, maxOffset);
    }

    private void scrollTo(double mouseY) {
        if (maxOffset == 0) {
            return;
        }

        double ratio = (mouseY - getY() - INSET - HANDLE_HEIGHT / 2.0) / travel();

        setOffset((int) Math.round(ratio * maxOffset));
    }

    private int handleTop() {
        if (maxOffset == 0) {
            return getY() + INSET;
        }

        return getY() + INSET + travel() * offset / maxOffset;
    }

    private int travel() {
        return height - 2 * INSET - HANDLE_HEIGHT;
    }
}
