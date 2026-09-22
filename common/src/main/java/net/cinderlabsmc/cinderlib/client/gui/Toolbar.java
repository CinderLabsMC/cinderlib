package net.cinderlabsmc.cinderlib.client.gui;

import net.cinderlabsmc.cinderlib.client.gui.widget.IconButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Toolbar {

    private static final int GAP = 2;

    private final CinderLayout layout;
    private final int left;
    private final int top;

    private int count;

    Toolbar(@NonNull CinderLayout layout, int left, int top) {
        this.layout = layout;
        this.left = left;
        this.top = top;
    }

    public @NonNull IconButton<ItemStack> button(@NonNull ItemStack icon, @NonNull Component name, @NonNull Runnable action) {
        return cycle(List.of(icon), icon, Function.identity(), _ -> name, _ -> action.run());
    }

    public @NonNull IconButton<Boolean> toggle(@NonNull ItemStack onIcon, @NonNull ItemStack offIcon, @NonNull Component name, boolean initial, @NonNull Consumer<Boolean> onChange) {
        return cycle(List.of(false, true), initial, enabled -> enabled ? onIcon : offIcon, enabled -> CommonComponents.optionStatus(name, enabled), onChange);
    }

    public <Value> @NonNull IconButton<Value> cycle(@NonNull List<Value> values, @NonNull Value initial, @NonNull Function<Value, ItemStack> icons, @NonNull Function<Value, Component> names, @NonNull Consumer<Value> onChange) {
        int buttonTop = top + count * (IconButton.SIZE + GAP);

        count++;

        return layout.widget(left, buttonTop, new IconButton<>(0, 0, layout.theme(), values, initial, icons, names, onChange));
    }
}
