package dev.eliux.monumentaitemdictionary.gui.builder;

import dev.eliux.monumentaitemdictionary.gui.DictionaryController;
import dev.eliux.monumentaitemdictionary.gui.charm.DictionaryCharm;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;
import dev.eliux.monumentaitemdictionary.gui.widgets.BuildCharmButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.BuildItemButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.CheckBoxWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.ItemIconButtonWidget;
import dev.eliux.monumentaitemdictionary.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.List;

import static java.lang.Math.floor;
import static java.lang.Math.max;
public class BuilderGui extends Screen {
    private final DictionaryController controller;
    public final List<String> itemTypesIndex = Arrays.asList("Mainhand", "Offhand", "Helmet", "Chestplate", "Leggings", "Boots");
    public final List<String> situationals = Arrays.asList("Shielding", "Poise", "Inure", "Steadfast", "Guard", "Second Wind", "Ethereal", "Reflexes", "Evasion", "Tempo", "Cloaked", "Versatile");
    public final List<String> infusions = Arrays.asList("Vigor", "Focus", "Tenacity", "Vitality", "Perspicacity");
    private final List<BuildItemButtonWidget> buildItemButtons = new ArrayList<>();
    private final List<BuildCharmButtonWidget> buildCharmButtons = new ArrayList<>();
    public List<DictionaryCharm> charms = new ArrayList<>();
    public List<DictionaryItem> buildItems = Arrays.asList(null, null, null, null, null, null);
    private Regions region = Regions.NO_REGION;
    public DictionaryItem itemOnBuildButton;
    private BuildCharmButtonWidget charmsButton;
    private Stats buildStats;
    private final List<String> statsToRender = new ArrayList<>();
    public int sideMenuWidth = 40;

    public int labelMenuHeight = 30;
    public int itemPadding = 5;
    public int buttonSize = 50;
    public int checkBoxSise = 20;
    public int statsY = 260 + itemPadding*2;
    public int statsRow = 350;
    public int statsColumn = 170;
    public int charmsY = labelMenuHeight + itemPadding + (buttonSize + itemPadding) * 3;
    public int charmsButtonY;
    public int charmsX = 2*statsColumn + itemPadding;
    public int textTimeOffset = 1;
    public float deltaTicks = 0;
    private int halfWidth;
    private int halfWidthPadding;
    private TextFieldWidget nameBar;
    private SliderWidget currentHealthSlider;
    private ItemIconButtonWidget showBuildDictionaryButton;
    private ItemIconButtonWidget setBuildFromClipboardButton;
    private ItemIconButtonWidget addBuildButton;
    private CyclingButtonWidget<Regions> regionButton;
    private Map<String, Boolean> enabledSituationals;
    private HashMap<String, Boolean> enabledInfusions;
    private final List<CheckBoxWidget> situationalCheckBoxList = new ArrayList<>();
    private final List<CheckBoxWidget> infusionsCheckBoxList = new ArrayList<>();
    private double currentHealthPercent;
    private int scrollPixels = 0;
    private int statusY;
    private String statusText = "";

    public BuilderGui(Text title, DictionaryController controller) {
        super(title);
        this.controller = controller;

    }
    public void postInit() {

        this.charmsButtonY = (int) (charms.size()/floor((double) (width - sideMenuWidth - charmsX)/(buttonSize + itemPadding)))*
                (buttonSize + itemPadding) - scrollPixels + buttonSize + itemPadding;
        this.halfWidth = width/2;

        nameBar = new TextFieldWidget(textRenderer,
                190 + textRenderer.getWidth(Text.literal("Monumenta Builder").setStyle(Style.EMPTY.withBold(true))),
                itemPadding,
                width - itemPadding - (185 + textRenderer.getWidth(Text.literal("Monumenta Builder").setStyle(Style.EMPTY.withBold(true)))),
                20, Text.literal("Build Name"));
        nameBar.setPlaceholder(Text.literal("Build Name: "));

        currentHealthSlider = new SliderWidget(
                charmsX,
                charmsY + charmsButtonY,
                (width - sideMenuWidth) - charmsX - itemPadding,
                20,
                Text.literal("Current Health: 100%"),
                1) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Current Health: " + (float) this.value * 100 + "%"));
            }

