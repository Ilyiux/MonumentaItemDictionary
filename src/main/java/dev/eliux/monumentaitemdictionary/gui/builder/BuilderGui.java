package dev.eliux.monumentaitemdictionary.gui.builder;

import dev.eliux.monumentaitemdictionary.gui.DictionaryController;
import dev.eliux.monumentaitemdictionary.gui.charm.DictionaryCharm;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;
import dev.eliux.monumentaitemdictionary.gui.widgets.BuildCharmButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.BuildItemButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.ItemIconButtonWidget;
import dev.eliux.monumentaitemdictionary.util.ItemColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

public class BuilderGui extends Screen {
    public final List<String> itemTypesIndex = Arrays.asList("Mainhand", "Offhand", "Helmet", "Chestplate", "Leggings", "Boots");
    private BuildItemButtonWidget mainhandButton;
    private BuildItemButtonWidget offhandButton;
    private BuildItemButtonWidget headButton;
    private BuildItemButtonWidget chestplateButton;
    private BuildItemButtonWidget leggingsButton;
    private BuildItemButtonWidget bootsButton;
    public List<DictionaryCharm> charms = new ArrayList<>();
    private BuildCharmButtonWidget charmsButton;
    public List<DictionaryItem> buildItems = Arrays.asList(null, null, null, null, null, null);
    private final List<BuildItemButtonWidget> buildItemButtons = new ArrayList<>();
    private final List<BuildCharmButtonWidget> buildCharmButtons = new ArrayList<>();
    public final int sideMenuWidth = 40;
    public final int labelMenuHeight = 30;
    private TextFieldWidget nameBar;
    private ItemIconButtonWidget showBuildDictionaryButton;
    private final DictionaryController controller;
    private ItemIconButtonWidget setBuildFromClipboardButton;
    private ItemIconButtonWidget addBuildButton;

    public BuilderGui(Text title, DictionaryController controller) {
        super(title);
        this.controller = controller;
    }

