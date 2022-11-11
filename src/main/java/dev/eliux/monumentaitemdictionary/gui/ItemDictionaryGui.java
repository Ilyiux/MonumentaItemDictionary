package dev.eliux.monumentaitemdictionary.gui;

import dev.eliux.monumentaitemdictionary.util.ItemColors;
import dev.eliux.monumentaitemdictionary.util.ItemFormatter;
import dev.eliux.monumentaitemdictionary.util.ItemStat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ItemDictionaryGui extends Screen {
    private final int sortMenuWidth = 50;
    private final int labelMenuHeight = 30;
    private final int itemPadding = 7;
    private final int itemSize = 25;
    private int scrollPixels = 0;

    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    private ArrayList<ItemButtonWidget> itemButtons = new ArrayList<>();

    private TextFieldWidget searchBar;
    private ReloadButtonWidget reloadItemsButton;

    private final DictionaryController controller;

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

        reloadItemsButton = new ReloadButtonWidget(5, 5, 20, 20, new LiteralText(""), (button) -> {
            controller.requestItemsAndUpdate();
        }, (button, matrices, mouseX, mouseY) -> {
            renderTooltip(matrices, new LiteralText("Reload Item Data"), mouseX, mouseY);
        });
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        // draw the scroll bar
        int totalRows = (int) Math.ceil((double)itemButtons.size() / (double)((width - sortMenuWidth - 5) / (itemSize + itemPadding)));
        int totalPixelHeight = totalRows * itemSize + (totalRows + 1) * itemPadding;
        double bottomPercent = (double)scrollPixels / totalPixelHeight;
        double screenPercent = (double)(height - labelMenuHeight) / totalPixelHeight;
        drawVerticalLine(matrices, width - sortMenuWidth - 1, labelMenuHeight, height, 0x77AAAAAA); // called twice to make the scroll bar render wider (janky, but I don't really care)
        drawVerticalLine(matrices, width - sortMenuWidth - 2, labelMenuHeight, height, 0x77AAAAAA);
        drawVerticalLine(matrices, width - sortMenuWidth - 1, (int) (labelMenuHeight + (height - labelMenuHeight) * bottomPercent), (int) (labelMenuHeight + (height - labelMenuHeight) * (bottomPercent + screenPercent)), 0xFFC3C3C3);
        drawVerticalLine(matrices, width - sortMenuWidth - 2, (int) (labelMenuHeight + (height - labelMenuHeight) * bottomPercent), (int) (labelMenuHeight + (height - labelMenuHeight) * (bottomPercent + screenPercent)), 0xFFC3C3C3);

        // draw the sort menu
        drawVerticalLine(matrices, width - sortMenuWidth, labelMenuHeight, height, 0xFFFFFFFF);

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
            int row = index / ((width - sortMenuWidth - 5) / (itemSize + itemPadding));
            int col = index % ((width - sortMenuWidth - 5) / (itemSize + itemPadding));

            int x = (col + 1) * itemPadding + col * itemSize;
            int y = labelMenuHeight + (row + 1) * itemPadding + row * itemSize;

            ButtonWidget.TooltipSupplier tooltip = (button, matrices, mouseX, mouseY) -> {
                renderTooltip(matrices, generateItemLoreText(item), mouseX, mouseY);
            };
            ItemButtonWidget button = new ItemButtonWidget(x, y, itemSize, index, new LiteralText(item.name), i -> {}, item, tooltip, this);

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

        searchBar.mouseClicked(mouseX, mouseY, button);
        reloadItemsButton.mouseClicked(mouseX, mouseY, button);

        return true;
    }

    private List<Text> generateItemLoreText(DictionaryItem item) {
        List<Text> lines = new ArrayList<>();

        lines.add(new LiteralText(item.name).setStyle(Style.EMPTY
                .withColor(0xFF000000 + ItemColors.getColorForLocation(item.location))
                .withBold(ItemFormatter.shouldBold(item.tier))
                .withUnderline(ItemFormatter.shouldUnderline(item.tier))));

        if (!item.originalItem.equals(""))
            lines.add(new LiteralText("Skin for " + item.originalItem).setStyle(Style.EMPTY.withColor(ItemColors.TEXT_COLOR)));

        lines.add(new LiteralText(item.type + " - " + item.baseItem).setStyle(Style.EMPTY
                .withColor(ItemColors.TEXT_COLOR)));

        lines.add(new LiteralText(""));

        ArrayList<Text> enchants = new ArrayList<>();
        ArrayList<Text> basestats = new ArrayList<>();
        ArrayList<Text> stats = new ArrayList<>();

        for (ItemStat stat : item.stats) {
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

        lines.addAll(enchants);

        if (enchants.size() > 0) lines.add(new LiteralText(""));
        if (stats.size() > 0 || basestats.size() > 0) lines.add(new LiteralText("When Used:").setStyle(Style.EMPTY.withColor(0xAAAAAA)));

        lines.addAll(basestats);
        lines.addAll(stats);

        if (stats.size() > 0 || basestats.size() > 0) lines.add(new LiteralText(""));

        lines.add(new LiteralText(item.region + " ")
                .setStyle(Style.EMPTY.withColor(ItemColors.TEXT_COLOR))
                .append(new LiteralText(item.tier).setStyle(Style.EMPTY
                        .withColor(ItemColors.getColorForTier(item.tier))
                        .withBold(ItemFormatter.shouldUnderline(item.tier)))));

        lines.add(new LiteralText(item.location).setStyle(Style.EMPTY
                .withColor(ItemColors.getColorForLocation(item.location))));

        return lines;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);

        buildItemList();
        updateScrollLimits();

        searchBar.setX(width / 2 + 90);
        searchBar.setWidth(width / 2 - 100);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        super.mouseScrolled(mouseX, mouseY, amount);

        if (mouseX >= 0 && mouseX < width - sortMenuWidth && mouseY >= labelMenuHeight && mouseY < height) {
            scrollPixels += -amount * 12; // scaled

            updateScrollLimits();
        }

        return true;
    }

    private void updateScrollLimits() {
        int rows = (int) Math.ceil((double)itemButtons.size() / ((double)(width - sortMenuWidth - 5) / (itemSize + itemPadding)));
        int maxScroll = rows * (itemPadding + itemSize) + itemPadding - (height - labelMenuHeight);
        if (scrollPixels > maxScroll) scrollPixels = maxScroll;

        if (scrollPixels < 0) scrollPixels = 0;
    }

    public int getScrollPixels() {
        return scrollPixels;
    }
}