            @Override
            protected void applyValue() {
                currentHealthPercent = this.value * 100;
                updateStats();
            }
        };

        setBuildFromClipboardButton = new ItemIconButtonWidget(30, 5, 20, 20, Text.literal(""), (button) -> {
            try {
                String buildUrl = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                if (verifyUrl(buildUrl)) {
                    getBuildFromUrl(buildUrl);
                }
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }, Text.literal("Add Build From Clipboard"), "name_tag", "");

        showBuildDictionaryButton = new ItemIconButtonWidget(
                width - sideMenuWidth + 10, labelMenuHeight + 10, 20, 20,
                Text.literal(""),
                (button) -> controller.setBuildDictionaryScreen(),
                Text.literal("Builds Data"),
                "diamond_chestplate", "");

        addBuildButton = new ItemIconButtonWidget(
                5, 5, 20, 20,
                Text.literal(""),
                (button) -> {
                    if (itemOnBuildButton == null) statusText = "Please select an item to appear on your Build Icon";
                    else if (nameBar.getText().isBlank()) statusText = "Please put a name to your Build";
                    else {
                        controller.buildDictionaryGui.addBuild(nameBar.getText(), buildItems, charms,
                                itemOnBuildButton);
                        resetBuild();
                        controller.setBuildDictionaryScreen();
                    }
                },
                Text.literal("Add Build To Dictionary"), "writable_book", "");

        regionButton = CyclingButtonWidget.builder(Regions::getText)
                .values(Regions.values())
                .initially(Regions.NO_REGION)
                .build(55, 5, 125, 20, Text.literal("Region"),
                        (button, region) -> this.region = region);
        enabledSituationals = new HashMap<>() {{
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
        enabledInfusions = new HashMap<>() {{
            put("vigor", false);
            put("focus", false);
            put("tenacity", false);
            put("vitality", false);
            put("perspicacity", false);
        }};

        this.buildStats = new Stats(buildItems, enabledSituationals, enabledInfusions, currentHealthPercent);

        updateButtons();
        updateGuiPositions();
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
    private boolean verifyUrl(String buildUrl) {
        return buildUrl.contains("ohthemisery.tk/builder") || buildUrl.contains("ohthemisery.vercel.app/builder") || buildUrl.contains("ohthemisery-psi.vercel.app/builder");
    }
    private BuildCharmButtonWidget getCharmButtonWidget(int i, @Nullable DictionaryCharm charm) {
        charmsButton = new BuildCharmButtonWidget(
                charmsX + (int) (i % floor((double) (width - sideMenuWidth - charmsX) / (buttonSize + itemPadding))) * (buttonSize + itemPadding),
                charmsY + (int) (i / floor((double) (width - sideMenuWidth - charmsX) / (buttonSize + itemPadding))) * (buttonSize + itemPadding) - scrollPixels,
                buttonSize,
                Text.literal(""),
                (b) -> charmButtonClicked(charm, hasShiftDown(), hasControlDown()), charm,  () -> (charm != null) ? controller.charmGui.generateCharmLoreText(charm) : null,
                this);
        buildCharmButtons.add(charmsButton);

        return charmsButton;
    }
    private void charmButtonClicked(@Nullable DictionaryCharm charm, boolean shiftDown, boolean ctrlDown) {
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
    private BuildItemButtonWidget getBuildItemButtonWidget(int i) {
        DictionaryItem item = buildItems.get(i);
        String itemType = itemTypesIndex.get(i);
        return new BuildItemButtonWidget(
                (i < 2) ? halfWidth + halfWidthPadding : itemPadding,
                labelMenuHeight + itemPadding + (buttonSize+itemPadding)*(i < 2 ? i : i - 2) - scrollPixels,
                buttonSize,
                Text.literal(""),
                (b) -> itemButtonClicked(item, itemType, hasShiftDown(), hasControlDown()),
                item,
                () -> controller.itemGui.generateItemLoreText(item),
                this
        );
    }
    private void itemButtonClicked(@Nullable DictionaryItem item, String itemType, boolean shiftDown, boolean controlDown) {
        if (!shiftDown && !controlDown) controller.getItemFromDictionary(itemType);
        else if (shiftDown && !controlDown) {
            buildItems.set(itemTypesIndex.indexOf(itemType), null);
            updateStats();
        } else if (!shiftDown && item != null) {
            itemOnBuildButton = item;
        } else if (item != null) {
            String wikiFormatted = item.name.replace(" ", "_").replace("'", "%27");
            Util.getOperatingSystem().open("https://monumenta.wiki.gg/wiki/" + wikiFormatted);
        }
    }
    public void updateGuiPositions() {
        halfWidth = width/2;
        halfWidthPadding = textRenderer.getWidth(situationalCheckBoxList.get(6).getMessage()) + 3*(checkBoxSise + itemPadding);
        buttonSize = width/18; // 50
        itemPadding = buttonSize/10; // 5

        charmsY = labelMenuHeight + itemPadding + (buttonSize + itemPadding) * 2 + (checkBoxSise + itemPadding) * 5 + 30; // 240
        charmsButtonY = (int) ((charms.size())/floor((double) (width - sideMenuWidth - charmsX)/(buttonSize + itemPadding)))*
                (buttonSize + itemPadding) - scrollPixels + buttonSize + 2*itemPadding;
        statsY = Math.max(labelMenuHeight + itemPadding + (buttonSize + itemPadding) * 4, labelMenuHeight + itemPadding + (checkBoxSise + itemPadding)*6) + 20; // 260
        statusY = statsY - 20;

        showBuildDictionaryButton.setX(width - sideMenuWidth + 10);
        showBuildDictionaryButton.setY(labelMenuHeight + 10);

        nameBar.setWidth(width - 2*itemPadding - (190 + textRenderer.getWidth(Text.literal("Monumenta Builder").setStyle(Style.EMPTY.withBold(true)))));

        currentHealthSlider.setX(charmsX);
        currentHealthSlider.setY(charmsY + charmsButtonY);
        currentHealthSlider.setWidth(width - sideMenuWidth - charmsX - 2*itemPadding);
    }
    public void updateButtons() {
        situationalCheckBoxList.clear();
        for (int i = 0; i < situationals.size(); i++) {
            String situational = situationals.get(i);

            CheckBoxWidget situationalCheckBox = new CheckBoxWidget(
                    width/3 + (i/6)*90,
                    labelMenuHeight + itemPadding + (checkBoxSise + itemPadding)*(i%6) - scrollPixels,
                    checkBoxSise,
                    checkBoxSise,
                    Text.literal(situational),
                    enabledSituationals.get(situational.replace(" ", "_").toLowerCase()),
                    true,
                    this);
            situationalCheckBoxList.add(situationalCheckBox);
        }

        infusionsCheckBoxList.clear();
        for (int i = 0; i < infusions.size() ; i++) {
            String infusion = getFormattedText(infusions.get(i), halfWidth + halfWidthPadding + (i/6)*90 + checkBoxSise, width - sideMenuWidth, false);

            CheckBoxWidget infusionCheckBox = new CheckBoxWidget(
                    halfWidth + halfWidthPadding + (i/6)*90,
                    labelMenuHeight + itemPadding + 2*(buttonSize + itemPadding) + (checkBoxSise + itemPadding)*(i%6) - scrollPixels,
                    checkBoxSise,
                    checkBoxSise,
                    Text.literal(infusion),
                    enabledInfusions.get(infusions.get(i).toLowerCase()),
                    true,
                    this);
            infusionsCheckBoxList.add(infusionCheckBox);
        }

        halfWidthPadding = textRenderer.getWidth(situationalCheckBoxList.get(6).getMessage()) + 3*(checkBoxSise + itemPadding);

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

        updateGuiPositions();
    }
    public void updateUserOptions() {
    }
    public void updateCheckBoxes() {
        Iterator<CheckBoxWidget> checkBox = situationalCheckBoxList.iterator();
        Iterator<String> text = situationals.iterator();
        while (checkBox.hasNext() && text.hasNext()) {
            enabledSituationals.put(text.next().replace(" ", "_").toLowerCase(), checkBox.next().isChecked());
        }

        checkBox = infusionsCheckBoxList.iterator();
        text = infusions.iterator();
        while (checkBox.hasNext() && text.hasNext()) {
            enabledInfusions.put(text.next().toLowerCase(), checkBox.next().isChecked());
        }
    }
    public void updateStats() {
        updateCheckBoxes();
        buildStats = new Stats(buildItems, enabledSituationals, enabledInfusions, currentHealthPercent);
        statsToRender.clear();
        Map<String, String> statFormatter = StatsFormats.getStatFormats();
        Field[] allFields = Stats.class.getFields();
        List<Field> fields = Arrays.stream(allFields).filter(field -> Modifier.isPublic(field.getModifiers())).toList();

        for (Field field : fields) {
            try {
                String statName = field.getName();
                if (statName.contains("EHP")) statName = statName.substring(0, statName.indexOf("EHP"));
                else if (statName.contains("HNDR")) statName = statName.substring(0, statName.indexOf("HNDR"));
                else if (statName.contains("DR")) statName = statName.substring(0, statName.indexOf("DR"));
                String formattedStatName = statFormatter.get(statName);
                String formattedStat = getFormattedStat(field, formattedStatName);
                statsToRender.add(formattedStat);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    private String getFormattedStat(Field field, String formattedStatName) throws IllegalAccessException {
        int intStatValue;
        double doubleStatValue;
        String formattedStatValue;
        if (field.get(buildStats) instanceof Percentage) {
            doubleStatValue = (((Percentage) field.get(buildStats)).perc);
            formattedStatValue = String.format("%.2f", doubleStatValue) + "%";
        } else if (field.get(buildStats) instanceof Integer) {
            intStatValue = field.getInt(buildStats);
            formattedStatValue = String.valueOf(intStatValue);
        } else {
            doubleStatValue = field.getDouble(buildStats);
            formattedStatValue = String.valueOf(String.format("%.2f", doubleStatValue));
        }

        return formattedStatName + formattedStatValue;
    }
    public void loadItems(DictionaryBuild build) {
        for (int i = 0; i < build.allItems.size(); i++) {
            buildItems.set(i, build.allItems.get(i));
        }

        charms = build.charms;

        updateButtons();
    }
    public void resetBuild() {
        buildItems = Arrays.asList(null, null, null, null, null, null);
        charms = new ArrayList<>();
        updateCheckBoxes();
        buildStats = new Stats(buildItems, enabledSituationals, enabledInfusions, currentHealthPercent);
        updateStats();
        updateButtons();
    }
    private void drawButtons(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        buildItemButtons.forEach((b) -> b.render(matrices, mouseX, mouseY, delta));
        buildCharmButtons.forEach((b) -> b.render(matrices, mouseX, mouseY, delta));
        situationalCheckBoxList.forEach((b) -> b.render(matrices, mouseX, mouseY, delta));
        infusionsCheckBoxList.forEach((b) -> b.render(matrices, mouseX, mouseY, delta));
    }
    private void drawItemText(MatrixStack matrices) {
        for (int i = 0;i < buildItems.size(); i++) {
            DictionaryItem item = buildItems.get(i);
            if (item != null) {
                int x = ((i < 2) ? halfWidth + halfWidthPadding : itemPadding) + buttonSize + 2*itemPadding;
                int y = 10 + labelMenuHeight + itemPadding + (buttonSize+itemPadding)*(i < 2 ? i : i - 2) - scrollPixels;
                String itemText = getFormattedText(item.name, x, (i < 2) ? width - sideMenuWidth : width/3, true);
                drawTextWithShadow(matrices,
                        textRenderer,
                        Text.literal(itemText).setStyle(Style.EMPTY.withBold(true).withUnderline(true)),
                        x,
                        y,
                        0xFF000000 + ItemColours.getColorForLocation(item.location));
            }
        }

        for (int i = 0; i < buildItemButtons.size(); i++) {
            int x = ((i < 2) ? halfWidth + halfWidthPadding : itemPadding) + buttonSize + 2*itemPadding;
            int y = labelMenuHeight + itemPadding + (buttonSize+itemPadding)*(i < 2 ? i : i - 2) - scrollPixels;
            String text = getFormattedText(itemTypesIndex.get(i), x, (i < 2) ? width - sideMenuWidth : width/3, true);
            drawTextWithShadow(matrices,
                    textRenderer,
                    Text.literal(text).setStyle(Style.EMPTY.withBold(true)),
                    x,
                    y,
                    0xFFFFFFFF);
        }

        String stars = "★".repeat(charms.size()) + "☆".repeat(12 - charms.size()) + " " + charms.size() + "/12";
        stars = (textRenderer.getWidth(stars) > width - sideMenuWidth - charmsX) ? charms.size() + "/12" : stars;
        drawTextWithShadow(matrices, textRenderer,
                Text.literal("Charms"),
                charmsX, charmsY-20 - scrollPixels, 0xFFFFFFFF);
        drawTextWithShadow(matrices, textRenderer,
                Text.literal(stars),
                charmsX, charmsY - 10 - scrollPixels, 0xFFFFFF00);
        if (charms.size() == 12) {
            drawTextWithShadow(matrices, textRenderer,
                    Text.literal(getFormattedText("Full Charms", charmsX, width - labelMenuHeight, true)).setStyle(Style.EMPTY.withBold(true).withUnderline(true)),
                    charmsX, charmsY-30 - scrollPixels, 0xFFFF0000);
        }

        if (!statusText.isEmpty()) {
            drawTextWithShadow(matrices, textRenderer, Text.literal(statusText), itemPadding, statusY, 0xFFFF0000);
        }
    }
    private String getFormattedText(String text, int xi, int xf, boolean bold) {
        int textWidth = xf - xi - 10;
        if (textWidth >= textRenderer.getWidth(Text.literal(text).setStyle(Style.EMPTY.withBold(bold))) + 20) return text;
        int charWidth = textRenderer.getWidth(Text.literal("M").setStyle(Style.EMPTY.withBold(bold)));
        int textLength = (int) floor((double) textWidth/charWidth);


        int start = textTimeOffset % textLength;

        return (text + " " + text + " " + text).substring(start, start + textLength);
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
            drawTextWithShadow(matrices, textRenderer, Text.literal(statsTypes.get(i)).setStyle(Style.EMPTY.withBold(true)), itemPadding + (i/4)* statsColumn, statsY + (j*10)% statsRow - scrollPixels, 0xFF92BDA3);
            for (String stat : stats) {
                j++;
                drawTextWithShadow(matrices, textRenderer, Text.literal(stat), itemPadding + (i/4)* statsColumn, statsY + (j*10)% statsRow - scrollPixels, 0xFFA1BA89);
            }
            j += 2;
            i++;
        }
    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        deltaTicks += delta;
        textTimeOffset += (deltaTicks >= 20) ? 1 : 0;
        deltaTicks = (deltaTicks >= 20) ? 0 : deltaTicks;
        textTimeOffset = (textTimeOffset >= 100) ? 1 : textTimeOffset;

        int totalRows = 10;
        int totalPixelHeight = totalRows * buttonSize + (totalRows + 1) * itemPadding;
        double bottomPercent = (double)scrollPixels / totalPixelHeight;
        double screenPercent = (double)(height - labelMenuHeight) / totalPixelHeight;
        drawVerticalLine(matrices, width - sideMenuWidth - 1, labelMenuHeight, height, 0x77AAAAAA); // called twice to make the scroll bar render wider (janky, but I don't really care)
        drawVerticalLine(matrices, width - sideMenuWidth - 2, labelMenuHeight, height, 0x77AAAAAA);
        drawVerticalLine(matrices, width - sideMenuWidth - 1, (int) (labelMenuHeight + (height - labelMenuHeight) * bottomPercent), (int) (labelMenuHeight + (height - labelMenuHeight) * (bottomPercent + screenPercent)), 0xFFC3C3C3);
        drawVerticalLine(matrices, width - sideMenuWidth - 2, (int) (labelMenuHeight + (height - labelMenuHeight) * bottomPercent), (int) (labelMenuHeight + (height - labelMenuHeight) * (bottomPercent + screenPercent)), 0xFFC3C3C3);


        updateGuiPositions();
        updateButtons();
        drawButtons(matrices, mouseX, mouseY, delta);
        drawItemText(matrices);
        drawStats(matrices);

        matrices.push();
        matrices.translate(0, 0, 110);
        fill(matrices, 0, 0, width, labelMenuHeight, 0xFF555555);
        drawHorizontalLine(matrices, 0, width, labelMenuHeight, 0xFFFFFFFF);
        drawTextWithShadow(matrices, textRenderer, Text.literal("Monumenta Builder").setStyle(Style.EMPTY.withBold(true)), 185, (labelMenuHeight - textRenderer.fontHeight) / 2, 0xFF2ca9d3);
        matrices.pop();

        matrices.push();
        matrices.translate(0, 0, 110);
        regionButton.render(matrices, mouseX, mouseY, delta);
        nameBar.render(matrices, mouseX, mouseY, delta);
        currentHealthSlider.render(matrices, mouseX, mouseY, delta);
        addBuildButton.render(matrices, mouseX, mouseY, delta);
        setBuildFromClipboardButton.render(matrices, mouseX, mouseY, delta);
        showBuildDictionaryButton.render(matrices, mouseX, mouseY, delta);
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

        updateGuiPositions();
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        nameBar.mouseClicked(mouseX, mouseY, button);
        regionButton.mouseClicked(mouseX, mouseY, button);
        currentHealthSlider.mouseClicked(mouseX, mouseY, button);
        addBuildButton.mouseClicked(mouseX, mouseY, button);
        setBuildFromClipboardButton.mouseClicked(mouseX, mouseY, button);
        showBuildDictionaryButton.mouseClicked(mouseX, mouseY, button);

        situationalCheckBoxList.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));
        infusionsCheckBoxList.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));
        buildItemButtons.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));
        buildCharmButtons.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));

        return true;
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (mouseX >= 0 && mouseX < width - sideMenuWidth && mouseY >= labelMenuHeight && mouseY < height) {
            scrollPixels += (int) (-amount * 22); // scaled

            updateScrollLimits();
            }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (currentHealthSlider.isMouseOver(mouseX, mouseY)) currentHealthSlider.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void updateScrollLimits () {
        int maxScroll = max(4*(buttonSize + itemPadding) + labelMenuHeight + statsRow - height + 10, charmsButtonY + 50);
        if (scrollPixels > maxScroll) scrollPixels = maxScroll;

        if (scrollPixels < 0) scrollPixels = 0;
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
    enum Regions {
        NO_REGION(Text.literal("No Region")),
        KINGS_VALLEY(Text.literal("King's Valley")),
        CELSIAN_ISLES(Text.literal("Celsian Isles")),
        ARCHITECTS_RING(Text.literal("Architect's Ring"));

        private final Text text;
        private Regions(Text text) {
            this.text = text;
        }

        public Text getText() {
            return this.text;
        }
    }
}
