package net.cinderlabsmc.cinderlib.client.gui.widget;

import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.cinderlabsmc.cinderlib.client.gui.render.GuiPainter;
import net.cinderlabsmc.cinderlib.client.gui.render.GuiTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public final class Scrollbar extends AbstractWidget {

    public static final int WIDTH = 12;

    private static final int HANDLE_HEIGHT = 15;
    private static final int HANDLE_INSET = 1;

    private int offset;
    private int maxOffset;

    public Scrollbar(int left, int top, int height) {
        super(left, top, WIDTH, height, Component.empty());
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
        GuiPainter.fill(graphics, new Bounds(getX(), getY(), width, height), GuiTheme.SLOT_DARK);

        int color = maxOffset == 0 ? GuiTheme.SCROLL_HANDLE_DISABLED : GuiTheme.SCROLL_HANDLE;
        var handle = new Bounds(getX() + HANDLE_INSET, handleTop(), width - 2 * HANDLE_INSET, HANDLE_HEIGHT);

        GuiPainter.fill(graphics, handle, color);
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

        double travel = height - HANDLE_HEIGHT;
        double ratio = (mouseY - getY() - HANDLE_HEIGHT / 2.0) / travel;

        setOffset((int) Math.round(ratio * maxOffset));
    }

    private int handleTop() {
        if (maxOffset == 0) {
            return getY();
        }

        return getY() + (height - HANDLE_HEIGHT) * offset / maxOffset;
    }
}
