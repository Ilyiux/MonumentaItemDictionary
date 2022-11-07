package dev.eliux.monumentaitemdictionary.gui;

import com.google.gson.*;
import dev.eliux.monumentaitemdictionary.util.ItemStat;
import dev.eliux.monumentaitemdictionary.web.WebManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;

public class DictionaryController {
    private ArrayList<String> statFilters = new ArrayList<>();
    private boolean hasNameFilter = false;
    private String nameFilter;
    private boolean hasRegionFilter = false;
    private String regionFilter;
    private boolean hasTypeFilter = false;
    private String typeFilter;
    private boolean hasTierFilter = false;
    private String tierFilter;
    private boolean hasLocationFilter = false;
    private String locationFilter;

    private final ArrayList<DictionaryItem> items;
    private ArrayList<DictionaryItem> validItems;

    public boolean itemLoadFailed = false;

    private ItemDictionaryGui gui;

    public DictionaryController() {
        items = new ArrayList<>();
        validItems = new ArrayList<>();

        loadItems();

        gui = new ItemDictionaryGui(new LiteralText("Monumenta Item Dictionary"), this);
        MinecraftClient.getInstance().setScreen(gui);
        gui.postInit();
    }

    public void loadItems() {
        try {
            String rawData = "";
            rawData = WebManager.getRequest("https://www.ohthemisery.tk/api/items");

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

                ArrayList<ItemStat> itemStats = new ArrayList<>();
                JsonObject statObject = itemData.get("stats").getAsJsonObject();
                for (String statKey : statObject.keySet()) {
                    itemStats.add(new ItemStat(statKey, statObject.get(statKey).getAsDouble()));
                }

                // Build the item
                items.add(new DictionaryItem(itemName, itemType, itemRegion, itemTier, itemLocation, itemBaseItem, itemOriginalItem, itemStats));
            }
        } catch (Exception e) {
            e.printStackTrace();
            itemLoadFailed = true;
        }
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
        hasNameFilter = true;
    }

    public void clearNameFilter() {
        hasNameFilter = false;
    }

    public void setTypeFilter(String typeFilter) {
        this.typeFilter = typeFilter;
        hasTypeFilter = false;
    }

    public void clearTypeFilter() {
        hasTypeFilter = false;
    }

    public void setRegionFilter(String regionFilter) {
        this.regionFilter = regionFilter;
        hasRegionFilter = true;
    }

    public void clearRegionFilter() {
        hasRegionFilter = false;
    }

    public void setTierFilter(String tierFilter) {
        this.tierFilter = tierFilter;
        hasTierFilter = true;
    }

    public void clearTierFilter() {
        hasTierFilter = false;
    }

    public void setLocationFilter(String locationFilter) {
        this.locationFilter = locationFilter;
        hasLocationFilter = true;
    }

    public void clearLocationFilter() {
        hasLocationFilter = false;
    }

    public void addStatFilter(String statFilter) {
        statFilters.add(statFilter);
    }

    public void removeStatFilter(String statFilter) {
        statFilters.remove(statFilter);
    }

    public void refreshItems() {
        ArrayList<DictionaryItem> filteredItems = new ArrayList<>(items);

        if (hasNameFilter)
            filteredItems.removeIf(i -> !i.name.toLowerCase().contains(nameFilter.toLowerCase()));

        if (hasTypeFilter)
            filteredItems.removeIf(i -> !i.type.equals(typeFilter));

        if (hasRegionFilter)
            filteredItems.removeIf(i -> !i.region.equals(regionFilter));

        if (hasTierFilter)
            filteredItems.removeIf(i -> !i.tier.equals(tierFilter));

        if (hasLocationFilter)
            filteredItems.removeIf(i -> !i.location.equals(locationFilter));

        // sort by first stat and subsort for all other stats
        for (String stat : statFilters) {
            filteredItems.removeIf(i -> !i.hasStat(stat));
        }

        filteredItems.sort((o1, o2) -> {
            for (String stat : statFilters) {
                if (o1.getStat(stat) == o2.getStat(stat))
                    continue;

                double val = o1.getStat(stat) - o2.getStat(stat);

                if (val > 0) return 1;
                if (val < 0) return -1;
            }
            return 0;
        });

        validItems = filteredItems;
    }

    public ArrayList<DictionaryItem> getItems() {
        return validItems;
    }
}
