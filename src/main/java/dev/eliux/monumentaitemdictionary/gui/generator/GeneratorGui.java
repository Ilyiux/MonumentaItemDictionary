package dev.eliux.monumentaitemdictionary.gui.generator;

import dev.eliux.monumentaitemdictionary.gui.DictionaryController;
import dev.eliux.monumentaitemdictionary.gui.charm.CharmDictionaryGui;
import dev.eliux.monumentaitemdictionary.gui.charm.DictionaryCharm;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;
import dev.eliux.monumentaitemdictionary.gui.item.ItemDictionaryGui;
import dev.eliux.monumentaitemdictionary.gui.widgets.ColorPickerWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.DropdownWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.ItemIconButtonWidget;
import dev.eliux.monumentaitemdictionary.util.ItemFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

import java.util.ArrayList;
import java.util.Arrays;

public class GeneratorGui extends Screen {
    public final int labelMenuHeight = 30;

    public final DictionaryController controller;

    private ItemIconButtonWidget backButton;
    private ButtonWidget giveButton;
    private ButtonWidget increaseMasterworkButton;
    private ButtonWidget decreaseMasterworkButton;
    private DropdownWidget colorSelectDropdown;
    private TextFieldWidget customColorTextField;
    private ColorPickerWidget colorPicker;

    private Class<?> lastFocused = null;
    private DictionaryItem focusedItem = null;
    private DictionaryCharm focusedCharm = null;

    private ItemStack generatedItem;
    private int dyeColor = -1;

    private int masterworkLevel = 0;

    public GeneratorGui(Text title, DictionaryController controller) {
        super(title);
        this.controller = controller;
    }

    public void postInit() {
        backButton = new ItemIconButtonWidget(5, 5, 20, 20, Text.literal(""), (button) -> {
            if (controller.lastOpenedScreen instanceof ItemDictionaryGui) {
                controller.setItemDictionaryScreen();
            } else if (controller.lastOpenedScreen instanceof CharmDictionaryGui) {
                controller.setCharmDictionaryScreen();
            }
        }, Text.literal("Go Back"), "arrow", "");

        giveButton = ButtonWidget.builder(Text.literal("Generate Item"), (button) -> ItemFactory.giveItemToClientPlayer(generatedItem)).dimensions(10, labelMenuHeight + 10, 90, 20).tooltip(Tooltip.of(Text.literal(""))).build();

        decreaseMasterworkButton = ButtonWidget.builder(Text.literal("<"), (button) -> {
            masterworkLevel--;
            if (masterworkLevel < focusedItem.getMinMasterwork()) masterworkLevel = focusedItem.getMinMasterwork();
            updateMasterworkDisabled();

            updateGeneratedItem();
        }).dimensions(10, labelMenuHeight + 60, 20, 20).tooltip(Tooltip.of(Text.literal(""))).build();
        increaseMasterworkButton = ButtonWidget.builder(Text.literal(">"), (button) -> {
            masterworkLevel++;
            if (masterworkLevel > focusedItem.getMaxMasterwork() - 1) masterworkLevel = focusedItem.getMaxMasterwork() - 1;
            updateMasterworkDisabled();

            updateGeneratedItem();
        }).dimensions(40, labelMenuHeight + 60, 20, 20).tooltip(Tooltip.of(Text.literal(""))).build();

        ArrayList<String> possibleColors = new ArrayList<>(Arrays.asList("Default", "Undyed", "Custom"));
        for (DyeColor color : DyeColor.values()) {
            possibleColors.add(color.asString());
        }
        colorSelectDropdown = new DropdownWidget(textRenderer, 10, 134, 90, Text.literal(""), "Default", possibleColors, (e) -> {
            customColorTextField.visible = e.equals("Custom");
            colorPicker.visible = e.equals("Custom");

            if (e.equals("Default")) {
                dyeColor = -1;
            } else if (e.equals("Undyed")) {
                dyeColor = DyeableItem.DEFAULT_COLOR;
            } else if (!e.equals("Custom")) {
                float[] colorComponents = DyeColor.byName(e, DyeColor.WHITE).getColorComponents();
                dyeColor = (int)(0xFF0000 * colorComponents[0] + 0xFF00 * colorComponents[1] + 0xFF * colorComponents[2]);
            }

            updateGeneratedItem();
        });
        customColorTextField = new TextFieldWidget(textRenderer, 10, 154, 70, 14, Text.literal(""));
        customColorTextField.setText("FF0000");
        customColorTextField.setChangedListener((t) -> {
            long parsed;
            try {
                parsed = Long.parseLong(t.replace("#", ""), 16);
            } catch (NumberFormatException e) {
                parsed = 0;
            }
            dyeColor = Math.min((int)parsed, 0xFFFFFF);

            updateGeneratedItem();
        });
        customColorTextField.visible = false;
        colorPicker = new ColorPickerWidget(86, 154, 14, 14, 120, 80, (i) -> {
            dyeColor = i;
            customColorTextField.setText(Integer.toHexString(i&0xFFFFFF));

            updateGeneratedItem();
        });
        colorPicker.visible = false;
    }

    private void updateMasterworkDisabled() {
        if (lastFocused == DictionaryItem.class && focusedItem.hasMasterwork) {
            decreaseMasterworkButton.active = masterworkLevel != focusedItem.getMinMasterwork();
            increaseMasterworkButton.active = masterworkLevel != focusedItem.getMaxMasterwork() - 1;
        }
    }

    public void setItem(DictionaryItem focusedItem) {
        this.focusedItem = focusedItem;
        lastFocused = DictionaryItem.class;
        masterworkLevel = focusedItem.getMinMasterwork();
        dyeColor = -1;

        updateGeneratedItem();
        updateMasterworkDisabled();
    }