    public void postInit() {
        nameBar = new TextFieldWidget(textRenderer, width / 2 +90, 7, width / 2 - 100, 15, Text.literal("Build Name"));

        setBuildFromClipboardButton = new ItemIconButtonWidget(30, 5, 20, 20, Text.literal(""), (button) -> {
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

        addBuildButton = new ItemIconButtonWidget(5, 5, 20, 20, Text.literal(""), (button) -> {
            controller.buildDictionaryGui.addBuild(nameBar.getText(), buildItems, charms);
            resetBuild();
            controller.setBuildDictionaryScreen();
        }, Text.literal("Add Build To Dictionary"), "writable_book", "");

        updateButtons();
    }

    public void updateButtons() {
        mainhandButton = new BuildItemButtonWidget((width/2), 40, 50, Text.literal(""), (b) -> {
            if (!hasShiftDown() && !hasControlDown()) controller.getItemFromDictionary("Mainhand");
            else if (hasShiftDown() && hasControlDown()){
                String wikiFormatted = buildItems.get(0).name.replace(" ", "_").replace("'", "%27");
                Util.getOperatingSystem().open("https://monumenta.wiki.gg/wiki/" + wikiFormatted);
            }
        }, buildItems.get(0), () -> controller.itemGui.generateItemLoreText(buildItems.get(0)), this);

        offhandButton = new BuildItemButtonWidget((width/2), 100, 50, Text.literal(""), (b) -> {
            if (!hasShiftDown() && !hasControlDown()) controller.getItemFromDictionary("Offhand");
            else if (hasShiftDown() && hasControlDown()){
                String wikiFormatted = buildItems.get(1).name.replace(" ", "_").replace("'", "%27");
                Util.getOperatingSystem().open("https://monumenta.wiki.gg/wiki/" + wikiFormatted);
            }
        }, buildItems.get(1), () -> controller.itemGui.generateItemLoreText(buildItems.get(1)), this);

        headButton = new BuildItemButtonWidget(10, 40, 50, Text.literal(""), (b) -> {
            if (!hasShiftDown() && !hasControlDown()) controller.getItemFromDictionary("Helmet");
            else if (hasShiftDown() && hasControlDown()){
                String wikiFormatted = buildItems.get(2).name.replace(" ", "_").replace("'", "%27");
                Util.getOperatingSystem().open("https://monumenta.wiki.gg/wiki/" + wikiFormatted);
            }
        }, buildItems.get(2), () -> controller.itemGui.generateItemLoreText(buildItems.get(2)), this);

        chestplateButton = new BuildItemButtonWidget(10, 100, 50, Text.literal(""), (b) -> {
            if (!hasShiftDown() && !hasControlDown()) controller.getItemFromDictionary("Chestplate");
            else if (hasShiftDown() && hasControlDown()){
                String wikiFormatted = buildItems.get(3).name.replace(" ", "_").replace("'", "%27");
                Util.getOperatingSystem().open("https://monumenta.wiki.gg/wiki/" + wikiFormatted);
            }
        }, buildItems.get(3), () -> controller.itemGui.generateItemLoreText(buildItems.get(3)), this);

        leggingsButton = new BuildItemButtonWidget(10, 160, 50, Text.literal(""), (b) -> {
            if (!hasShiftDown() && !hasControlDown()) controller.getItemFromDictionary("Leggings");
            else if (hasShiftDown() && hasControlDown()){
                String wikiFormatted = buildItems.get(4).name.replace(" ", "_").replace("'", "%27");
                Util.getOperatingSystem().open("https://monumenta.wiki.gg/wiki/" + wikiFormatted);
            }
        }, buildItems.get(4), () -> controller.itemGui.generateItemLoreText(buildItems.get(4)), this);

        bootsButton = new BuildItemButtonWidget(10, 220, 50, Text.literal(""), (b) -> {
            if (!hasShiftDown() && !hasControlDown()) controller.getItemFromDictionary("Boots");
            else if (hasShiftDown() && hasControlDown()){
                String wikiFormatted = buildItems.get(5).name.replace(" ", "_").replace("'", "%27");
                Util.getOperatingSystem().open("https://monumenta.wiki.gg/wiki/" + wikiFormatted);
            }
        }, buildItems.get(5), () -> controller.itemGui.generateItemLoreText(buildItems.get(5)), this);

        buildCharmButtons.clear();
        if (charms.isEmpty()) {
            charmsButton = new BuildCharmButtonWidget(width/2, 220, 50, Text.literal(""), (b) -> {
                controller.getCharmFromDictionary();
            }, null,  null, this);
            buildCharmButtons.add(charmsButton);
        } else {
            for (int i = 0; i < charms.size(); i++) {
                DictionaryCharm charm = charms.get(i);
                charmsButton = new BuildCharmButtonWidget(width/2 + ((i%6)*60), 220 + ((i/6)*60), 50, Text.literal(""), (b) -> {
                     if (!Screen.hasShiftDown() && !Screen.hasControlDown()) {
                         do {
                             charms.remove(charm);
                         } while (charms.contains(charm));
                         controller.getCharmFromDictionary();
                     } else {
                         String wikiFormatted = charm.name.replace(" ", "_").replace("'", "%27");
                         Util.getOperatingSystem().open("https://monumenta.wiki.gg/wiki/" + wikiFormatted);
                     }
                }, charm,  () -> controller.charmGui.generateCharmLoreText(charm), this);
                buildCharmButtons.add(charmsButton);
            }
            if (charms.size() < 12) {
                charmsButton = new BuildCharmButtonWidget(width / 2 + ((charms.size() % 6) * 60), 220 + ((charms.size() / 6) * 60), 50, Text.literal(""), (b) -> {
                    controller.getCharmFromDictionary();
                }, null, null, this);
                buildCharmButtons.add(charmsButton);
            }
        }

        buildItemButtons.clear();
        buildItemButtons.add(mainhandButton);
        buildItemButtons.add(offhandButton);
        buildItemButtons.add(headButton);
        buildItemButtons.add(chestplateButton);
        buildItemButtons.add(leggingsButton);
        buildItemButtons.add(bootsButton);
    }

    private boolean verifyUrl(String buildUrl) {
        return buildUrl.contains("ohthemisery.tk/builder");
    }

    private void getBuildFromUrl(String buildUrl) {
        buildUrl = buildUrl.substring(buildUrl.indexOf("m="));
        DictionaryItem item;

        for (String rawItem : buildUrl.split("&")) {
            boolean isExalted = false;

            String itemName = String.join(" ", rawItem.substring(2).split("%20"));
            if (itemName.charAt(itemName.length()-2) == '-') {
                itemName = itemName.substring(0, itemName.length()-2);
            }

            if (itemName.contains("EX")) {
                itemName = itemName.replaceAll("EX ", "");
                isExalted = true;
            }

            String itemType = rawItem.substring(0, 1);

            item = controller.getItemByName(itemName, isExalted);
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

        for (String charm : rawCharms) {
            System.out.println(charm);
            DictionaryCharm charmToAdd = controller.getCharmByName(charm);
            if (charmToAdd != null) {
                System.out.println(charmToAdd.power);
                for (int i = 0; i < charmToAdd.power; i++) {
                    charms.add(charmToAdd);
                }
            }
        }

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

        nameBar.render(matrices, mouseX, mouseY, delta);
        addBuildButton.render(matrices, mouseX, mouseY, delta);
        setBuildFromClipboardButton.render(matrices, mouseX, mouseY, delta);
        showBuildDictionaryButton.render(matrices, mouseX, mouseY, delta);

        assert MinecraftClient.getInstance().player != null;
        int size = 130;
        int x = (width / 2) - 100;
        int y = size*2 + 20;
        int entityMouseX = -(mouseX - x);
        int entityMouseY = -(mouseY - y + size + 3*size/4);
        drawEntity(matrices, x, y, size, entityMouseX, entityMouseY, MinecraftClient.getInstance().player);

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
        for (BuildItemButtonWidget button : buildItemButtons) {
            drawTextWithShadow(matrices, textRenderer, Text.literal(itemTypesIndex.get(i)).setStyle(Style.EMPTY.withBold(true)), (i > 1 ? 70 : width/2 + 60), 40 + (i > 1 ? ((i-2)*60) : (i*60)), 0xFFFFFFFF);
            button.render(matrices, mouseX, mouseY, delta);
            i++;
        }

        for (BuildCharmButtonWidget button : buildCharmButtons) {
            button.render(matrices, mouseX, mouseY, delta);
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

        drawTextWithShadow(matrices, textRenderer, Text.literal("Charms").setStyle(Style.EMPTY.withBold(true)), width/2, 200, 0xFFFFFFFF);
        String stars = "★".repeat(charms.size()) + "☆".repeat(12 - charms.size());
        drawTextWithShadow(matrices, textRenderer, Text.literal(stars).setStyle(Style.EMPTY.withBold(true)), width/2 + 50, 200, 0xFFFFFF00);
        if (charms.size() == 12) {
            drawTextWithShadow(matrices, textRenderer, Text.literal("FULL CHARM POWER").setStyle(Style.EMPTY.withBold(true).withUnderline(true)), width/2 + 170, 200, 0xFFFF0000);
        }

    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        nameBar.mouseClicked(mouseX, mouseY, button);
        addBuildButton.mouseClicked(mouseX, mouseY, button);
        setBuildFromClipboardButton.mouseClicked(mouseX, mouseY, button);
        showBuildDictionaryButton.mouseClicked(mouseX, mouseY, button);

        buildItemButtons.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));
        buildCharmButtons.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);

        nameBar.keyPressed(keyCode, scanCode, modifiers);

        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);

        nameBar.charTyped(chr, modifiers);

        return true;
    }

    public void updateGuiPositions() {

    }

    public void loadItems(DictionaryBuild build) {
        for (int i = 0; i < build.allItems.size(); i++) {
            buildItems.set(i, build.allItems.get(i));
        }

        charms = build.charms;

        updateButtons();
    }

    private void resetBuild() {
        buildItems = Arrays.asList(null, null, null, null, null, null);
        charms = new ArrayList<>();
        updateButtons();
    }
}
