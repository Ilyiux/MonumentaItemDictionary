package dev.eliux.monumentaitemdictionary.gui;

import com.google.gson.*;
import dev.eliux.monumentaitemdictionary.util.ItemFormatter;
import dev.eliux.monumentaitemdictionary.util.ItemStat;
import dev.eliux.monumentaitemdictionary.web.WebManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DictionaryController {
    private ArrayList<String> statFilters = new ArrayList<>();
    private String nameFilter;
    private boolean hasNameFilter = false;
    private ArrayList<String> regionFilters = new ArrayList<>();
    private ArrayList<String> typeFilters = new ArrayList<>();
    private ArrayList<String> tierFilters = new ArrayList<>();
    private ArrayList<String> locationFilters = new ArrayList<>();

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
    public ItemSortMenuGui sortGui;
    private boolean sortGuiPreviouslyOpened = false;

    public DictionaryController() {
        items = new ArrayList<>();
        validItems = new ArrayList<>();

        loadItems();

        itemGui = new ItemDictionaryGui(new LiteralText("Monumenta Item Dictionary"), this);
        sortGui = new ItemSortMenuGui(new LiteralText("Item Sort Menu"), this);
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

    public void setSortScreen() {
        MinecraftClient.getInstance().setScreen(sortGui);
        if (!sortGuiPreviouslyOpened) {
            sortGui.postInit();
            sortGuiPreviouslyOpened = true;
        } else {
            sortGui.updateGuiPositions();
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
            String data = WebManager.getRequest("https://www.ohthemisery.tk/api/items");

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
                String itemRegion = itemData.get("region").getAsString();
                String itemTier = itemData.get("tier").getAsString();
                String itemLocation = itemData.get("location").getAsString();
                String itemBaseItem = itemData.get("base_item").getAsString();
                String itemOriginalItem = "";
                if (itemData.has("original_item")) {
                    itemOriginalItem = itemData.get("original_item").getAsString();
                }

                if (itemType.equals("Charm"))
                    continue;

                if (!allTypes.contains(itemType))
                    allTypes.add(itemType);
                if (!allRegions.contains(itemRegion))
                    allRegions.add(itemRegion);
                if (!allTiers.contains(itemTier))
                    allTiers.add(itemTier);
                if (!allLocations.contains(itemLocation))
                    allLocations.add(itemLocation);

                ArrayList<ItemStat> itemStats = new ArrayList<>();
                JsonObject statObject = itemData.get("stats").getAsJsonObject();
                for (String statKey : statObject.keySet()) {
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
                        items.add(new DictionaryItem(itemName, itemType, itemRegion, itemTier, itemLocation, itemBaseItem, itemOriginalItem, totalList, true));
                    }
                } else {
                    ArrayList<ArrayList<ItemStat>> totalList = new ArrayList<>();
                    totalList.add(itemStats);
                    items.add(new DictionaryItem(itemName, itemType, itemRegion, itemTier, itemLocation, itemBaseItem, itemOriginalItem, totalList, false));
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

    public void addTypeFilter(String typeFilter) {
        typeFilters.add(typeFilter);
    }

    public void removeTypeFilter(String typeFilter) {
        typeFilters.remove(typeFilter);
    }

    public void addRegionFilter(String regionFilter) {
        regionFilters.add(regionFilter);
    }

    public void removeRegionFilter(String regionFilter) {
        regionFilters.remove(regionFilter);
    }

    public void addTierFilter(String tierFilter) {
        tierFilters.add(tierFilter);
    }

    public void removeTierFilter(String tierFilter) {
        tierFilters.remove(tierFilter);
    }

    public void addLocationFilter(String locationFilter) {
        locationFilters.add(locationFilter);
    }

    public void removeLocationFilter(String locationFilter) {
        locationFilters.remove(locationFilter);
    }

    public void addStatFilter(String statFilter) {
        statFilters.add(statFilter);
    }

    public void removeStatFilter(String statFilter) {
        statFilters.remove(statFilter);
    }

    public void resetAllFilters() {
        typeFilters.clear();
        regionFilters.clear();
        tierFilters.clear();
        locationFilters.clear();
        statFilters.clear();
    }

    public void refreshItems() {
        ArrayList<DictionaryItem> filteredItems = new ArrayList<>(items);


        if (hasNameFilter)
            filteredItems.removeIf(i -> !i.name.toLowerCase().contains(nameFilter.toLowerCase()));

        if (typeFilters.size() > 0) {
            filteredItems.removeIf(i -> !typeFilters.contains(i.type));
        }

        if (regionFilters.size() > 0) {
            filteredItems.removeIf(i -> !regionFilters.contains(i.region));
        }

        if (tierFilters.size() > 0) {
            filteredItems.removeIf(i -> !tierFilters.contains(i.tier));
        }

        if (locationFilters.size() > 0) {
            filteredItems.removeIf(i -> !locationFilters.contains(i.location));
        }

        // sort by first stat and sub-sort for all other stats
        for (String stat : statFilters) {
            filteredItems.removeIf(i -> !i.hasStat(stat));
        }

        filteredItems.sort((o1, o2) -> {
            for (String stat : statFilters) {
                if (o1.getStat(stat) == o2.getStat(stat))
                    continue;

                double val = o1.getStat(stat) - o2.getStat(stat);

                if (val > 0) return -1;
                if (val < 0) return 1;
            }
            return 0;
        });

        validItems = filteredItems;
    }

    public ArrayList<DictionaryItem> getItems() {
        return validItems;
    }
}
