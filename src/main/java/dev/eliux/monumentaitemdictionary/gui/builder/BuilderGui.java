package dev.eliux.monumentaitemdictionary.gui.builder;

import dev.eliux.monumentaitemdictionary.gui.DictionaryController;
import dev.eliux.monumentaitemdictionary.gui.widgets.BuildButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.ItemIconButtonWidget;
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

public class BuilderGui  extends Screen {
    private ItemIconButtonWidget addBuildFromClipboardButton;
    private ItemIconButtonWidget showItemsButton;
    private ItemIconButtonWidget showCharmsButton;
    public final int sideMenuWidth = 40;
    public final int labelMenuHeight = 30;
    public final int itemPadding = 10;
    public final int itemSize = 50;
    public final DictionaryController controller;
    public ArrayList<DictionaryBuild> buildsList;
    public HashMap<DictionaryBuild, BuildButtonWidget> buildsButtons = new HashMap<>();
    public BuilderGui(Text title, DictionaryController controller) {
        super(title);
        this.controller = controller;
    }

    public void postInit() {
        buildsList = new ArrayList<>();

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

        showItemsButton = new ItemIconButtonWidget(width - sideMenuWidth + 10, labelMenuHeight + 10, 20, 20, Text.literal(""), (button) -> {
            controller.setItemDictionaryScreen();
        }, Text.literal("Item Data").setStyle(Style.EMPTY.withColor(0xFF00FFFF)), "iron_chestplate", "");

        showCharmsButton = new ItemIconButtonWidget(width - sideMenuWidth + 10, labelMenuHeight + 30, 20, 20, Text.literal(""), (button) -> {
            controller.setCharmDictionaryScreen();
        }, Text.literal("Charm Data").setStyle(Style.EMPTY.withColor(0xFFFFFF00)), "glowstone_dust", "");
    }

    private void getBuildFromUrl(String buildUrl) {
        buildUrl = buildUrl.substring(buildUrl.indexOf("m="));
        HashMap<String, String> buildItems = new HashMap<>();

        for (String item : buildUrl.split("&")) {
            String itemName = String.join(" ", item.substring(2).split("%20"));
            String itemType = item.substring(0, 1);

            switch (itemType) {
                case "m": buildItems.put("mainhand", itemName); break;
                case "o": buildItems.put("offhand", itemName); break;
                case "h": buildItems.put("head", itemName); break;
                case "c": buildItems.put("chestplate", itemName); break;
                case "l": buildItems.put("leggings", itemName); break;
                case "b": buildItems.put("boots", itemName); break;
                default:
                    break;
            }
        }

        String[] charms = buildUrl.substring(buildUrl.indexOf("charm=") + 6).split(",");
        DictionaryBuild build = new DictionaryBuild(
                "Build",
                buildItems.get("mainhand"),
                buildItems.get("offhand"),
                buildItems.get("head"),
                buildItems.get("chestplate"),
                buildItems.get("leggings"),
                buildItems.get("boots"),
                charms
        );
        addBuild(build);
    }

    private void addBuild(DictionaryBuild build) {
        buildsList.add(build);
        buildBuildsList();
    }

    private boolean verifyUrl(String buildUrl) {
        return buildUrl.contains("ohthemisery.tk/builder");
    }

    public void buildBuildsList() {
        for (DictionaryBuild build : buildsList) {
            int index = buildsList.indexOf(build);
            int row = index / ((width - sideMenuWidth - 5) / (itemSize + itemPadding));
            int col = index % ((width - sideMenuWidth - 5) / (itemSize + itemPadding));

            int x = (col + 1) * itemPadding + col * itemSize;
            int y = labelMenuHeight + (row + 1) * itemPadding + row * itemSize;

            BuildButtonWidget button = new BuildButtonWidget(x, y, itemSize, Text.literal(build.name), (b) -> {
                System.out.println(build.chestplate);
            }, build, this);

            buildsButtons.put(build, button);
        }
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

        buildsButtons.forEach((build, button) -> {
            button.renderButton(matrices, mouseX, mouseY, delta);
        });

        if (buildsButtons.isEmpty()) {
            drawCenteredTextWithShadow(matrices, textRenderer, "Found No Builds", width / 2, labelMenuHeight + 10, 0xFF2222);
        }

        matrices.push();
        matrices.translate(0, 0, 110);
        addBuildFromClipboardButton.render(matrices, mouseX, mouseY, delta);
        showCharmsButton.render(matrices, mouseX, mouseY, delta);
        showItemsButton.render(matrices, mouseX, mouseY, delta);
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
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        buildsButtons.forEach((build, b) -> {
            b.mouseClicked(mouseX, mouseY, button);
        });

        addBuildFromClipboardButton.mouseClicked(mouseX, mouseY, button);
        showCharmsButton.mouseClicked(mouseX, mouseY, button);
        showItemsButton.mouseClicked(mouseX, mouseY, button);

        return true;
    }

    public void updateGuiPositions() {
    }
}
