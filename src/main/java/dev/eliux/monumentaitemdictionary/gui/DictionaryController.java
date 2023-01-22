package dev.eliux.monumentaitemdictionary.gui;

import com.google.gson.*;
import dev.eliux.monumentaitemdictionary.util.ItemFilter;
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

public class DictionaryController {
    private String nameFilter;
    private boolean hasNameFilter = false;
    private ArrayList<ItemFilter> itemFilters = new ArrayList<>();

    private ArrayList<String> allTypes;
    private ArrayList<String> allRegions;
    private ArrayList<String> allTiers;
    private ArrayList<String> allLocations;
    private ArrayList<String> allStats;

    private final ArrayList<DictionaryItem> items;
    private ArrayList<DictionaryItem> validItems;

    public boolean itemLoadFailed = false;

    public ItemDictionaryGui itemGui;
    private boolean itemGuiPreviouslyOpened = false;
    public ItemFilterGui filterGui;
    private boolean filterGuiPreviouslyOpened = false;

    public DictionaryController() {
        items = new ArrayList<>();
        validItems = new ArrayList<>();

        loadItems();

        itemGui = new ItemDictionaryGui(new LiteralText("Monumenta Item Dictionary"), this);
        filterGui = new ItemFilterGui(new LiteralText("Item Filter Menu"), this);
        setDictionaryScreen();
    }

    public void setDictionaryScreen() {
        MinecraftClient.getInstance().setScreen(itemGui);
        if (!itemGuiPreviouslyOpened) {
            itemGui.postInit();
            itemGuiPreviouslyOpened = true;
        } else {
            itemGui.updateGuiPositions();
        }
    }

    public void setFilterScreen() {
        MinecraftClient.getInstance().setScreen(filterGui);
        if (!filterGuiPreviouslyOpened) {
            filterGui.postInit();
            filterGuiPreviouslyOpened = true;
        } else {
            //filterGui.updateGuiPositions();
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

    public void requestItemsAndUpdate() {
        try {
            String data = WebManager.getRequest("https://api.playmonumenta.com/items");

            writeItemData(data);
            loadItems();
            itemGui.buildItemList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadItems() {
        allTypes = new ArrayList<>();
        allRegions = new ArrayList<>();
        allTiers = new ArrayList<>();
        allLocations = new ArrayList<>();
        allStats = new ArrayList<>();

        try {
            String rawData = readItemData();

            items.clear();
            JsonObject data = new Gson().fromJson(rawData, JsonObject.class);
            for (String key : data.keySet()) {
                JsonObject itemData = data.getAsJsonObject(key);

                // Construct item information
                String itemName = itemData.get("name").getAsString();

                String itemType = itemData.get("type").getAsString();
                if (!allTypes.contains(itemType))
                    allTypes.add(itemType);

                // charms are not handled yet
                if (itemType.equals("Charm"))
                    continue;

                String itemRegion = "";
                boolean hasRegion = false;
                if (itemData.has("region")) {
                    itemRegion = itemData.get("region").getAsString();
                    hasRegion = true;

                    if (!allRegions.contains(itemRegion))
                        allRegions.add(itemRegion);
                }

                String itemTier = "";
                boolean hasTier = false;
                if (itemData.has("tier")) {
                    itemTier = itemData.get("tier").getAsString();
                    hasTier = true;

                    if (!allTiers.contains(itemTier))
                        allTiers.add(itemTier);
                }

                String itemLocation = "";
                boolean hasLocation = false;
                if (itemData.has("location")) {
                    itemLocation = itemData.get("location").getAsString();
                    hasLocation = true;

                    if (!allLocations.contains(itemLocation))
                        allLocations.add(itemLocation);
                }

                String itemBaseItem = itemData.get("base_item").getAsString();

                String itemLore = "";
                if (itemData.has("lore")) {
                    itemLore = itemData.get("lore").getAsString();
                }

                ArrayList<ItemStat> itemStats = new ArrayList<>();
                JsonObject statObject = itemData.get("stats").getAsJsonObject();
                for (String statKey : statObject.keySet()) {
                    if (ItemFormatter.isHiddenStat(statKey)) continue;

                    itemStats.add(new ItemStat(statKey, statObject.get(statKey).getAsDouble()));

                    if (!allStats.contains(statKey))
                        allStats.add(statKey);
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
    }

    public ArrayList<String> getAllTypes() {
        return allTypes;
    }

    public ArrayList<String> getAllRegions() {
        return allRegions;
    }

    public ArrayList<String> getAllTiers() {
        return allTiers;
    }

    public ArrayList<String> getAllLocations() {
        return allLocations;
    }

    public ArrayList<String> getAllStats() {
        return allStats;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
        hasNameFilter = true;
    }

    public void clearNameFilter() {
        hasNameFilter = false;
    }

    public void updateFilters(ArrayList<ItemFilter> filters) {
        itemFilters = new ArrayList<>(filters);
    }

    public void resetFilters() {
        itemFilters = new ArrayList<>();
    }

    public void refreshItems() {
        ArrayList<DictionaryItem> filteredItems = new ArrayList<>(items);

        for (ItemFilter filter : itemFilters) {
            if (filter != null) {
                if (filter.option.equals("Stat")) {
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
                } else if (filter.option.equals("Tier")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredItems.removeIf(i -> !i.hasTier || !i.tier.equals(filter.value));
                            case 1 -> filteredItems.removeIf(i -> i.hasTier && i.tier.equals(filter.value));
                        }
                    }
                } else if (filter.option.equals("Region")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredItems.removeIf(i -> !i.hasRegion || !i.region.equals(filter.value));
                            case 1 -> filteredItems.removeIf(i -> i.hasRegion && i.region.equals(filter.value));
                        }
                    }
                } else if (filter.option.equals("Type")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredItems.removeIf(i -> !i.type.equals(filter.value));
                            case 1 -> filteredItems.removeIf(i -> i.type.equals(filter.value));
                        }
                    }
                } else if (filter.option.equals("Location")) {
                    if (!filter.value.equals("")) {
                        switch (filter.comparator) {
                            case 0 -> filteredItems.removeIf(i -> !i.hasLocation || !i.location.equals(filter.value));
                            case 1 -> filteredItems.removeIf(i -> i.hasLocation && i.location.equals(filter.value));
                        }
                    }
                }
            }
        }

        if (hasNameFilter)
            filteredItems.removeIf(i -> !i.name.toLowerCase().contains(nameFilter.toLowerCase()));

        validItems = filteredItems;
    }

    public ArrayList<DictionaryItem> getItems() {
        return validItems;
    }
}
