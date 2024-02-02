package dev.eliux.monumentaitemdictionary.gui.builder;

import dev.eliux.monumentaitemdictionary.gui.DictionaryController;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuilderGui  extends Screen {
    private ItemIconButtonWidget addBuildFromClipboardButton;
    public final int labelMenuHeight = 30;
    public final DictionaryController controller;
    public ArrayList<DictionaryBuild> buildsList;
    public BuilderGui(Text title, DictionaryController controller) {
        super(title);
        this.controller = controller;
    }

    public void postInit() {
        System.setProperty("java.awt.headless", "false");
        buildsList = new ArrayList<>();

        addBuildFromClipboardButton = new ItemIconButtonWidget(5, 50, 20, 20, Text.literal(""), (button) -> {
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
    }

    private void getBuildFromUrl(String buildUrl) {
        buildUrl = buildUrl.substring(buildUrl.indexOf("m="));

        Map<String, String> buildItems = new HashMap<>(Map.of());

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

            System.out.println(itemName);

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

        for (String charm : charms) {
            System.out.println(charm);
        }

        addBuild(build);
    }

    private void addBuild(DictionaryBuild build) {
        buildsList.add(build);
    }

    private boolean verifyUrl(String buildUrl) {
        return buildUrl.contains("ohthemisery.tk/builder");
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

        matrices.push();
        matrices.translate(0, 0, 110);
        addBuildFromClipboardButton.render(matrices, mouseX, mouseY, delta);
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

        addBuildFromClipboardButton.mouseClicked(mouseX, mouseY, button);

        return true;
    }

    public void updateGuiPositions() {
    }
}
