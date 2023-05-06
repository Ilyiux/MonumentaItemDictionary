package dev.eliux.monumentaitemdictionary.gui;

import com.google.gson.*;
import dev.eliux.monumentaitemdictionary.gui.charm.CharmDictionaryGui;
import dev.eliux.monumentaitemdictionary.gui.charm.CharmFilterGui;
import dev.eliux.monumentaitemdictionary.gui.charm.DictionaryCharm;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;
import dev.eliux.monumentaitemdictionary.gui.item.ItemDictionaryGui;
import dev.eliux.monumentaitemdictionary.gui.item.ItemFilterGui;
import dev.eliux.monumentaitemdictionary.util.CharmStat;
import dev.eliux.monumentaitemdictionary.util.Filter;
import dev.eliux.monumentaitemdictionary.util.ItemFormatter;
import dev.eliux.monumentaitemdictionary.util.ItemStat;
import dev.eliux.monumentaitemdictionary.web.WebManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DictionaryController {
    private String itemNameFilter;
    private boolean hasItemNameFilter = false;
    private String charmNameFilter;
    private boolean hasCharmNameFilter = false;
    private ArrayList<Filter> itemFilters = new ArrayList<>();
    private ArrayList<Filter> charmFilters = new ArrayList<>();

    private ArrayList<String> allItemTypes;
    private ArrayList<String> allItemRegions;
    private ArrayList<String> allItemTiers;
    private ArrayList<String> allItemLocations;
    private ArrayList<String> allItemStats;
    private ArrayList<String> allItemBaseItems;

    private ArrayList<String> allCharmRegions;
    private ArrayList<String> allCharmTiers;
    private ArrayList<String> allCharmLocations;
    private ArrayList<String> allCharmSkillMods;
    private ArrayList<String> allCharmClasses;
    private ArrayList<String> allCharmStats;
    private ArrayList<String> allCharmBaseItems;

    private final ArrayList<DictionaryItem> items;
    private ArrayList<DictionaryItem> validItems;
    private final ArrayList<DictionaryCharm> charms;
    private ArrayList<DictionaryCharm> validCharms;

    public boolean itemLoadFailed = false;
    public boolean charmLoadFailed = false;

    public ItemDictionaryGui itemGui;
    public boolean itemGuiPreviouslyOpened = false;
    public ItemFilterGui itemFilterGui;
    public boolean itemFilterGuiPreviouslyOpened = false;
    public CharmDictionaryGui charmGui;
    public boolean charmGuiPreviouslyOpened = false;
    public CharmFilterGui charmFilterGui;
    public boolean charmFilterGuiPreviouslyOpened = false;

    public DictionaryController() {
        items = new ArrayList<>();
        validItems = new ArrayList<>();
        charms = new ArrayList<>();
        validCharms = new ArrayList<>();

        loadItems();
        loadCharms();

        itemGui = new ItemDictionaryGui(new LiteralText("Monumenta Item Dictionary"), this);
        itemFilterGui = new ItemFilterGui(new LiteralText("Item Filter Menu"), this);
        charmGui = new CharmDictionaryGui(new LiteralText("Monumenta Charm Dictionary"), this);
        charmFilterGui = new CharmFilterGui(new LiteralText("Charm Filter Menu"), this);
    }

    public void open() {
        setItemDictionaryScreen();
    }

    public void setItemDictionaryScreen() {
        MinecraftClient.getInstance().setScreen(itemGui);
        if (!itemGuiPreviouslyOpened) {
            itemGui.postInit();
            itemGuiPreviouslyOpened = true;
        } else {
            itemGui.updateGuiPositions();
        }
    }

    public void setItemFilterScreen() {
        MinecraftClient.getInstance().setScreen(itemFilterGui);
        if (!itemFilterGuiPreviouslyOpened) {
            itemFilterGui.postInit();
            itemFilterGuiPreviouslyOpened = true;
        } else {
            //filterGui.updateGuiPositions();
        }
    }

    public void setCharmDictionaryScreen() {
        MinecraftClient.getInstance().setScreen(charmGui);
        if (!charmGuiPreviouslyOpened) {
            charmGui.postInit();
            charmGuiPreviouslyOpened = true;
        } else {
            charmGui.updateGuiPositions();
        }
    }

    public void setCharmFilterScreen() {
        MinecraftClient.getInstance().setScreen(charmFilterGui);
        if (!charmFilterGuiPreviouslyOpened) {
            charmFilterGui.postInit();
            charmFilterGuiPreviouslyOpened = true;
        } else {
            //charmFilterGui.updateGuiPositions();
        }
    }

    private String readItemData() {
        try {
            return Files.readString(Path.of("config/mid/items.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{}";
    }

    private void writeItemData(String writeData) {
        try {
            File targetFile = new File("config/mid/items.json");

            targetFile.getParentFile().mkdirs();
            targetFile.createNewFile();

            FileUtils.writeStringToFile(targetFile, writeData, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestAndUpdate() {
        try {
            String data = WebManager.getRequest("https://api.playmonumenta.com/items");

            writeItemData(data);
            loadItems();
            itemGui.buildItemList();
            loadCharms();
            charmGui.buildCharmList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadItems() {
        allItemTypes = new ArrayList<>();
        allItemRegions = new ArrayList<>();
        allItemTiers = new ArrayList<>();
        allItemLocations = new ArrayList<>();
        allItemStats = new ArrayList<>();
        allItemBaseItems = new ArrayList<>();

        try {
            String rawData = readItemData();

            items.clear();
            JsonObject data = new Gson().fromJson(rawData, JsonObject.class);
            for (String key : data.keySet()) {
                JsonObject itemData = data.getAsJsonObject(key);

                // Construct item information
                if (itemData.get("type").getAsString().equals("Charm"))
                    continue;

                String itemName = itemData.get("name").getAsString();

                String itemType = itemData.get("type").getAsString();
                if (!allItemTypes.contains(itemType))
                    allItemTypes.add(itemType);

                String itemRegion = "";
                boolean hasRegion = false;
                if (itemData.has("region")) {
                    itemRegion = itemData.get("region").getAsString();
                    hasRegion = true;

                    if (!allItemRegions.contains(itemRegion))
                        allItemRegions.add(itemRegion);
                }

                String itemTier = "";
                boolean hasTier = false;
                if (itemData.has("tier")) {
                    List<String> plainSplit = Arrays.asList(itemData.get("tier").getAsString().replace("_", " ").split(" ")); // janky code to patch Event Currency appearing as Event_currency and other future similar events
                    String formattedSplit = "";
                    for (String s : plainSplit) {
                        if (s.length() > 0)
                            formattedSplit = formattedSplit + s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
                        if (plainSplit.indexOf(s) != plainSplit.size() - 1)
                            formattedSplit += " ";
                    }
                    itemTier = formattedSplit;
                    hasTier = true;

                    if (!allItemTiers.contains(itemTier))
                        allItemTiers.add(itemTier);
                }

                String itemLocation = "";
                boolean hasLocation = false;
                if (itemData.has("location")) {
                    itemLocation = itemData.get("location").getAsString();
                    hasLocation = true;

                    if (!allItemLocations.contains(itemLocation))
                        allItemLocations.add(itemLocation);
                }

                String itemBaseItem = itemData.get("base_item").getAsString();
                System.out.println(itemBaseItem + " | " + (!allItemBaseItems.contains(itemBaseItem) ? "Added" : ""));
                if (!allItemBaseItems.contains(itemBaseItem))
                    allItemBaseItems.add(itemBaseItem);

                String itemLore = "";
                if (itemData.has("lore")) {
                    itemLore = itemData.get("lore").getAsString();
                }

                ArrayList<ItemStat> itemStats = new ArrayList<>();
                JsonObject statObject = itemData.get("stats").getAsJsonObject();
                for (String statKey : statObject.keySet()) {
                    if (ItemFormatter.isHiddenStat(statKey)) continue;

                    itemStats.add(new ItemStat(statKey, statObject.get(statKey).getAsDouble()));

                    if (!allItemStats.contains(statKey))
                        allItemStats.add(statKey);
                }

                // Build the item
                if (itemData.has("masterwork")) {
                    boolean hasItem = false;
                    for (DictionaryItem dictionaryItem : items) {
                        if (dictionaryItem.name.equals(itemName)) {
                            hasItem = true;
                            dictionaryItem.addMasterworkTier(itemStats, itemData.get("masterwork").getAsInt());
                        }
                    }
                    if (!hasItem) {
                        ArrayList<ArrayList<ItemStat>> totalList = new ArrayList<>();
                        for (int i = 0; i < ItemFormatter.getMasterworkForRarity(itemTier) + 1; i ++)
                            totalList.add(null);
                        totalList.set(itemData.get("masterwork").getAsInt(), itemStats);
                        items.add(new DictionaryItem(itemName, itemType, itemRegion, hasRegion, itemTier, hasTier, itemLocation, hasLocation, itemBaseItem, itemLore, totalList, true));
                    }
                } else {
                    ArrayList<ArrayList<ItemStat>> totalList = new ArrayList<>();
                    totalList.add(itemStats);
                    items.add(new DictionaryItem(itemName, itemType, itemRegion, hasRegion, itemTier, hasTier, itemLocation, hasLocation, itemBaseItem, itemLore, totalList, false));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            itemLoadFailed = true;
        }

        items.sort((o1, o2) -> {
            if (!o1.region.equals(o2.region)) {
                return -(ItemFormatter.getNumberForRegion(o1.region) - ItemFormatter.getNumberForRegion(o2.region));
            } else if (!o1.tier.equals(o2.tier)) {
                return -(ItemFormatter.getNumberForTier(o1.tier) - ItemFormatter.getNumberForTier(o2.tier));
            }
            return 0;
        });
    }

    public void loadCharms() {
        allCharmRegions = new ArrayList<>();
        allCharmTiers = new ArrayList<>();
        allCharmLocations = new ArrayList<>();
        allCharmSkillMods = new ArrayList<>();
        allCharmClasses = new ArrayList<>();
        allCharmStats = new ArrayList<>();
        allCharmBaseItems = new ArrayList<>();

        try {
            String rawData = readItemData();

            charms.clear();
            JsonObject data = new Gson().fromJson(rawData, JsonObject.class);
            for (String key : data.keySet()) {
                JsonObject charmData = data.getAsJsonObject(key);

                // Construct charm information
                if (!charmData.get("type").getAsString().equals("Charm"))
                    continue;

                String charmName = charmData.get("name").getAsString();

                String charmRegion = "Architect's Ring";
                if (!allCharmRegions.contains(charmRegion))
                    allCharmRegions.add(charmRegion);

                String charmLocation = charmData.get("location").getAsString();
                if (!allCharmLocations.contains(charmLocation))
                    allCharmLocations.add(charmLocation);

                String charmTier = charmData.get("tier").getAsString().replace("_", " ");
                if (!allCharmTiers.contains(charmTier))
                    allCharmTiers.add(charmTier);

                int charmPower = charmData.get("power").getAsInt();

                String charmClass = charmData.get("class_name").getAsString();
                if (!allCharmClasses.contains(charmClass))
                    allCharmClasses.add(charmClass);

                String charmBaseItem = charmData.get("base_item").getAsString();
                if (!allCharmBaseItems.contains(charmBaseItem))
                    allCharmBaseItems.add(charmBaseItem);

                ArrayList<CharmStat> charmStats = new ArrayList<>();
                JsonObject statObject = charmData.get("stats").getAsJsonObject();
                for (String statKey : statObject.keySet()) {
                    String skillMod = ItemFormatter.getSkillFromCharmStat(statKey);
                    if (!allCharmSkillMods.contains(skillMod))
                        allCharmSkillMods.add(skillMod);

                    charmStats.add(new CharmStat(statKey, skillMod, statObject.get(statKey).getAsDouble()));

                    if (!allCharmStats.contains(statKey))
                        allCharmStats.add(statKey);
                }

                charms.add(new DictionaryCharm(charmName, charmRegion, charmLocation, charmTier, charmPower, charmClass, charmBaseItem, charmStats));
            }
        } catch (Exception e) {
            e.printStackTrace();
            charmLoadFailed = true;
        }

        charms.sort((o1, o2) -> {
            if (!o1.tier.equals(o2.tier)) {
                return -(ItemFormatter.getNumberForTier(o1.tier) - ItemFormatter.getNumberForTier(o2.tier));
            }
            return 0;
        });
    }

    public ArrayList<String> getAllItemTypes() {
        return allItemTypes;
    }

    public ArrayList<String> getAllItemRegions() {
        return allItemRegions;
    }

    public ArrayList<String> getAllItemTiers() {
        return allItemTiers;
    }

    public ArrayList<String> getAllItemLocations() {
        return allItemLocations;
    }

    public ArrayList<String> getAllItemStats() {
        return allItemStats;
    }

    public ArrayList<String> getAllItemBaseItems() {
        return allItemBaseItems;
    }

    public void setItemNameFilter(String nameFilter) {
        this.itemNameFilter = nameFilter;
        hasItemNameFilter = true;
    }

    public void clearItemNameFilter() {
        hasItemNameFilter = false;
    }

    public void updateItemFilters(ArrayList<Filter> filters) {
        itemFilters = new ArrayList<>(filters);
    }

    public void resetItemFilters() {
        itemFilters = new ArrayList<>();
    }

    public ArrayList<String> getAllCharmRegions() {
        return allCharmRegions;
    }

    public ArrayList<String> getAllCharmTiers() {
        return allCharmTiers;
    }

    public ArrayList<String> getAllCharmLocations() {
        return allCharmLocations;
    }

    public ArrayList<String> getAllCharmSkillMods() {
        return allCharmSkillMods;
    }

    public ArrayList<String> getAllCharmClasses() {
        return allCharmClasses;
    }

    public ArrayList<String> getAllCharmStats() {
        return allCharmStats;
    }

    public ArrayList<String> getAllCharmBaseItems() {
        return allCharmBaseItems;
    }

    public void setCharmNameFilter(String nameFilter) {
        this.charmNameFilter = nameFilter;
        hasCharmNameFilter = true;
    }

    public void clearCharmNameFilter() {
        hasCharmNameFilter = false;
    }

    public void updateCharmFilters(ArrayList<Filter> filters) {
        charmFilters = new ArrayList<>(filters);
    }

    public void resetCharmFilters() {
        charmFilters = new ArrayList<>();
    }

    public void refreshItems() {
        ArrayList<DictionaryItem> filteredItems = new ArrayList<>(items);

        for (Filter filter : itemFilters) {
            if (filter != null) {
                if (filter.getOption().equals("Stat")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredItems.removeIf(i -> !i.hasStat(filter.value));
                            case 1 -> filteredItems.removeIf(i -> i.hasStat(filter.value));
                            case 2 -> filteredItems.removeIf(i -> !i.hasStat(filter.value) || !(i.getStat(filter.value) >= filter.constant));
                            case 3 -> filteredItems.removeIf(i -> !i.hasStat(filter.value) || !(i.getStat(filter.value) > filter.constant));
                            case 4 -> filteredItems.removeIf(i -> !i.hasStat(filter.value) || !(i.getStat(filter.value) == filter.constant));
                            case 5 -> filteredItems.removeIf(i -> !i.hasStat(filter.value) || !(i.getStat(filter.value) <= filter.constant));
                            case 6 -> filteredItems.removeIf(i -> !i.hasStat(filter.value) || !(i.getStat(filter.value) < filter.constant));
                        }
                    }
                } else if (filter.getOption().equals("Tier")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredItems.removeIf(i -> !i.hasTier || !i.tier.equals(filter.value));
                            case 1 -> filteredItems.removeIf(i -> i.hasTier && i.tier.equals(filter.value));
                        }
                    }
                } else if (filter.getOption().equals("Region")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredItems.removeIf(i -> !i.hasRegion || !i.region.equals(filter.value));
                            case 1 -> filteredItems.removeIf(i -> i.hasRegion && i.region.equals(filter.value));
                        }
                    }
                } else if (filter.getOption().equals("Type")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredItems.removeIf(i -> !i.type.equals(filter.value));
                            case 1 -> filteredItems.removeIf(i -> i.type.equals(filter.value));
                        }
                    }
                } else if (filter.getOption().equals("Location")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredItems.removeIf(i -> !i.hasLocation || !i.location.equals(filter.value));
                            case 1 -> filteredItems.removeIf(i -> i.hasLocation && i.location.equals(filter.value));
                        }
                    }
                } else if (filter.getOption().equals("Base Item")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredItems.removeIf(i -> !i.baseItem.equals(filter.value));
                            case 1 -> filteredItems.removeIf(i -> i.baseItem.equals(filter.value));
                        }
                    }
                }
            }
        }

        if (hasItemNameFilter)
            filteredItems.removeIf(i -> !i.name.toLowerCase().contains(itemNameFilter.toLowerCase()));

        filteredItems.sort((o1, o2) -> {
            for (Filter f : itemFilters) {
                if (f.getOption().equals("Stat")) {
                    if (o1.getStat(f.value) == o2.getStat(f.value))
                        continue;

                    double val = o1.getStat(f.value) - o2.getStat(f.value);

                    if (val > 0) return -1;
                    if (val < 0) return 1;
                }
            }
            return 0;
        });

        validItems = filteredItems;
    }

    public void refreshCharms() {
        ArrayList<DictionaryCharm> filteredCharms = new ArrayList<>(charms);

        for (Filter filter : charmFilters) {
            if (filter != null) {
                if (filter.getOption().equals("Stat")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredCharms.removeIf(i -> !i.hasStat(filter.value));
                            case 1 -> filteredCharms.removeIf(i -> i.hasStat(filter.value));
                            case 2 -> filteredCharms.removeIf(i -> !i.hasStat(filter.value) || !(i.getStat(filter.value) >= filter.constant));
                            case 3 -> filteredCharms.removeIf(i -> !i.hasStat(filter.value) || !(i.getStat(filter.value) > filter.constant));
                            case 4 -> filteredCharms.removeIf(i -> !i.hasStat(filter.value) || !(i.getStat(filter.value) == filter.constant));
                            case 5 -> filteredCharms.removeIf(i -> !i.hasStat(filter.value) || !(i.getStat(filter.value) <= filter.constant));
                            case 6 -> filteredCharms.removeIf(i -> !i.hasStat(filter.value) || !(i.getStat(filter.value) < filter.constant));
                        }
                    }
                } else if (filter.getOption().equals("Tier")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredCharms.removeIf(i -> !i.tier.equals(filter.value));
                            case 1 -> filteredCharms.removeIf(i -> i.tier.equals(filter.value));
                        }
                    }
                } else if (filter.getOption().equals("Class")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredCharms.removeIf(i -> !i.className.equals(filter.value));
                            case 1 -> filteredCharms.removeIf(i -> i.className.equals(filter.value));
                        }
                    }
                } else if (filter.getOption().equals("Skill Modifier")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredCharms.removeIf(i -> !i.hasStatModifier(filter.value));
                            case 1 -> filteredCharms.removeIf(i -> i.hasStatModifier(filter.value));
                        }
                    }
                } else if (filter.getOption().equals("Charm Power")) {
                    switch (filter.comparator) {
                        case 2 -> filteredCharms.removeIf(i -> !(i.power >= filter.constant));
                        case 3 -> filteredCharms.removeIf(i -> !(i.power > filter.constant));
                        case 4 -> filteredCharms.removeIf(i -> !(i.power == filter.constant));
                        case 5 -> filteredCharms.removeIf(i -> !(i.power < filter.constant));
                        case 6 -> filteredCharms.removeIf(i -> !(i.power <= filter.constant));
                    }
                } else if (filter.getOption().equals("Location")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredCharms.removeIf(i -> !i.location.equals(filter.value));
                            case 1 -> filteredCharms.removeIf(i -> i.location.equals(filter.value));
                        }
                    }
                } else if (filter.getOption().equals("Base Item")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredCharms.removeIf(i -> !i.baseItem.equals(filter.value));
                            case 1 -> filteredCharms.removeIf(i -> i.baseItem.equals(filter.value));
                        }
                    }
                }
            }
        }

        if (hasCharmNameFilter)
            filteredCharms.removeIf(i -> !i.name.toLowerCase().contains(charmNameFilter.toLowerCase()));

        filteredCharms.sort((o1, o2) -> {
            for (Filter f : charmFilters) {
                if (f.getOption().equals("Stat")) {
                    if (o1.getStat(f.value) == o2.getStat(f.value))
                        continue;

                    double val = o1.getStat(f.value) - o2.getStat(f.value);

                    if (val > 0) return -1;
                    if (val < 0) return 1;
                }
            }
            return 0;
        });

        validCharms = filteredCharms;
    }

    public ArrayList<DictionaryItem> getItems() {
        return validItems;
    }

    public ArrayList<DictionaryCharm> getCharms() {
        return validCharms;
    }
}
