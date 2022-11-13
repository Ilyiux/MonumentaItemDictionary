package dev.eliux.monumentaitemdictionary.gui;

import dev.eliux.monumentaitemdictionary.util.ItemColors;
import dev.eliux.monumentaitemdictionary.util.ItemFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class ItemSortMenuGui extends Screen {
    private final int labelMenuHeight = 30;

    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    private ItemIconButtonWidget backButton;

    private ArrayList<SortButtonWidget> typeButtons;
    private int typeScroll;
    private ArrayList<SortButtonWidget> regionButtons;
    private int regionScroll;
    private ArrayList<SortButtonWidget> tierButtons;
    private int tierScroll;
    private ArrayList<SortButtonWidget> locationButtons;
    private int locationScroll;
    private ArrayList<SortButtonWidget> statButtons;
    private int statScroll;

    public final DictionaryController controller;

    public boolean initialized = false;

    public ItemSortMenuGui(Text title, DictionaryController controller) {
        super(title);

        this.controller = controller;
    }

    public void postInit() {
        initialized = true;

        backButton = new ItemIconButtonWidget(5, 5, 20, 20, new LiteralText(""), (button) -> {
            controller.setDictionaryScreen();
        }, (button, matrices, mouseX, mouseY) -> {
            renderTooltip(matrices, new LiteralText("Go Back"), mouseX, mouseY);
        }, "arrow");

        typeButtons = new ArrayList<>();
        regionButtons = new ArrayList<>();
        tierButtons = new ArrayList<>();
        locationButtons = new ArrayList<>();
        statButtons = new ArrayList<>();

        for (String type : controller.getAllTypes()) {
            typeButtons.add(new SortButtonWidget(5, labelMenuHeight + 5 + 25 * typeButtons.size(), 20, 20, new LiteralText(type), (button) -> {

            }, false, SortType.TYPE, type,this));
        }
        for (String region : controller.getAllRegions()) {
            regionButtons.add(new SortButtonWidget(width / 5 + 5, labelMenuHeight + 5 + 25 * regionButtons.size(), 20, 20, new LiteralText(region), (button) -> {

            }, false, SortType.REGION, region,this));
        }
        for (String tier : controller.getAllTiers()) {
            tierButtons.add(new SortButtonWidget(width / 5 * 2 + 5, labelMenuHeight + 5 + 25 * tierButtons.size(), 20, 20, new LiteralText(tier).setStyle(Style.EMPTY.withColor(ItemColors.getColorForTier(tier))), (button) -> {

            }, false, SortType.TIER, tier, this));
        }
        for (String location : controller.getAllLocations()) {
            locationButtons.add(new SortButtonWidget(width / 5 * 3 + 5, labelMenuHeight + 5 + 25 * locationButtons.size(), 20, 20, new LiteralText(location).setStyle(Style.EMPTY.withColor(ItemColors.getColorForLocation(location))), (button) -> {

            }, false, SortType.LOCATION, location, this));
        }

        ArrayList<String> baseStats = new ArrayList<>();
        ArrayList<String> stats = new ArrayList<>();
        ArrayList<String> enchants = new ArrayList<>();
        ArrayList<String> curses = new ArrayList<>();
        for (String stat : controller.getAllStats()) {
            if (ItemFormatter.isStat(stat)) {
                if (ItemFormatter.isBaseStat(stat)) {
                    baseStats.add(stat);
                } else {
                    stats.add(stat);
                }
            } else {
                if (ItemFormatter.isCurseEnchant(stat)) {
                    curses.add(stat);
                } else {
                    enchants.add(stat);
                }
            }
        }
        baseStats.sort(String::compareToIgnoreCase);
        stats.sort(String::compareToIgnoreCase);
        enchants.sort(String::compareToIgnoreCase);
        curses.sort(String::compareToIgnoreCase);
        ArrayList<String> allStats = new ArrayList<>();
        allStats.addAll(baseStats);
        allStats.addAll(stats);
        allStats.addAll(enchants);
        allStats.addAll(curses);
        for (String stat : allStats) {
            statButtons.add(new SortButtonWidget(width / 5 * 4 + 5, labelMenuHeight + 5 + 25 * statButtons.size(), 20, 20, new LiteralText(ItemFormatter.formatStat(stat).trim()).setStyle(Style.EMPTY.withColor(ItemColors.getColorForStat(stat, 0))), (button) -> {

            }, false, SortType.STAT, stat, this));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        // draw dividing lines
        drawVerticalLine(matrices, width / 5, labelMenuHeight, height, 0xFFFFFFFF);
        drawVerticalLine(matrices, width / 5 * 2, labelMenuHeight, height, 0xFFFFFFFF);
        drawVerticalLine(matrices, width / 5 * 3, labelMenuHeight, height, 0xFFFFFFFF);
        drawVerticalLine(matrices, width / 5 * 4, labelMenuHeight, height, 0xFFFFFFFF);

        // draw scroll indicators
        fill(matrices, width / 5 - 2, labelMenuHeight, width / 5, height, 0x77AAAAAA);
        fill(matrices, width / 5 * 2 - 2, labelMenuHeight, width / 5 * 2, height, 0x77AAAAAA);
        fill(matrices, width / 5 * 3 - 2, labelMenuHeight, width / 5 * 3, height, 0x77AAAAAA);
        fill(matrices, width / 5 * 4 - 2, labelMenuHeight, width / 5 * 4, height, 0x77AAAAAA);
        fill(matrices, width - 2, labelMenuHeight, width, height, 0x77AAAAAA);

        int typeTotalPixelHeight = typeButtons.size() * 20 + (typeButtons.size() + 1) * 5;
        double typeBottomPercent = (double)getSortScrollOffset(SortType.TYPE) / typeTotalPixelHeight;
        double typeScreenPercent = (double)(height - labelMenuHeight) / typeTotalPixelHeight;
        fill(matrices, width / 5 - 2, (int) (labelMenuHeight + (height - labelMenuHeight) * typeBottomPercent), width / 5, (int) (labelMenuHeight + (height - labelMenuHeight) * (typeBottomPercent + typeScreenPercent)), 0xFFC3C3C3);
        int regionTotalPixelHeight = regionButtons.size() * 20 + (regionButtons.size() + 1) * 5;
        double regionBottomPercent = (double)getSortScrollOffset(SortType.REGION) / regionTotalPixelHeight;
        double regionScreenPercent = (double)(height - labelMenuHeight) / regionTotalPixelHeight;
        fill(matrices, width / 5 * 2 - 2, (int) (labelMenuHeight + (height - labelMenuHeight) * regionBottomPercent), width / 5 * 2, (int) (labelMenuHeight + (height - labelMenuHeight) * (regionBottomPercent + regionScreenPercent)), 0xFFC3C3C3);
        int tierTotalPixelHeight = tierButtons.size() * 20 + (tierButtons.size() + 1) * 5;
        double tierBottomPercent = (double)getSortScrollOffset(SortType.TIER) / tierTotalPixelHeight;
        double tierScreenPercent = (double)(height - labelMenuHeight) / tierTotalPixelHeight;
        fill(matrices, width / 5 * 3 - 2, (int) (labelMenuHeight + (height - labelMenuHeight) * tierBottomPercent), width / 5 * 3, (int) (labelMenuHeight + (height - labelMenuHeight) * (tierBottomPercent + tierScreenPercent)), 0xFFC3C3C3);
        int locationTotalPixelHeight = locationButtons.size() * 20 + (locationButtons.size() + 1) * 5;
        double locationBottomPercent = (double)getSortScrollOffset(SortType.LOCATION) / locationTotalPixelHeight;
        double locationScreenPercent = (double)(height - labelMenuHeight) / locationTotalPixelHeight;
        fill(matrices, width / 5 * 4 - 2, (int) (labelMenuHeight + (height - labelMenuHeight) * locationBottomPercent), width / 5 * 4, (int) (labelMenuHeight + (height - labelMenuHeight) * (locationBottomPercent + locationScreenPercent)), 0xFFC3C3C3);
        int statTotalPixelHeight = statButtons.size() * 20 + (statButtons.size() + 1) * 5;
        double statBottomPercent = (double)getSortScrollOffset(SortType.STAT) / statTotalPixelHeight;
        double statScreenPercent = (double)(height - labelMenuHeight) / statTotalPixelHeight;
        fill(matrices, width - 2, (int) (labelMenuHeight + (height - labelMenuHeight) * statBottomPercent), width, (int) (labelMenuHeight + (height - labelMenuHeight) * (statBottomPercent + statScreenPercent)), 0xFFC3C3C3);

        // draw buttons
        typeButtons.forEach((b) -> {
            b.renderButton(matrices, mouseX, mouseY, delta);
        });
        regionButtons.forEach((b) -> {
            b.renderButton(matrices, mouseX, mouseY, delta);
        });
        tierButtons.forEach((b) -> {
            b.renderButton(matrices, mouseX, mouseY, delta);
        });
        locationButtons.forEach((b) -> {
            b.renderButton(matrices, mouseX, mouseY, delta);
        });
        statButtons.forEach((b) -> {
            b.renderButton(matrices, mouseX, mouseY, delta);
        });

        // draw the label at the top
        matrices.push();
        matrices.translate(0, 0, 110);
        fill(matrices, 0, 0, width, labelMenuHeight, 0xFF555555);
        drawHorizontalLine(matrices, 0, width, labelMenuHeight, 0xFFFFFFFF);
        drawCenteredText(matrices, textRenderer, new LiteralText("Sort Menu").setStyle(Style.EMPTY.withBold(true)), width / 2, (labelMenuHeight - textRenderer.fontHeight) / 2, 0xFFFFAA00);
        matrices.pop();

        // draw gui elements
        matrices.push();
        matrices.translate(0, 0, 110);
        backButton.render(matrices, mouseX, mouseY, delta);
        matrices.pop();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);

        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        backButton.mouseClicked(mouseX, mouseY, button);
        typeButtons.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));
        regionButtons.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));
        tierButtons.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));
        locationButtons.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));
        statButtons.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));

        return true;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);

        updateGuiPositions();
        updateScrollLimits();
    }

    public void updateGuiPositions() {
        typeButtons.forEach((b) -> b.x = 5);
        regionButtons.forEach((b) -> b.x = width / 5 + 5);
        tierButtons.forEach((b) -> b.x = width / 5 * 2 + 5);
        locationButtons.forEach((b) -> b.x = width / 5 * 3 + 5);
        statButtons.forEach((b) -> b.x = width / 5 * 4 + 5);
    }

    public void resetButtons() {
        typeButtons.forEach((b) -> b.enabled = false);
        regionButtons.forEach((b) -> b.enabled = false);
        tierButtons.forEach((b) -> b.enabled = false);
        locationButtons.forEach((b) -> b.enabled = false);
        statButtons.forEach((b) -> b.enabled = false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        super.mouseScrolled(mouseX, mouseY, amount);

        if (mouseY > labelMenuHeight) {
            if (mouseX > 0 && mouseX < width / 5) {
                typeScroll += -amount * 12;
            } else if (mouseX > width / 5 && mouseX < width / 5 * 2) {
                regionScroll += -amount * 12;
            } else if (mouseX > width / 5 * 2 && mouseX < width / 5 * 3) {
                tierScroll += -amount * 12;
            } else if (mouseX > width / 5 * 3 && mouseX < width / 5 * 4) {
                locationScroll += -amount * 12;
            } else if (mouseX > width / 5 * 4 && mouseX < width) {
                statScroll += -amount * 12;
            }
        }
        updateScrollLimits();

        return true;
    }

    private void updateScrollLimits() {
        int typeMax = typeButtons.size() * 25 + labelMenuHeight - height + 5;
        if (typeScroll > typeMax) typeScroll = typeMax;
        if (typeScroll < 0) typeScroll = 0;

        int regionMax = regionButtons.size() * 25 + labelMenuHeight - height + 5;
        if (regionScroll > regionMax) regionScroll = regionMax;
        if (regionScroll < 0) regionScroll = 0;

        int tierMax = tierButtons.size() * 25 + labelMenuHeight - height + 5;
        if (tierScroll > tierMax) tierScroll = tierMax;
        if (tierScroll < 0) tierScroll = 0;

        int locationMax = locationButtons.size() * 25 + labelMenuHeight - height + 5;
        if (locationScroll > locationMax) locationScroll = locationMax;
        if (locationScroll < 0) locationScroll = 0;

        int statMax = statButtons.size() * 25 + labelMenuHeight - height + 5;
        if (statScroll > statMax) statScroll = statMax;
        if (statScroll < 0) statScroll = 0;
    }

    public int getSortScrollOffset(SortType sortType) {
        return switch (sortType) {
            case TYPE -> typeScroll;
            case REGION -> regionScroll;
            case TIER -> tierScroll;
            case LOCATION -> locationScroll;
            case STAT -> statScroll;
        };
    }

    public enum SortType {
        TYPE, REGION, TIER, LOCATION, STAT
    }
}
