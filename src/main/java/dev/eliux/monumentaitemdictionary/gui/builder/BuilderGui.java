package dev.eliux.monumentaitemdictionary.gui.builder;

import dev.eliux.monumentaitemdictionary.gui.DictionaryController;
import dev.eliux.monumentaitemdictionary.gui.charm.DictionaryCharm;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;
import dev.eliux.monumentaitemdictionary.gui.widgets.BuildCharmButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.BuildItemButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.CheckBoxWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.ItemIconButtonWidget;
import dev.eliux.monumentaitemdictionary.util.*;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

public class BuilderGui extends Screen {
    private final DictionaryController controller;
    public final List<String> itemTypesIndex = Arrays.asList("Mainhand", "Offhand", "Helmet", "Chestplate", "Leggings", "Boots");
    public final List<String> situationals = Arrays.asList("Shielding", "Poise", "Inure", "Steadfast", "Guard", "Second Wind", "Ethereal", "Reflexes", "Evasion", "Tempo", "Cloaked", "Versatile");
    private final List<BuildItemButtonWidget> buildItemButtons = new ArrayList<>();
    private final List<ItemStack> buildItemStacks = new ArrayList<>();
    private final List<BuildCharmButtonWidget> buildCharmButtons = new ArrayList<>();
    public List<DictionaryCharm> charms = new ArrayList<>();
    public List<DictionaryItem> buildItems = Arrays.asList(null, null, null, null, null, null);
    private BuildCharmButtonWidget charmsButton;
    private Stats buildStats;
    private final List<String> statsToRender = new ArrayList<>();
    public float scale = 1;
    public int sideMenuWidth = 40;
    public int labelMenuHeight = 30;
    public int itemPadding = 5;
    public int buttonSize = 50;
    public int charmsY = labelMenuHeight + itemPadding + (buttonSize + itemPadding) * 3;
    public int statsY = 260 + itemPadding*2;
    private int halfWidth;
    private int statsRow = 350;
    private int statsColumn = 170;
    private int halfWidthPadding;
    private TextFieldWidget nameBar;
    private ItemIconButtonWidget showBuildDictionaryButton;
    private ItemIconButtonWidget setBuildFromClipboardButton;
    private ItemIconButtonWidget addBuildButton;
    private Map<String, Boolean> enabledSituationals;
    private HashMap<String, Integer> infusions;
    private final List<CheckBoxWidget> situationalCheckBoxList = new ArrayList<>();
    private double currentHealthPercent;
    private int scrollPixels = 0;

