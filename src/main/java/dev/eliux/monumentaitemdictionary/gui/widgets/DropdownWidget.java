package dev.eliux.monumentaitemdictionary.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DropdownWidget extends TextFieldWidget {
    private final TextRenderer textRenderer;

    private List<String> choices;
    private List<String> visualChoices;
    private ArrayList<String> validChoices;
    private ArrayList<String> visualValidChoices;
    private String lastChoice;
    private String visualLastChoice;
    private String defaultText;
    private int maxShown;
    private int scrollAmount;

    private final Consumer<String> onUpdate;

    public DropdownWidget(TextRenderer textRenderer, int x, int y, int width, Text text, String defaultText, List<String> choices, Consumer<String> onUpdate) {
        super(textRenderer, x, y, width, 14, text);
        this.textRenderer = textRenderer;
        this.onUpdate = onUpdate;

        this.choices = choices;
        this.visualChoices = choices;
        this.defaultText = defaultText;
        lastChoice = "";
        visualLastChoice = "";
        setText(visualLastChoice);
        validChoices = new ArrayList<>(choices);
        visualValidChoices = new ArrayList<>(visualChoices);
        updateMaxShown();
    }

    public DropdownWidget(TextRenderer textRenderer, int x, int y, int width, Text text, String defaultText, List<String> choices, List<String> visualChoices, Consumer<String> onUpdate) {
        super(textRenderer, x, y, width, 14, text);
        this.textRenderer = textRenderer;
        this.onUpdate = onUpdate;

        this.choices = choices;
        this.visualChoices = visualChoices;
        this.defaultText = defaultText;
        lastChoice = "";
        visualLastChoice = "";
        setText(visualLastChoice);
        validChoices = new ArrayList<>(choices);
        visualValidChoices = new ArrayList<>(visualChoices);
        updateMaxShown();
    }

    public void setChoices(List<String> newChoices) {
        choices = newChoices;
        visualChoices = newChoices;
        lastChoice = "";
        visualLastChoice = "";
        setText(visualLastChoice);
        // crashes when changing after a value is selected
    }

    public void setChoices(List<String> newChoices, List<String> newVisualChoices) {
        choices = newChoices;
        visualChoices = newVisualChoices;
        lastChoice = "";
        visualLastChoice = "";
        setText(visualLastChoice);
        // crashes when changing after a value is selected
    }

    public void setDefaultText(String newDefaultText) {
        defaultText = newDefaultText;
    }

    public String getLastChoice() {
        return lastChoice;
    }

    private void updateShownChoices() {
        validChoices.clear();
        visualValidChoices.clear();
        if (isFocused()) {
            if (getText().length() == 0) {
                validChoices = new ArrayList<>(choices);
                visualValidChoices = new ArrayList<>(visualChoices);
            } else {
                for (String choice : visualChoices) {
                    if (choice.toLowerCase().contains(getText().toLowerCase())) {
                        validChoices.add(choices.get(visualChoices.indexOf(choice)));
                        visualValidChoices.add(choice);
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
                    visualLastChoice = visualValidChoices.get(i + scrollAmount);
                    onUpdate.accept(lastChoice);

                    this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                }
            }
        }

        if (isFocused() && mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height) {
            setFocused(false);
        } else {
            super.mouseClicked(mouseX, mouseY, button);
        }

        if (isFocused()) {
            setText("");
            validChoices = new ArrayList<>(choices);
            visualValidChoices = new ArrayList<>(visualChoices);
        } else {
            setText(visualLastChoice);
        }

        return true;
    }

    public boolean willClick(double mouseX, double mouseY) {
        return isFocused() && (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + this.height) && mouseY < this.y + this.height + ((this.height + 1) * (validChoices.size() + 1));
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
    // called at normal render
    public void renderMain(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        if (lastChoice.equals("") && !isFocused()) textRenderer.drawWithShadow(matrices, defaultText, this.x + 3, this.y + 3, 0x666666);
    }

    // must be called manually
    // called after other render calls
    public void renderDropdown(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        matrices.translate(0, 0, 200);
        if (this.isFocused() && validChoices.size() > 0) {
            fill(matrices, this.x - 1, this.y + this.height, this.x + this.width + 1, this.y + this.height + ((this.height + 1) * Math.min(validChoices.size(), maxShown)) + 1, 0xFFA0A0A0);
            fill(matrices, this.x, this.y + this.height + 1, this.x + this.width, this.y + this.height + ((this.height + 1) * Math.min(validChoices.size(), maxShown)), 0xFF000000);

            // draw highlight under mouse
            for (int i = 0; i < Math.min(validChoices.size(), maxShown); i ++) {
                if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + this.height + ((this.height + 1) * i) && mouseY < this.y + this.height + ((this.height + 1) * (i + 1))) {
                    fill(matrices, this.x, this.y + this.height + ((this.height + 1) * i) + 1, this.x + this.width, this.y + this.height + ((this.height + 1) * (i + 1)), 0xFF212121);
                }
            }

            // draw dividing lines
            for (int i = 0; i < Math.min(validChoices.size(), maxShown) - 1; i ++) {
                drawHorizontalLine(matrices, this.x + 3, this.x + this.width - 4, this.y + this.height + ((this.height + 1) * (i + 1)), 0xFFA0A0A0);
            }

            // draw choice text
            for (int i = 0; i < Math.min(visualValidChoices.size(), maxShown); i ++) {
                String finalText;
                if (textRenderer.getWidth(visualValidChoices.get(i + scrollAmount)) > this.width - 8) {
                    finalText = textRenderer.trimToWidth(visualValidChoices.get(i + scrollAmount), this.width - 14) + "...";
                } else {
                    finalText = visualValidChoices.get(i + scrollAmount);
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
        matrices.pop();
    }
}