    public void setCharm(DictionaryCharm focusedCharm) {
        this.focusedCharm = focusedCharm;
        lastFocused = DictionaryCharm.class;
        dyeColor = -1;

        updateGeneratedItem();
    }

    private void updateGeneratedItem() {
        if (lastFocused == DictionaryItem.class) {
            String nbt = focusedItem.hasMasterwork ? focusedItem.getNbtFromMasterwork(masterworkLevel) : focusedItem.getNbtNoMasterwork();
            generatedItem = ItemFactory.fromEncodingWithStringNbt(focusedItem.baseItem.split("/")[0].trim().toLowerCase().replace(" ", "_"), nbt);
        } else {
            generatedItem = ItemFactory.fromEncodingWithStringNbt(focusedCharm.baseItem.split("/")[0].trim().toLowerCase().replace(" ", "_"), focusedCharm.nbt);
        }

        if (generatedItem.getItem() instanceof DyeableItem item && dyeColor != -1) {
            item.setColor(generatedItem, dyeColor);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.renderBackground(matrices);

        textRenderer.drawTrimmed(matrices, StringVisitable.plain("Preview:"), 130, labelMenuHeight + 23, 60, 0xFFFFFFFF);
        if (lastFocused != null && MinecraftClient.getInstance().currentScreen != null) {

            int minX = 175;
            int minY = labelMenuHeight + 14;
            int maxX = 199;
            int maxY = labelMenuHeight + 38;

            fill(matrices, minX, minY, maxX, maxY, 0x6B666666);
            drawHorizontalLine(matrices, minX, maxX, minY, 0xFFCCCCCC);
            drawHorizontalLine(matrices, minX, maxX, maxY, 0xFFCCCCCC);
            drawVerticalLine(matrices, minX, minY, maxY, 0xFFCCCCCC);
            drawVerticalLine(matrices, maxX, minY, maxY, 0xFFCCCCCC);

            MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(matrices, generatedItem, 180, labelMenuHeight + 18);

            MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, generatedItem.getTooltip(MinecraftClient.getInstance().player, MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipContext.ADVANCED : TooltipContext.BASIC), 118, labelMenuHeight + 61);
        }

        // draw vertical line
        matrices.push();
        matrices.translate(0, 0, 100);
        drawVerticalLine(matrices, 113, labelMenuHeight, height, 0xFFFFFFFF);
        matrices.pop();

        giveButton.render(matrices, mouseX, mouseY, delta);

        if (lastFocused == DictionaryItem.class && focusedItem.hasMasterwork) {
            matrices.push();
            matrices.translate(0, 0, 200);
            textRenderer.drawTrimmed(matrices, StringVisitable.plain("Change Masterwork Level"), 10, labelMenuHeight + 40, 100, 0xFFFFFFFF);
            decreaseMasterworkButton.render(matrices, mouseX, mouseY, delta);
            increaseMasterworkButton.render(matrices, mouseX, mouseY, delta);
            matrices.pop();
        }

        if (generatedItem.getItem() instanceof DyeableItem) {
            matrices.push();
            matrices.translate(0, 0, 200);
            textRenderer.drawTrimmed(matrices, StringVisitable.plain("Set Dye Color"), 10, labelMenuHeight + 92, 100, 0xFFFFFFFF);
            colorSelectDropdown.renderMain(matrices, mouseX, mouseY, delta);
            customColorTextField.render(matrices, mouseX, mouseY, delta);
            colorPicker.renderMain(matrices, mouseX, mouseY, delta);
            colorSelectDropdown.renderDropdown(matrices, mouseX, mouseY, delta);
            colorPicker.renderPopup(matrices, mouseX, mouseY, delta);
            matrices.pop();
        }

        // draw the label at the tops
        matrices.push();
        matrices.translate(0, 0, 100);
        fill(matrices, 0, 0, width, labelMenuHeight, 0xFF555555);
        drawHorizontalLine(matrices, 0, width, labelMenuHeight, 0xFFFFFFFF);
        drawCenteredTextWithShadow(matrices, textRenderer, Text.literal("Monumenta Item Generator").setStyle(Style.EMPTY.withBold(true)), width / 2, (labelMenuHeight - textRenderer.fontHeight) / 2, 0xFFeb4034);
        matrices.pop();

        matrices.push();
        matrices.translate(0, 0, 110);
        backButton.render(matrices, mouseX, mouseY, delta);
        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        backButton.mouseClicked(mouseX, mouseY, button);
        giveButton.mouseClicked(mouseX, mouseY, button);

        if (lastFocused == DictionaryItem.class && focusedItem.hasMasterwork) {
            decreaseMasterworkButton.mouseClicked(mouseX, mouseY, button);
            increaseMasterworkButton.mouseClicked(mouseX, mouseY, button);
        }

        if (colorSelectDropdown.willClick(mouseX, mouseY)) {
            colorSelectDropdown.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        if (colorPicker.willClick(mouseX, mouseY)) {
            colorPicker.mouseClicked(mouseX, mouseY, button);
            return true;
        }

        colorSelectDropdown.mouseClicked(mouseX, mouseY, button);
        customColorTextField.mouseClicked(mouseX, mouseY, button);
        colorPicker.mouseClicked(mouseX, mouseY, button);

        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);

        colorSelectDropdown.charTyped(chr, modifiers);
        customColorTextField.charTyped(chr, modifiers);

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);

        colorSelectDropdown.keyPressed(keyCode, scanCode, modifiers);
        customColorTextField.keyPressed(keyCode, scanCode, modifiers);

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        super.mouseScrolled(mouseX, mouseY, amount);

        colorSelectDropdown.mouseScrolled(mouseX, mouseY, amount);

        return true;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);


    }
}
