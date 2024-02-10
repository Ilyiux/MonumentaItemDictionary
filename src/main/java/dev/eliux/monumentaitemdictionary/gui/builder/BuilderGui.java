package dev.eliux.monumentaitemdictionary.gui.builder;

import dev.eliux.monumentaitemdictionary.gui.DictionaryController;
import dev.eliux.monumentaitemdictionary.gui.charm.DictionaryCharm;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;
import dev.eliux.monumentaitemdictionary.gui.widgets.BuildCharmButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.BuildItemButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.ItemIconButtonWidget;
import dev.eliux.monumentaitemdictionary.util.ItemColors;
import dev.eliux.monumentaitemdictionary.util.Percentage;
import dev.eliux.monumentaitemdictionary.util.Stats;
import dev.eliux.monumentaitemdictionary.util.StatsFormats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
    private Stats buildStats;
    private List<String> statsToRender = new ArrayList<>();
    private List<BuildItemButtonWidget> buildItemButtons = new ArrayList<>();
    private final List<BuildCharmButtonWidget> buildCharmButtons = new ArrayList<>();
    public final int sideMenuWidth = 40;
    public final int labelMenuHeight = 30;
    public final int itemPadding = 5;
    public final int buttonSize = 50;
    public final int charmsY = 220;
    private int halfWidth = 0;
    private TextFieldWidget nameBar;
    private ItemIconButtonWidget showBuildDictionaryButton;
    private final DictionaryController controller;
    private ItemIconButtonWidget setBuildFromClipboardButton;
    private ItemIconButtonWidget addBuildButton;
    private HashMap<String, Boolean> situationals;
    private HashMap<String, Integer> infusions;
    private double currentHealthPercent;
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

        this.situationals = new HashMap<String, Boolean>() {{
            put("shielding", false);
            put("poise", false);
            put("inure", false);
            put("steadfast", false);
            put("guard", false);
            put("ethereal", false);
            put("reflexes", false);
            put("evasion", false);
            put("tempo", false);
            put("cloaked", false);
            put("adaptability", false);
            put("second_wind", false);
            put("versatile", false);
        }};
        this.infusions = new HashMap<String, Integer>() {{
            put("vitality", 0);
            put("tenacity", 0);
            put("vigor", 0);
            put("focus", 0);
            put("perspicacity", 0);
        }};

        this.currentHealthPercent = 100;

        this.buildStats = new Stats(buildItems, situationals, infusions, currentHealthPercent);

        updateButtons();
    }

    public void updateButtons() {
        this.halfWidth = width/2;
        buildItemButtons.clear();
        for (int i = 0; i < 6; i++) {
            BuildItemButtonWidget itemButton = getBuildItemButtonWidget(i);
            buildItemButtons.add(itemButton);
        }

        buildCharmButtons.clear();
        if (charms.isEmpty()) {
            charmsButton = getCharmButtonWidget(0, null);
        } else {
            for (int i = 0; i < charms.size(); i++) {
                DictionaryCharm charm = charms.get(i);
                charmsButton = getCharmButtonWidget(i, charm);
            }
            if (charms.size() < 12) {
                charmsButton = getCharmButtonWidget(charms.size(), null);
            }
        }
    }

    private BuildCharmButtonWidget getCharmButtonWidget(int i, @Nullable DictionaryCharm charm) {
        charmsButton = new BuildCharmButtonWidget(halfWidth + 30 + ((i % 6) * (buttonSize + itemPadding)), charmsY + ((i / 6) * (buttonSize + itemPadding)),buttonSize, Text.literal(""), (b) -> {
            charmButtonclicked(charm, hasShiftDown(), hasControlDown());
        }, charm,  () -> (charm != null) ? controller.charmGui.generateCharmLoreText(charm) : null, this);
        buildCharmButtons.add(charmsButton);

        return charmsButton;
    }
    private BuildItemButtonWidget getBuildItemButtonWidget(int i) {
        DictionaryItem item = buildItems.get(i);
        String itemType = itemTypesIndex.get(i);
        BuildItemButtonWidget itemButton = new BuildItemButtonWidget(
                (i < 2) ? halfWidth + 30 : itemPadding,
                labelMenuHeight + itemPadding + (buttonSize+itemPadding)*(i < 2 ? i : i - 2),
                buttonSize,
                Text.literal(""),
                (b) -> {
                    itemButtonClicked(item, itemType, hasShiftDown(), hasControlDown());
                },
                item,
                () -> controller.itemGui.generateItemLoreText(item),
                this
        );
        return itemButton;
    }

    private void charmButtonclicked(@Nullable DictionaryCharm charm, boolean shiftDown, boolean ctrlDown) {
        if (charm == null) controller.getCharmFromDictionary();
        else if (!shiftDown && !ctrlDown) {
            charms.removeAll(Collections.singleton(charm));
            controller.getCharmFromDictionary();
        } else if (shiftDown) {
            charms.removeAll(Collections.singleton(charm));
            updateStats();
        } else {
            String wikiFormatted = charm.name.replace(" ", "_").replace("'", "%27");
            Util.getOperatingSystem().open("https://monumenta.wiki.gg/wiki/" + wikiFormatted);
        }
    }

    private void itemButtonClicked(@Nullable DictionaryItem item, String itemType, boolean shiftDown, boolean controlDown) {
        if (!shiftDown && !controlDown) controller.getItemFromDictionary(itemType);
        else if (shiftDown) {
            buildItems.set(itemTypesIndex.indexOf(itemType), null);
            updateStats();
        } else if (item != null) {
            String wikiFormatted = item.name.replace(" ", "_").replace("'", "%27");
            Util.getOperatingSystem().open("https://monumenta.wiki.gg/wiki/" + wikiFormatted);
        }
    }

    private boolean verifyUrl(String buildUrl) {
        return buildUrl.contains("ohthemisery.tk/builder") || buildUrl.contains("ohthemisery.vercel.app/builder");
    }

    private void getBuildFromUrl(String buildUrl) {
        updateUserOptions();
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

        charms.clear();
        String[] rawCharms = buildUrl.substring(buildUrl.indexOf("charm=") + 6).split(",");
        if (!rawCharms[0].equals("None")) {
            for (String charm : rawCharms) {
                DictionaryCharm charmToAdd = controller.getCharmByName(charm);
                if (charmToAdd != null) {
                    for (int i = 0; i < charmToAdd.power; i++) {
                        charms.add(charmToAdd);
                    }
                }
            }
        }

        updateButtons();
        updateStats();
    }

    public void updateUserOptions() {
    }

    public void updateStats() {
        buildStats = new Stats(buildItems, situationals, infusions, currentHealthPercent);
        statsToRender.clear();
        Map<String, String> statFormatter = StatsFormats.getStatFormats();
        Field[] allFields = Stats.class.getFields();
        List<Field> fields = Arrays.asList(allFields).stream().filter(field -> Modifier.isPublic(field.getModifiers())).collect(Collectors.toList());

        for (int i=0;i<fields.size();i++) {
            Field field = fields.get(i);
            try {
                String statName = field.getName();
                if (statName.contains("EHP")) statName =statName.substring(0, statName.indexOf("EHP"));
                else if (statName.contains("HNDR")) statName = statName.substring(0, statName.indexOf("HNDR"));
                else if (statName.contains("DR")) statName = statName.substring(0, statName.indexOf("DR"));
                String formattedStatName = statFormatter.get(statName);
                int intStatValue = 0;
                double doubleStatValue = 0.0;
                String formattedStatValue;
                if (field.get(buildStats) instanceof Percentage) {
                    doubleStatValue = (((Percentage) field.get(buildStats)).perc);
                    formattedStatValue = String.format("%.2f", doubleStatValue) + "%";
                } else if (field.get(buildStats) instanceof Integer){
                    intStatValue = field.getInt(buildStats);
                    formattedStatValue = String.valueOf(intStatValue);
                } else {
                    doubleStatValue = field.getDouble(buildStats);
                    formattedStatValue = String.valueOf(String.format("%.2f", doubleStatValue));
                }

                String formattedStat = formattedStatName + formattedStatValue;
                statsToRender.add(formattedStat);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
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

        matrices.push();
        matrices.translate(0, 0, 110);

        nameBar.render(matrices, mouseX, mouseY, delta);
        addBuildButton.render(matrices, mouseX, mouseY, delta);
        setBuildFromClipboardButton.render(matrices, mouseX, mouseY, delta);
        showBuildDictionaryButton.render(matrices, mouseX, mouseY, delta);

        assert MinecraftClient.getInstance().player != null;
        this.halfWidth = width/2;
        int size = 100;
        int x = halfWidth - 100;
        int y = size*2 + 20;
        int entityMouseX = -(mouseX - x);
        int entityMouseY = -(mouseY - y + size + 3*size/4);
        drawEntity(matrices, x, y, size, entityMouseX, entityMouseY, MinecraftClient.getInstance().player);

        updateButtons();
        drawButtons(matrices, mouseX, mouseY, delta);
        drawItemText(matrices);
        drawStats(matrices);

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
            drawTextWithShadow(matrices,
                    textRenderer,
                    Text.literal(itemTypesIndex.get(i)).setStyle(Style.EMPTY.withBold(true)),
                    ((i < 2) ? halfWidth + 30 : itemPadding) + buttonSize + 2*itemPadding,
                    labelMenuHeight + itemPadding + (buttonSize+itemPadding)*(i < 2 ? i : i - 2),
                    0xFFFFFFFF);
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
                drawTextWithShadow(matrices,
                        textRenderer,
                        Text.literal(item.name).setStyle(Style.EMPTY.withBold(true).withUnderline(true)),
                        ((i < 2) ? halfWidth + 30 : itemPadding) + buttonSize + 2*itemPadding,
                        10 + labelMenuHeight + itemPadding + (buttonSize+itemPadding)*(i < 2 ? i : i - 2),
                        0xFF000000 + ItemColors.getColorForLocation(item.location));
            }
            i++;
        }

        drawTextWithShadow(matrices, textRenderer, Text.literal("Charms").setStyle(Style.EMPTY.withBold(true)), halfWidth + 30, 200, 0xFFFFFFFF);
        String stars = "★".repeat(charms.size()) + "☆".repeat(12 - charms.size());
        drawTextWithShadow(matrices, textRenderer, Text.literal(stars).setStyle(Style.EMPTY.withBold(true)), halfWidth + 80, 200, 0xFFFFFF00);
        drawTextWithShadow(matrices, textRenderer, Text.literal(charms.size() + "/12").setStyle(Style.EMPTY.withBold(true)), halfWidth + 190, 200, 0xFFFFFF00);
        if (charms.size() == 12) {
            drawTextWithShadow(matrices, textRenderer, Text.literal("FULL CHARM POWER").setStyle(Style.EMPTY.withBold(true).withUnderline(true)), halfWidth + 250, 200, 0xFFFF0000);
        }
    }

    private void drawStats(MatrixStack matrices) {
        if (statsToRender.isEmpty()) return;
        List<String> statsTypes = new ArrayList<>(Arrays.asList("Misc Stats", "Health Stats", "DR Stats", "HP Normalized DR Stats", "EHP Stats", "Melee Stats", "Projectile Stats", "Magic Stats"));
        List<List<String>> statsByType = new ArrayList<>();

        statsByType.add(statsToRender.subList(0 ,  6)); // Misc Stats
        statsByType.add(statsToRender.subList(6 , 14)); // Health Stats
        statsByType.add(statsToRender.subList(14, 21)); // Damage Reduction Stats
        statsByType.add(statsToRender.subList(21, 28)); // Health Normalized Damage Reduction Stats
        statsByType.add(statsToRender.subList(28, 35)); // EHP Stats
        statsByType.add(statsToRender.subList(35, 42)); // Melee Stats
        statsByType.add(statsToRender.subList(42, 48)); // Projectile Stats
        statsByType.add(statsToRender.subList(48, 52)); // Magic Stats

        int i = 0;
        int j = 0;
        for (List<String> stats : statsByType) {
            drawTextWithShadow(matrices, textRenderer, Text.literal(statsTypes.get(i)).setStyle(Style.EMPTY.withBold(true)), 5 + ((i/3)*170), 260 + (j*10)%270, 0xFF92BDA3);
            for (String stat : stats) {
                j++;
                drawTextWithShadow(matrices, textRenderer, Text.literal(stat).setStyle(Style.EMPTY.withBold(true)), 5 + ((i/3)*170), 260 + (j*10)%270, 0xFFA1BA89);
            }
            j += 2;
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
        buildStats = new Stats(buildItems, situationals, infusions, currentHealthPercent);
        updateButtons();
    }
}
