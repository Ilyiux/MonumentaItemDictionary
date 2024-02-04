package dev.eliux.monumentaitemdictionary.gui.builder;

import dev.eliux.monumentaitemdictionary.gui.DictionaryController;
import dev.eliux.monumentaitemdictionary.gui.charm.DictionaryCharm;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;
import dev.eliux.monumentaitemdictionary.gui.widgets.BuildItemButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.ItemIconButtonWidget;
import dev.eliux.monumentaitemdictionary.util.ItemColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class BuilderGui extends Screen {
    public final List<String> itemTypesIndex = Arrays.asList("Mainhand", "Offhand", "Helmet", "Chestplate", "Leggings", "Boots");
    private BuildItemButtonWidget mainhandButton;
    private BuildItemButtonWidget offhandButton;
    private BuildItemButtonWidget headButton;
    private BuildItemButtonWidget chestplateButton;
    private BuildItemButtonWidget leggingsButton;
    private BuildItemButtonWidget bootsButton;
    private List<DictionaryCharm> charms;
    private BuildItemButtonWidget charmsButton;
    public List<DictionaryItem> buildItems = Arrays.asList(null, null, null, null, null, null);
    private final List<BuildItemButtonWidget> buildButtons = new ArrayList<>();
    public final int sideMenuWidth = 40;
    public final int labelMenuHeight = 30;
    private ItemIconButtonWidget showBuildDictionaryButton;
    private final DictionaryController controller;
    private ItemIconButtonWidget addBuildFromClipboardButton;

    public BuilderGui(Text title, DictionaryController controller) {
        super(title);
        this.controller = controller;
    }

    public void postInit() {
        addBuildFromClipboardButton = new ItemIconButtonWidget(5, 5, 20, 20, Text.literal(""), (button) -> {
            try {
                String buildUrl = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                if (verifyUrl(buildUrl)) {
                    getBuildFromUrl(buildUrl);
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, Text.literal("Add Build From Clipboard"), "name_tag", "");

        showBuildDictionaryButton = new ItemIconButtonWidget(width - sideMenuWidth + 10, labelMenuHeight + 10, 20, 20, Text.literal(""), (button) -> {
            controller.setBuildDictionaryScreen();
        }, Text.literal("Builds Data"), "diamond_chestplate", "");



        updateButtons();
    }

    public void updateButtons() {
        mainhandButton = new BuildItemButtonWidget((width/2), 40, 50, Text.literal(""), (b) -> {
            controller.getItemFromDictionary("Mainhand");
        }, buildItems.get(0), () -> controller.itemGui.generateItemLoreText(buildItems.get(0)), this);

        offhandButton = new BuildItemButtonWidget((width/2), 100, 50, Text.literal(""), (b) -> {
            controller.getItemFromDictionary("Offhand");
        }, buildItems.get(1), () -> controller.itemGui.generateItemLoreText(buildItems.get(1)), this);

        headButton = new BuildItemButtonWidget(10, 40, 50, Text.literal(""), (b) -> {
            controller.getItemFromDictionary("Helmet");
        }, buildItems.get(2), () -> controller.itemGui.generateItemLoreText(buildItems.get(2)), this);

        chestplateButton = new BuildItemButtonWidget(10, 100, 50, Text.literal(""), (b) -> {
            controller.getItemFromDictionary("Chestplate");
        }, buildItems.get(3), () -> controller.itemGui.generateItemLoreText(buildItems.get(3)), this);

        leggingsButton = new BuildItemButtonWidget(10, 160, 50, Text.literal(""), (b) -> {
            controller.getItemFromDictionary("Leggings");
        }, buildItems.get(4), () -> controller.itemGui.generateItemLoreText(buildItems.get(4)), this);

        bootsButton = new BuildItemButtonWidget(10, 220, 50, Text.literal(""), (b) -> {
            controller.getItemFromDictionary("Boots");
        }, buildItems.get(5), () -> controller.itemGui.generateItemLoreText(buildItems.get(5)), this);

        buildButtons.clear();
        buildButtons.add(mainhandButton);
        buildButtons.add(offhandButton);
        buildButtons.add(headButton);
        buildButtons.add(chestplateButton);
        buildButtons.add(leggingsButton);
        buildButtons.add(bootsButton);
    }

    private boolean verifyUrl(String buildUrl) {
        return buildUrl.contains("ohthemisery.tk/builder");
    }

    private void getBuildFromUrl(String buildUrl) {
        buildUrl = buildUrl.substring(buildUrl.indexOf("m="));
        DictionaryItem item;

        for (String rawItem : buildUrl.split("&")) {
            String itemName = String.join(" ", rawItem.substring(2).split("%20"));
            if (itemName.charAt(itemName.length()-2) == '-') {
                itemName = itemName.substring(0, itemName.length()-2);
            }
            String itemType = rawItem.substring(0, 1);

            item = controller.getItemByName(itemName);
            if (item == null) { continue; }

            switch (itemType) {
                case "m": buildItems.set(0, item); break;
                case "o": buildItems.set(1, item); break;
                case "h": buildItems.set(2, item); break;
                case "c": buildItems.set(3, item); break;
                case "l": buildItems.set(4, item); break;
                case "b": buildItems.set(5, item); break;
                default:
                    break;
            }
        }

        String[] rawCharms = buildUrl.substring(buildUrl.indexOf("charm=") + 6).split(",");
        List<DictionaryCharm> charms = new ArrayList<>();

        for (String charm : rawCharms) {
            charms.add(controller.getCharmByName(charm));
        }

        this.charms = charms;
        updateButtons();
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
        drawVerticalLine(matrices, width - sideMenuWidth - 1, labelMenuHeight, height, 0x77AAAAAA); // called twice to make the scroll bar render wider (janky, but I don't really care)
        drawVerticalLine(matrices, width - sideMenuWidth - 2, labelMenuHeight, height, 0x77AAAAAA);

        matrices.push();
        matrices.translate(0, 0, 110);
        addBuildFromClipboardButton.render(matrices, mouseX, mouseY, delta);
        showBuildDictionaryButton.render(matrices, mouseX, mouseY, delta);

        drawButtons(matrices, mouseX, mouseY, delta);
        drawItemText(matrices);

        matrices.pop();

        try {
            super.render(matrices, mouseX, mouseY, delta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawButtons(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int i = 0;
        for (BuildItemButtonWidget button : buildButtons) {
            drawTextWithShadow(matrices, textRenderer, Text.literal(itemTypesIndex.get(i)).setStyle(Style.EMPTY.withBold(true)), (i > 1 ? 70 : width/2 + 60), 40 + (i > 1 ? ((i-2)*60) : (i*60)), 0xFFFFFFFF);
            button.render(matrices, mouseX, mouseY, delta);
            i++;
        }
    }

    private void drawItemText(MatrixStack matrices) {
        int i = 0;
        for (DictionaryItem item : buildItems) {
            if (item != null) {
                drawTextWithShadow(matrices, textRenderer, Text.literal(item.name).setStyle(Style.EMPTY.withBold(true).withUnderline(true)), (i > 1 ? 70 : width/2 + 60), 50 + (i > 1 ? ((i-2)*60) : (i*60)), 0xFF000000 + ItemColors.getColorForLocation(item.location));
            }
            i++;
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        addBuildFromClipboardButton.mouseClicked(mouseX, mouseY, button);
        showBuildDictionaryButton.mouseClicked(mouseX, mouseY, button);

        mainhandButton.mouseClicked(mouseX, mouseY, button);
        offhandButton.mouseClicked(mouseX, mouseY, button);
        headButton.mouseClicked(mouseX, mouseY, button);
        chestplateButton.mouseClicked(mouseX, mouseY, button);
        leggingsButton.mouseClicked(mouseX, mouseY, button);
        bootsButton.mouseClicked(mouseX, mouseY, button);

        return true;
    }

    public void updateGuiPositions() {

    }
}
