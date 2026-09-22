package net.cinderlabsmc.cinderlib.client.gui.render;

import org.jspecify.annotations.NonNull;

public record Bounds(int left, int top, int width, int height) {

    public int right() {
        return left + width;
    }

    public int bottom() {
        return top + height;
    }

    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= left && mouseX < right() && mouseY >= top && mouseY < bottom();
    }

    public @NonNull Bounds offset(int offsetLeft, int offsetTop) {
        return new Bounds(left + offsetLeft, top + offsetTop, width, height);
    }

    public @NonNull Bounds below(int gap, int height) {
        return new Bounds(left, bottom() + gap, width, height);
    }

    public @NonNull Bounds rightOf(int gap, int width) {
        return new Bounds(right() + gap, top, width, height);
    }

    public @NonNull Bounds withSize(int width, int height) {
        return new Bounds(left, top, width, height);
    }
}
