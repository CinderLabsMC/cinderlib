package net.cinderlabsmc.cinderlib.client.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.cinderlabsmc.cinderlib.CinderLib;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public final class SearchField extends EditBox {

    private static final Component HINT = Component.translatable("gui." + CinderLib.MOD_ID + ".search");
    private static final String EMPTY = "";
    private static final int MAX_LENGTH = 256;

    public SearchField(@NonNull Font font, int left, int top, int width, int height, @NonNull Consumer<String> onChange) {
        super(font, left, top, width, height, HINT);
        setHint(HINT);
        setMaxLength(MAX_LENGTH);
        setResponder(onChange);
    }

    public void clear() {
        setValue(EMPTY);
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
