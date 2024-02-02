package dev.eliux.monumentaitemdictionary.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.eliux.monumentaitemdictionary.gui.builder.BuilderGui;
import dev.eliux.monumentaitemdictionary.gui.builder.DictionaryBuild;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class BuildButtonWidget extends ButtonWidget {
    private final DictionaryBuild build;
    private final BuilderGui gui;
    public BuildButtonWidget(int x, int y, int itemSize, Text message, PressAction onPress, DictionaryBuild build, BuilderGui gui) {
        super(x, y, itemSize, itemSize, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.build = build;
        this.gui = gui;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int minX = getX();
        int minY = getY();
        int maxX = minX + width;
        int maxY = minY + height;

        // rendering breaks if I do not use this, what is this, why do I have to use this, I don't know
        RenderSystem.enableDepthTest();

        boolean hovered = (mouseX >= minX) && (mouseX <= maxX) && (mouseY >= minY) && (mouseY <= maxY) && (mouseY > gui.labelMenuHeight);

        int outlineColor = hovered ? 0xFFC6C6C6 : 0xFFFFFFFF;
        int fillOpacity = hovered ? 0x6B000000 : 0x88000000;

        fill(matrices, minX, minY, maxX, maxY, fillOpacity);
        drawHorizontalLine(matrices, minX, maxX, minY, outlineColor);
        drawHorizontalLine(matrices, minX, maxX, maxY, outlineColor);
        drawVerticalLine(matrices, minX, minY, maxY, outlineColor);
        drawVerticalLine(matrices, maxX, minY, maxY, outlineColor);

        if (hovered) {
            List<Text> lines = new ArrayList<>();
            lines.add(Text.literal("Amazing"));
            gui.renderTooltip(matrices, lines, mouseX, mouseY);
        }
    }
}
