package dev.eliux.monumentaitemdictionary.gui.builder;

import dev.eliux.monumentaitemdictionary.gui.DictionaryController;
import dev.eliux.monumentaitemdictionary.gui.widgets.ItemIconButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;

public class BuilderGui  extends Screen {
    public final int labelMenuHeight = 30;
    public final DictionaryController controller;
    public BuilderGui(Text title, DictionaryController controller) {
        super(title);
        this.controller = controller;
    }

    public void postInit() {
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        matrices.push();
        matrices.translate(0, 0, 110);
        fill(matrices, 0, 0, width, labelMenuHeight, 0xFF555555);
        drawHorizontalLine(matrices, 0, width, labelMenuHeight, 0xFFFFFFFF);
        drawCenteredTextWithShadow(matrices, textRenderer, Text.literal("Monumenta Builder").setStyle(Style.EMPTY.withBold(true)), width / 2, (labelMenuHeight - textRenderer.fontHeight) / 2, 0xFF2ca9d3);
        matrices.pop();

        try {
            super.render(matrices, mouseX, mouseY, delta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
    }

    public void updateGuiPositions() {
    }
}
