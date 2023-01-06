package dev.eliux.monumentaitemdictionary.gui;

import dev.eliux.monumentaitemdictionary.gui.widgets.DropdownWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class ItemFilterGui extends Screen {
    private DropdownWidget dropdownWidget;

    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    public final DictionaryController controller;

    public ItemFilterGui(Text title, DictionaryController controller) {
        super(title);
        this.controller = controller;

        dropdownWidget = new DropdownWidget(textRenderer, 10, 10, 120, new LiteralText("Garbage"), controller.getAllStats(), (s) -> {
            System.out.println(s);
        });
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        dropdownWidget.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        dropdownWidget.mouseClicked(mouseX, mouseY, button);

        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);

        dropdownWidget.charTyped(chr, modifiers);

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);

        dropdownWidget.keyPressed(keyCode, scanCode, modifiers);

        return true;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);

        dropdownWidget.resize(client, width, height);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        super.mouseScrolled(mouseX, mouseY, amount);

        dropdownWidget.mouseScrolled(mouseX, mouseY, amount);

        return true;
    }
}