    public BuilderGui(Text title, DictionaryController controller) {
        super(title);
        this.controller = controller;
    }
    public void postInit() {
        this.halfWidth = width/2;
        nameBar = new TextFieldWidget(textRenderer, width / 2 +90, 7, width / 2 - 100, 15, Text.literal("Build Name"));

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
                    controller.buildDictionaryGui.addBuild(nameBar.getText(), buildItems, charms);
                    resetBuild();
                    controller.setBuildDictionaryScreen();
                    },
                Text.literal("Add Build To Dictionary"), "writable_book", "");

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
        infusions = new HashMap<>() {{
            put("vitality", 0);
            put("tenacity", 0);
            put("vigor", 0);
            put("focus", 0);
            put("perspicacity", 0);
        }};
        currentHealthPercent = 100;

        this.buildStats = new Stats(buildItems, enabledSituationals, infusions, currentHealthPercent);

        updateButtons();
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
        return buildUrl.contains("ohthemisery.tk/builder") || buildUrl.contains("ohthemisery.vercel.app/builder");
    }
    private BuildCharmButtonWidget getCharmButtonWidget(int i, @Nullable DictionaryCharm charm) {
        charmsButton = new BuildCharmButtonWidget(
                (halfWidth + halfWidthPadding + (((int) (i % floor((double) ((width - sideMenuWidth) - (halfWidth + halfWidthPadding)) /buttonSize))) * (buttonSize + itemPadding))),
                (charmsY + (((int) (i / floor((double) ((width - sideMenuWidth) - (halfWidth + halfWidthPadding)) /buttonSize))) * (buttonSize + itemPadding))) - scrollPixels,
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
        else if (shiftDown) {
            buildItems.set(itemTypesIndex.indexOf(itemType), null);
            updateStats();
        } else if (item != null) {
            String wikiFormatted = item.name.replace(" ", "_").replace("'", "%27");
            Util.getOperatingSystem().open("https://monumenta.wiki.gg/wiki/" + wikiFormatted);
        }
    }
    public void updateGuiPositions() {
        halfWidth = width/2;
        halfWidthPadding = textRenderer.getWidth(situationalCheckBoxList.get(6).getMessage()) + buttonSize + 3*itemPadding;

        buttonSize = width/18; // 50
        itemPadding = buttonSize/10; // 5
        charmsY = labelMenuHeight + itemPadding + (buttonSize + itemPadding) * 5 ; // 240
        statsY = labelMenuHeight + itemPadding + (buttonSize + itemPadding) * 4; // 260
        statsRow = 270; // 270
        statsColumn = 170; // 170

        showBuildDictionaryButton.setX(width - sideMenuWidth + 10);
        showBuildDictionaryButton.setY(labelMenuHeight + 10);

        updateButtons();
    }
    public void updateButtons() {
        situationalCheckBoxList.clear();
        for (int i = 0; i < situationals.size(); i++) {
            String situational = situationals.get(i);
            CheckBoxWidget situationalCheckBox = new CheckBoxWidget(
                    width/3  + ((i / 6) * 90), labelMenuHeight + itemPadding + (20) * (i % 6) - scrollPixels, 20, 20,
                    Text.literal(situational),
                    enabledSituationals.get(situational.replace(" ", "_").toLowerCase()), true, this);
            situationalCheckBoxList.add(situationalCheckBox);
        }
        halfWidthPadding = textRenderer.getWidth(situationalCheckBoxList.get(6).getMessage()) + buttonSize + itemPadding;

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
        updateItemStack();
    }
    public void updateUserOptions() {
    }
    public void updateSituationals () {
        System.out.println(enabledSituationals);
        Iterator<CheckBoxWidget> checkBox = situationalCheckBoxList.iterator();
        Iterator<String> situational = situationals.iterator();
        while (checkBox.hasNext() && situational.hasNext()) {
            enabledSituationals.put(situational.next().replace(" ", "_").toLowerCase(), checkBox.next().isChecked());
        }
    }
    public void updateStats() {
        buildStats = new Stats(buildItems, enabledSituationals, infusions, currentHealthPercent);
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
    public void updateItemStack() {
        for (DictionaryItem item : buildItems) {
            if (item == null) buildItemStacks.add(null);
            else {
                ItemStack builtItem = ItemFactory.fromEncoding(item.baseItem.split("/")[0].trim().toLowerCase().replace(" ", "_"));
                NbtCompound baseNbt = builtItem.getOrCreateNbt();
                NbtCompound plain = new NbtCompound();
                NbtCompound display = new NbtCompound();
                display.putString("Name", item.name.split("\\(")[0].trim());
                plain.put("display", display);
                baseNbt.put("plain", plain);
                builtItem.setNbt(baseNbt);

                buildItemStacks.add(builtItem);
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
        updateSituationals();
        buildStats = new Stats(buildItems, enabledSituationals, infusions, currentHealthPercent);
        updateButtons();
    }
    private void drawButtons(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        buildItemButtons.forEach((b) -> b.render(matrices, mouseX, mouseY, delta));
        buildCharmButtons.forEach((b) -> b.render(matrices, mouseX, mouseY, delta));
        situationalCheckBoxList.forEach((b) -> b.render(matrices, mouseX, mouseY, delta));
    }
    private void drawItemText(MatrixStack matrices) {
        for (int i = 0;i < buildItems.size(); i++) {
            DictionaryItem item = buildItems.get(i);
            if (item != null) {
                drawTextWithShadow(matrices,
                        textRenderer,
                        Text.literal(item.name).setStyle(Style.EMPTY.withBold(true).withUnderline(true)),
                        ((i < 2) ? halfWidth + halfWidthPadding : itemPadding) + buttonSize + 2*itemPadding,
                        10 + labelMenuHeight + itemPadding + (buttonSize+itemPadding)*(i < 2 ? i : i - 2) - scrollPixels,
                        0xFF000000 + ItemColours.getColorForLocation(item.location));
            }
        }

        for (int i = 0; i < buildItemButtons.size(); i++) {
            drawTextWithShadow(matrices,
                    textRenderer,
                    Text.literal(itemTypesIndex.get(i)).setStyle(Style.EMPTY.withBold(true)),
                    ((i < 2) ? halfWidth + halfWidthPadding : itemPadding) + buttonSize + 2*itemPadding,
                    labelMenuHeight + itemPadding + (buttonSize+itemPadding)*(i < 2 ? i : i - 2) - scrollPixels,
                    0xFFFFFFFF);
        }

        String stars = "★".repeat(charms.size()) + "☆".repeat(12 - charms.size());
        String starsNumber = charms.size() + "/12";
        drawTextWithShadow(matrices, textRenderer,
                Text.literal("Charms"),
                halfWidth + halfWidthPadding, charmsY-20 - scrollPixels, 0xFFFFFFFF);
        drawTextWithShadow(matrices, textRenderer,
                Text.literal(stars),
                halfWidth + halfWidthPadding, charmsY-10 - scrollPixels, 0xFFFFFF00);
        drawTextWithShadow(matrices, textRenderer,
                Text.literal(starsNumber),
                halfWidth + halfWidthPadding + textRenderer.getWidth(stars) + 5, charmsY-10 - scrollPixels, 0xFFFFFF00);
        if (charms.size() == 12) {
            drawTextWithShadow(matrices, textRenderer,
                    Text.literal("FULL CHARM POWER").setStyle(Style.EMPTY.withBold(true).withUnderline(true)),
                    halfWidth + halfWidthPadding, charmsY-40 - scrollPixels, 0xFFFF0000);
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
            drawTextWithShadow(matrices, textRenderer, Text.literal(statsTypes.get(i)).setStyle(Style.EMPTY.withBold(true)), itemPadding + (i/4)*statsColumn, statsY + (j*10)%statsRow - scrollPixels, 0xFF92BDA3);
            for (String stat : stats) {
                j++;
                drawTextWithShadow(matrices, textRenderer, Text.literal(stat), itemPadding + (i/4)*statsColumn, statsY + (j*10)%statsRow - scrollPixels, 0xFFA1BA89);
            }
            j += 2;
            i++;
        }
    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        int totalRows = 50;
        int totalPixelHeight = totalRows * buttonSize + (totalRows + 1) * itemPadding;
        double bottomPercent = (double)scrollPixels / totalPixelHeight;
        double screenPercent = (double)(height - labelMenuHeight) / totalPixelHeight;
        drawVerticalLine(matrices, width - sideMenuWidth - 1, labelMenuHeight, height, 0x77AAAAAA); // called twice to make the scroll bar render wider (janky, but I don't really care)
        drawVerticalLine(matrices, width - sideMenuWidth - 2, labelMenuHeight, height, 0x77AAAAAA);
        drawVerticalLine(matrices, width - sideMenuWidth - 1, (int) (labelMenuHeight + (height - labelMenuHeight) * bottomPercent), (int) (labelMenuHeight + (height - labelMenuHeight) * (bottomPercent + screenPercent)), 0xFFC3C3C3);
        drawVerticalLine(matrices, width - sideMenuWidth - 2, (int) (labelMenuHeight + (height - labelMenuHeight) * bottomPercent), (int) (labelMenuHeight + (height - labelMenuHeight) * (bottomPercent + screenPercent)), 0xFFC3C3C3);

//        assert MinecraftClient.getInstance().player != null;
//        int size = buttonSize * 2;
//        int x = 2*width/3;
//        int y = size*2 + 30;
//        int entityMouseX = -(mouseX - x);
//        int entityMouseY = -(mouseY - y + size + 3*size/4);
//        drawEntity(matrices, x, y, size, entityMouseX, entityMouseY, MinecraftClient.getInstance().player);
        updateButtons();
        drawButtons(matrices, mouseX, mouseY, delta);
        drawItemText(matrices);
        drawStats(matrices);

        matrices.push();
        matrices.translate(0, 0, 110);
        fill(matrices, 0, 0, width, labelMenuHeight, 0xFF555555);
        drawHorizontalLine(matrices, 0, width, labelMenuHeight, 0xFFFFFFFF);
        drawCenteredTextWithShadow(matrices, textRenderer, Text.literal("Monumenta Builder").setStyle(Style.EMPTY.withBold(true)), width / 2, (labelMenuHeight - textRenderer.fontHeight) / 2, 0xFF2ca9d3);

        nameBar.render(matrices, mouseX, mouseY, delta);
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
        addBuildButton.mouseClicked(mouseX, mouseY, button);
        setBuildFromClipboardButton.mouseClicked(mouseX, mouseY, button);
        showBuildDictionaryButton.mouseClicked(mouseX, mouseY, button);

        situationalCheckBoxList.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));
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
    private void updateScrollLimits () {
        int rows = labelMenuHeight + (buttonSize + itemPadding) * 4 + statsToRender.size() / 3;
        int maxScroll = rows * buttonSize + (rows + 1) * itemPadding - height + labelMenuHeight;
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
}
