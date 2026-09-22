package net.cinderlabsmc.cinderlib.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.cinderlabsmc.cinderlib.client.gui.element.IGuiElement;
import net.cinderlabsmc.cinderlib.client.gui.render.Bounds;
import net.cinderlabsmc.cinderlib.client.gui.theme.ICinderTheme;
import net.cinderlabsmc.cinderlib.client.gui.theme.VanillaTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class CinderScreen<Menu extends AbstractContainerMenu> extends AbstractContainerScreen<Menu> {

    private final List<IGuiElement> elements = new ArrayList<>();

    protected CinderScreen(@NonNull Menu menu, @NonNull Inventory inventory, @NonNull Component title, int width, int height) {
        super(menu, inventory, title, width, height);
    }

    protected abstract void build(@NonNull CinderLayout layout);

    protected @NonNull ICinderTheme theme() {
        return VanillaTheme.INSTANCE;
    }

    @Override
    protected void init() {
        super.init();
        elements.clear();
        build(new CinderLayout(this, theme(), leftPos, topPos));
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        var theme = theme();

        theme.panel(graphics, new Bounds(leftPos, topPos, imageWidth, imageHeight));

        for (var slot : menu.slots) {
            theme.slot(graphics, leftPos + slot.x, topPos + slot.y);
        }

        for (var element : elements) {
            element.render(graphics, mouseX, mouseY);
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if (hoveredSlot != null) {
            return;
        }

        for (var element : elements) {
            var tooltip = element.tooltip(mouseX, mouseY);

            if (tooltip.isEmpty()) {
                continue;
            }

            graphics.setComponentTooltipForNextFrame(font, tooltip, mouseX, mouseY);
            return;
        }
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int color = theme().textColor();

        graphics.text(font, title, titleLabelX, titleLabelY, color, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, color, false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }

        return getChildAt(mouseX, mouseY).filter(child -> child.mouseScrolled(mouseX, mouseY, scrollX, scrollY)).isPresent();
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (event.key() == InputConstants.KEY_ESCAPE || !(getFocused() instanceof EditBox field) || !field.canConsumeInput()) {
            return super.keyPressed(event);
        }

        field.keyPressed(event);
        return true;
    }

    void addElement(@NonNull IGuiElement element) {
        elements.add(element);
    }

    <Widget extends AbstractWidget> Widget addCinderWidget(@NonNull Widget widget) {
        return addRenderableWidget(widget);
    }
}
