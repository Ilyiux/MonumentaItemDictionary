package dev.eliux.monumentaitemdictionary.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DropdownWidget extends TextFieldWidget {
    private final TextRenderer textRenderer;

    private final ArrayList<String> choices;
    private ArrayList<String> validChoices;
    private String lastChoice;
    private int maxShown;
    private int scrollAmount;

    private final Consumer<String> onUpdate;

    public DropdownWidget(TextRenderer textRenderer, int x, int y, int width, Text text, ArrayList<String> choices, Consumer<String> onUpdate) {
        super(textRenderer, x, y, width, 14, text);
        this.textRenderer = textRenderer;
        this.onUpdate = onUpdate;

        this.choices = choices;
        lastChoice = "-";
        setText(lastChoice);
        validChoices = new ArrayList<>(choices);
        updateMaxShown();
    }

    private void updateShownChoices() {
        validChoices.clear();
        if (isFocused()) {
            if (getText().length() == 0) {
                validChoices = new ArrayList<>(choices);
            } else {
                for (String choice : choices) {
                    if (choice.toLowerCase().contains(getText().toLowerCase())) {
                        validChoices.add(choice);
                    }
                }
            }
        }
        updateScrollLimits();
    }

    private void updateMaxShown() {
        if (MinecraftClient.getInstance().currentScreen == null) return;
        maxShown = (int) ((double) (MinecraftClient.getInstance().currentScreen.height - (this.y + this.height)) / (double) (this.height + 1));
    }

    private void updateScrollLimits() {
        if (scrollAmount > validChoices.size() - maxShown) scrollAmount = validChoices.size() - maxShown;
        if (scrollAmount < 0) scrollAmount = 0;
    }

    // must be called manually
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isFocused()) {
            for (int i = 0; i < Math.min(validChoices.size(), maxShown); i ++) {
                if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + this.height + ((this.height + 1) * i) && mouseY < this.y + this.height + ((this.height + 1) * (i + 1))) {
                    lastChoice = validChoices.get(i + scrollAmount);
                    onUpdate.accept(lastChoice);
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, button);

        if (isFocused()) {
            setText("");
            validChoices = new ArrayList<>(choices);
        } else {
            setText(lastChoice);
        }

        return true;
    }

    // must be called manually
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        updateShownChoices();
        return true;
    }

    // must be called manually
    @Override
    public boolean charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        updateShownChoices();
        return true;
    }

    // must be called manually
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        super.mouseScrolled(mouseX, mouseY, amount);

        if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + ((this.height + 1) * (Math.min(validChoices.size(), maxShown) + 1))) {
            // mouse is in scroll area
            if (validChoices.size() > maxShown) {
                // should be able to scroll
                scrollAmount -= amount;
                updateScrollLimits();
            }
        }

        return true;
    }

    // must be called manually
    public void resize(MinecraftClient client, int width, int height) {
        updateMaxShown();
        updateScrollLimits();
    }

    // must be called manually
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.isFocused()) {
            fill(matrices, this.x - 1, this.y + this.height, this.x + this.width + 1, this.y + this.height + ((this.height + 1) * Math.min(validChoices.size(), maxShown)) + 1, 0xFFA0A0A0);
            fill(matrices, this.x, this.y + this.height + 1, this.x + this.width, this.y + this.height + ((this.height + 1) * Math.min(validChoices.size(), maxShown)), 0xFF000000);

            // draw highlight under mouse
            for (int i = 0; i < Math.min(validChoices.size(), maxShown); i ++) {
                if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + this.height + ((this.height + 1) * i) && mouseY < this.y + this.height + ((this.height + 1) * (i + 1))) {
                    fill(matrices, this.x, this.y + this.height + ((this.height + 1) * i), this.x + this.width, this.y + this.height + ((this.height + 1) * (i + 1)), 0xFF212121);
                }
            }

            // draw dividing lines
            for (int i = 0; i < Math.min(validChoices.size(), maxShown) - 1; i ++) {
                drawHorizontalLine(matrices, this.x + 3, this.x + this.width - 4, this.y + this.height + ((this.height + 1) * (i + 1)), 0xFFA0A0A0);
            }

            // draw choice text
            for (int i = 0; i < Math.min(validChoices.size(), maxShown); i ++) {
                String finalText;
                if (textRenderer.getWidth(validChoices.get(i + scrollAmount)) > this.width - 8) {
                    finalText = textRenderer.trimToWidth(validChoices.get(i + scrollAmount), this.width - 14) + "...";
                } else {
                    finalText = validChoices.get(i + scrollAmount);
                }
                //String finalText = textRenderer.trimToWidth(validChoices.get(i), this.width - 8);
                textRenderer.drawWithShadow(matrices, finalText, this.x + 4, this.y + this.height + ((this.height + 1) * i) + 4, 0xFFFFFFFF);
            }

            // draw scroll bar if needed
            if (validChoices.size() > maxShown) {
                drawVerticalLine(matrices, this.x + this.width - 1, this.y + this.height,  this.y + ((this.height + 1) * (maxShown + 1)) - 1, 0xFF303030);
                int scrollBarPixels = ((this.height + 1) * (maxShown + 1)) - 1 - this.height;
                drawVerticalLine(matrices, this.x + this.width - 1, this.y + this.height + (int)(scrollBarPixels * ((double)scrollAmount / validChoices.size())), this.y + this.height + (int)(scrollBarPixels * ((double)(scrollAmount + maxShown) / validChoices.size())), 0xFF505050);
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }
}
