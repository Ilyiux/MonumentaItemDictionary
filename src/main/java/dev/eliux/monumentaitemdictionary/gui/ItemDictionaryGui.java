package dev.eliux.monumentaitemdictionary.gui;

import dev.eliux.monumentaitemdictionary.gui.widgets.ItemButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.ItemIconButtonWidget;
import dev.eliux.monumentaitemdictionary.util.ItemColors;
import dev.eliux.monumentaitemdictionary.util.ItemFormatter;
import dev.eliux.monumentaitemdictionary.util.ItemStat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemDictionaryGui extends Screen {
    public final int sideMenuWidth = 40;
    public final int labelMenuHeight = 30;
    public final int itemPadding = 7;
    public final int itemSize = 25;
    private int scrollPixels = 0;

    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    private ArrayList<ItemButtonWidget> itemButtons = new ArrayList<>();

    private TextFieldWidget searchBar;
    private ItemIconButtonWidget reloadItemsButton;
    private ItemIconButtonWidget showItemsButton;
    private ItemIconButtonWidget showCharmsButton;
    private ItemIconButtonWidget filterButton;
    private ItemIconButtonWidget resetFilterButton;
    private ItemIconButtonWidget minMasterworkButton;
    private ItemIconButtonWidget maxMasterworkButton;
    private ItemIconButtonWidget tipsMasterworkButton;

    public final DictionaryController controller;

    public ItemDictionaryGui(Text title, DictionaryController controller) {
        super(title);
        this.controller = controller;
    }

    public void postInit() {
        buildItemList();

        searchBar = new TextFieldWidget(textRenderer, width / 2 + 90, 7, width / 2 - 100, 15, new LiteralText("Search"));
        searchBar.setChangedListener(t -> {
            controller.setNameFilter(searchBar.getText());
            if (searchBar.getText().equals(""))
                controller.clearNameFilter();

            buildItemList();
            updateScrollLimits();
        });

        reloadItemsButton = new ItemIconButtonWidget(5, 5, 20, 20, new LiteralText(""), (button) -> {
            controller.requestItemsAndUpdate();
        }, (button, matrices, mouseX, mouseY) -> {
            renderTooltip(matrices, new LiteralText("Reload Item Data"), mouseX, mouseY);
        }, "globe_banner_pattern", "");

        showItemsButton = new ItemIconButtonWidget(width - sideMenuWidth + 10, labelMenuHeight + 10, 20, 20, new LiteralText(""), (button) -> {
            // do nothing
        }, ((button, matrices, mouseX, mouseY) -> {
            renderTooltip(matrices, new LiteralText("Item Data").setStyle(Style.EMPTY.withColor(0xFF00FFFF)), mouseX, mouseY);
        }), "iron_chestplate", "");

        showCharmsButton = new ItemIconButtonWidget(width - sideMenuWidth + 10, labelMenuHeight + 40, 20, 20, new LiteralText(""), (button) -> {
            // do stuff
        }, (button, matrices, mouseX, mouseY) -> {
            renderTooltip(matrices, Arrays.asList(new LiteralText("Charm Data").setStyle(Style.EMPTY.withColor(0xFFFFFF00)), new LiteralText("Coming Soon").setStyle(Style.EMPTY.withColor(0xFFAAAAAA))), mouseX, mouseY);
        }, "glowstone_dust", "");

        filterButton = new ItemIconButtonWidget(width - sideMenuWidth + 10, height - 30, 20, 20, new LiteralText(""), (button) -> {
            controller.setFilterScreen();
        }, (button, matrices, mouseX, mouseY) -> {
            renderTooltip(matrices, new LiteralText("Filter"), mouseX, mouseY);
        }, "chest", "");

        resetFilterButton = new ItemIconButtonWidget(width - sideMenuWidth + 10, height - 60, 20, 20, new LiteralText(""), (button) -> {
            controller.filterGui.clearFilters();
            buildItemList();
        }, (button, matrices, mouseX, mouseY) -> {
            renderTooltip(matrices, new LiteralText("Reset Filters").setStyle(Style.EMPTY.withColor(0xFFFF0000)), mouseX, mouseY);
        }, "barrier", "");

        minMasterworkButton = new ItemIconButtonWidget(width - sideMenuWidth + 10, height - 120, 20, 20, new LiteralText(""), (button) -> {
            itemButtons.forEach(ItemButtonWidget::setMinimumMasterwork);
        }, (button, matrices, mouseX, mouseY) -> {
            renderTooltip(matrices, new LiteralText("Show Minimum Masterwork").setStyle(Style.EMPTY.withColor(0xFFAA00AA)), mouseX, mouseY);
        }, "netherite_scrap", "");

        maxMasterworkButton = new ItemIconButtonWidget(width - sideMenuWidth + 10, height - 90, 20, 20, new LiteralText(""), (button) -> {
            itemButtons.forEach(ItemButtonWidget::setMaximumMasterwork);
        }, (button, matrices, mouseX, mouseY) -> {
            renderTooltip(matrices, Arrays.asList(new LiteralText("Show Maximum Masterwork").setStyle(Style.EMPTY.withColor(0xFFAA00AA)), new LiteralText("(Only counts tiers with data)").setStyle(Style.EMPTY.withColor(0xFFAAAAAA))), mouseX, mouseY);
        }, "netherite_ingot", "");

        tipsMasterworkButton = new ItemIconButtonWidget(30, 5, 20, 20, new LiteralText(""), (button) -> {
            Util.getOperatingSystem().open("https://github.com/Ilyiux/MonumentaItemDictionary");
        }, (button, matrices, mouseX, mouseY) -> {
            renderTooltip(matrices, Arrays.asList(
                    new LiteralText("Tips").setStyle(Style.EMPTY.withColor(0xFFFFFFFF)),
                    new LiteralText(""),
                    new LiteralText("Ctrl + Scroll").setStyle(Style.EMPTY.withItalic(true).withColor(ItemColors.TEXT_COLOR)).append(new LiteralText(" to increase/decrease individual masterwork tiers").setStyle(Style.EMPTY.withItalic(false).withColor(ItemColors.TEXT_COLOR))),
                    new LiteralText(""),
                    new LiteralText("Click to go to the MID Github page!").setStyle(Style.EMPTY.withUnderline(true).withColor(0xFF5555FF))
            ), mouseX, mouseY);
        }, "oak_sign", "");
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        // draw the scroll bar
        int totalRows = (int) Math.ceil((double)itemButtons.size() / (double)((width - sideMenuWidth - 5) / (itemSize + itemPadding)));
        int totalPixelHeight = totalRows * itemSize + (totalRows + 1) * itemPadding;
        double bottomPercent = (double)scrollPixels / totalPixelHeight;
        double screenPercent = (double)(height - labelMenuHeight) / totalPixelHeight;
        drawVerticalLine(matrices, width - sideMenuWidth - 1, labelMenuHeight, height, 0x77AAAAAA); // called twice to make the scroll bar render wider (janky, but I don't really care)
        drawVerticalLine(matrices, width - sideMenuWidth - 2, labelMenuHeight, height, 0x77AAAAAA);
        drawVerticalLine(matrices, width - sideMenuWidth - 1, (int) (labelMenuHeight + (height - labelMenuHeight) * bottomPercent), (int) (labelMenuHeight + (height - labelMenuHeight) * (bottomPercent + screenPercent)), 0xFFC3C3C3);
        drawVerticalLine(matrices, width - sideMenuWidth - 2, (int) (labelMenuHeight + (height - labelMenuHeight) * bottomPercent), (int) (labelMenuHeight + (height - labelMenuHeight) * (bottomPercent + screenPercent)), 0xFFC3C3C3);

        // draw the sort menu
        drawVerticalLine(matrices, width - sideMenuWidth, labelMenuHeight, height, 0xFFFFFFFF);

        // draw item buttons
        itemButtons.forEach(b -> {
            if (b.y - scrollPixels + itemSize >= labelMenuHeight && b.y - scrollPixels <= height) {
                b.renderButton(matrices, mouseX, mouseY, delta);
            }
        });

        if (itemButtons.size() == 0) {
            drawCenteredText(matrices, textRenderer, "Found No Items", width / 2, labelMenuHeight + 10, 0xFF2222);
        }

        // draw the label at the top
        matrices.push();
        matrices.translate(0, 0, 110);
        fill(matrices, 0, 0, width, labelMenuHeight, 0xFF555555);
        drawHorizontalLine(matrices, 0, width, labelMenuHeight, 0xFFFFFFFF);
        drawCenteredText(matrices, textRenderer, new LiteralText("Monumenta Item Dictionary").setStyle(Style.EMPTY.withBold(true)), width / 2, (labelMenuHeight - textRenderer.fontHeight) / 2, 0xFFFFAA00);
        matrices.pop();

        // draw gui elements
        matrices.push();
        matrices.translate(0, 0, 110);
        searchBar.render(matrices, mouseX, mouseY, delta);
        reloadItemsButton.render(matrices, mouseX, mouseY, delta);
        showItemsButton.render(matrices, mouseX, mouseY, delta);
        showCharmsButton.render(matrices, mouseX, mouseY, delta);
        filterButton.render(matrices, mouseX, mouseY, delta);
        resetFilterButton.render(matrices, mouseX, mouseY, delta);
        minMasterworkButton.render(matrices, mouseX, mouseY, delta);
        maxMasterworkButton.render(matrices, mouseX, mouseY, delta);
        tipsMasterworkButton.render(matrices, mouseX, mouseY, delta);
        matrices.pop();

        try {
            super.render(matrices, mouseX, mouseY, delta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildItemList() {
        controller.refreshItems();
        ArrayList<DictionaryItem> toBuildItems = controller.getItems();

        itemButtons.clear();
        for (DictionaryItem item : toBuildItems) {
            int index = toBuildItems.indexOf(item);
            int row = index / ((width - sideMenuWidth - 5) / (itemSize + itemPadding));
            int col = index % ((width - sideMenuWidth - 5) / (itemSize + itemPadding));

            int x = (col + 1) * itemPadding + col * itemSize;
            int y = labelMenuHeight + (row + 1) * itemPadding + row * itemSize;

            ItemButtonWidget button = new ItemButtonWidget(x, y, itemSize, index, new LiteralText(item.name), (b) -> {

            }, item, (b, matrices, mouseX, mouseY) -> {
                renderTooltip(matrices, generateItemLoreText(item), mouseX, mouseY);
            }, this);

            itemButtons.add(button);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);

        searchBar.keyPressed(keyCode, scanCode, modifiers);

        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);

        searchBar.charTyped(chr, modifiers);

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        itemButtons.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));

        searchBar.mouseClicked(mouseX, mouseY, button);
        reloadItemsButton.mouseClicked(mouseX, mouseY, button);
        showItemsButton.mouseClicked(mouseX, mouseY, button);
        showCharmsButton.mouseClicked(mouseX, mouseY, button);
        filterButton.mouseClicked(mouseX, mouseY, button);
        resetFilterButton.mouseClicked(mouseX, mouseY, button);
        minMasterworkButton.mouseClicked(mouseX, mouseY, button);
        maxMasterworkButton.mouseClicked(mouseX, mouseY, button);
        tipsMasterworkButton.mouseClicked(mouseX, mouseY, button);

        return true;
    }

    private List<Text> generateItemLoreText(DictionaryItem item) {
        // this is scuffed
        int masterworkTier = 0;
        for (ItemButtonWidget itemButton : itemButtons) {
            if (itemButton.isItem(item)) {
                masterworkTier = itemButton.shownMasterworkTier;
            }
        }

        List<Text> lines = new ArrayList<>();

        lines.add(new LiteralText(item.name).setStyle(Style.EMPTY
                .withColor(0xFF000000 + ItemColors.getColorForLocation(item.location))
                .withBold(ItemFormatter.shouldBold(item.tier))
                .withUnderline(ItemFormatter.shouldUnderline(item.tier))));

        ArrayList<Text> enchants = new ArrayList<>();
        ArrayList<Text> basestats = new ArrayList<>();
        ArrayList<Text> stats = new ArrayList<>();

        ArrayList<ItemStat> showStats;
        if (item.hasMasterwork) {
            showStats = item.getMasterworkTier(masterworkTier);
        } else {
            showStats = item.getNonMasterwork();
        }

        if (showStats == null) {
            if (masterworkTier > item.getMinMasterwork()) {
                lines.add(new LiteralText("The data for this tier is missing.").setStyle(Style.EMPTY.withColor(0xFFFF0000)));
            } else if (masterworkTier < item.getMinMasterwork()) {
                lines.add(new LiteralText("This masterwork tier does not exist.").setStyle(Style.EMPTY.withColor(0xFFFF0000)));
            }
        }

        if (showStats != null) {
            for (ItemStat stat : showStats) {
                Text line = new LiteralText(ItemFormatter.buildStatString(stat.statName, stat.statValue)).setStyle(Style.EMPTY
                        .withColor(ItemColors.getColorForStat(stat.statName, stat.statValue)));
                if (ItemFormatter.isStat(stat.statName)) {
                    if (ItemFormatter.isBaseStat(stat.statName)) {
                        basestats.add(line);
                    } else {
                        stats.add(line);
                    }
                } else {
                    enchants.add(line);
                }
            }
        }

        if (showStats != null) {
            lines.addAll(enchants);
        }

        if (item.hasRegion || item.hasTier) {
            MutableText regionText = new LiteralText(item.hasRegion ? ItemFormatter.formatRegion(item.region) + " : " : "")
                    .setStyle(Style.EMPTY.withColor(ItemColors.TEXT_COLOR));
            MutableText tierText = new LiteralText(item.hasTier ? item.tier : "").setStyle(Style.EMPTY
                    .withColor(ItemColors.getColorForTier(item.tier))
                    .withBold(ItemFormatter.shouldUnderline(item.tier)));

            lines.add(regionText.append(tierText));
        }

        if (item.hasMasterwork) {
            MutableText baseText = new LiteralText("Masterwork: ").setStyle(Style.EMPTY.withColor(ItemColors.TEXT_COLOR));
            for (int i = 0; i < ItemFormatter.getMasterworkForRarity(item.tier); i ++) {
                if (i < masterworkTier) {
                    baseText.append(new LiteralText("★").setStyle(Style.EMPTY.withColor(ItemColors.TEXT_MASTERWORK_COLOR)));
                } else {
                    baseText.append(new LiteralText("☆").setStyle(Style.EMPTY.withColor(ItemColors.TEXT_COLOR)));
                }
            }
            lines.add(baseText);
        }

        if (item.hasLocation) {
            lines.add(new LiteralText(item.location).setStyle(Style.EMPTY
                    .withColor(ItemColors.getColorForLocation(item.location))));
        }

        if (!item.lore.equals("")) {
            if (hasShiftDown()) {
                for (String line : item.lore.split("\n")) {
                    lines.add(new LiteralText(line).setStyle(Style.EMPTY.withColor(ItemColors.TEXT_LORE_COLOR)));
                }
            } else {
                lines.add(new LiteralText("Press [SHIFT] to show lore.").setStyle(Style.EMPTY.withColor(ItemColors.TEXT_COLOR)));
            }
        }

        if (showStats != null) {
            if (stats.size() > 0 || basestats.size() > 0) {
                lines.add(new LiteralText(""));
                lines.add(new LiteralText("When Used:").setStyle(Style.EMPTY.withColor(0xAAAAAA)));
            }

            lines.addAll(basestats);
            lines.addAll(stats);
        }

        lines.add(new LiteralText(""));

        lines.add(new LiteralText(item.type + " - " + item.baseItem).setStyle(Style.EMPTY
                .withColor(ItemColors.TEXT_COLOR)));

        return lines;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);

        updateGuiPositions();
    }

    public void updateGuiPositions() {
        buildItemList();
        updateScrollLimits();

        searchBar.setX(width / 2 + 90);
        searchBar.setWidth(width / 2 - 100);

        showItemsButton.x = width - sideMenuWidth + 10;
        showCharmsButton.x = width - sideMenuWidth + 10;
        showItemsButton.y = labelMenuHeight + 10;
        showCharmsButton.y = labelMenuHeight + 40;

        filterButton.x = width - sideMenuWidth + 10;
        filterButton.y = height - 30;
        resetFilterButton.x = width - sideMenuWidth + 10;
        resetFilterButton.y = height - 60;

        minMasterworkButton.x = width - sideMenuWidth + 10;
        minMasterworkButton.y = height - 120;
        maxMasterworkButton.x = width - sideMenuWidth + 10;
        maxMasterworkButton.y = height - 90;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        super.mouseScrolled(mouseX, mouseY, amount);

        if (Screen.hasControlDown()) {
            itemButtons.forEach((b) -> b.scrolled(mouseX, mouseY, amount));
        } else {
            if (mouseX >= 0 && mouseX < width - sideMenuWidth && mouseY >= labelMenuHeight && mouseY < height) {
                scrollPixels += -amount * 12; // scaled

                updateScrollLimits();
            }
        }

        return true;
    }

    private void updateScrollLimits() {
        int rows = (int) Math.ceil((double)itemButtons.size() / (double)((width - sideMenuWidth - 5) / (itemSize + itemPadding)));
        int maxScroll = rows * itemSize + (rows + 1) * itemPadding - height + labelMenuHeight;
        if (scrollPixels > maxScroll) scrollPixels = maxScroll;

        if (scrollPixels < 0) scrollPixels = 0;
    }

    public int getScrollPixels() {
        return scrollPixels;
    }
}
