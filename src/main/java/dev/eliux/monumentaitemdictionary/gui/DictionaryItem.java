package dev.eliux.monumentaitemdictionary.gui;

import dev.eliux.monumentaitemdictionary.util.ItemStat;

import java.util.ArrayList;

public class DictionaryItem {
    public String name; // will exist
    public String type; // will exist
    public String region;
    public boolean hasRegion;
    public String tier;
    public boolean hasTier;
    public String location;
    public boolean hasLocation;
    public String baseItem; // will exist
    public String lore; // will exist
    public ArrayList<ArrayList<ItemStat>> stats; // will exist

    public boolean hasMasterwork;

    public DictionaryItem(String name, String type, String region, boolean hasRegion, String tier, boolean hasTier, String location, boolean hasLocation, String baseItem, String lore, ArrayList<ArrayList<ItemStat>> stats, boolean hasMasterwork) {
        this.name = name;
        this.type = type;
        this.region = region;
        this.hasRegion = hasRegion;
        this.tier = tier;
        this.hasTier = hasTier;
        this.location = location;
        this.hasLocation = hasLocation;
        this.baseItem = baseItem;
        this.lore = lore;
        this.stats = stats;
        this.hasMasterwork = hasMasterwork;
    }

    public void addMasterworkTier(ArrayList<ItemStat> newStats, int tier) {
        stats.set(tier, newStats);
    }

    public ArrayList<ItemStat> getNonMasterwork() {
        return stats.get(0);
    }

    public ArrayList<ItemStat> getMasterworkTier(int tier) {
        return stats.get(tier);
    }

    public int getMinMasterwork() {
        if (hasMasterwork) {
            for (int i = 0; i < stats.size(); i ++) {
                if (stats.get(i) != null) {
                    return i;
                }
            }
        }
        return 0;
    }

    public int getMaxMasterwork() {
        if (hasMasterwork) {
            return stats.size();
        }
        return 0;
    }

    public boolean hasStat(String stat) {
        for (ArrayList<ItemStat> itemStatsList : stats) {
            if (itemStatsList == null) continue;
            for (ItemStat itemStat : itemStatsList) {
                if (itemStat.statName.equals(stat)) {
                    return true;
                }
            }
        }
        return false;
    }

    public double getStat(String stat) {
        double highest = -1.0;
        for (ArrayList<ItemStat> itemStatsList : stats) {
            if (itemStatsList == null) continue;
            for (ItemStat itemStat : itemStatsList) {
                if (itemStat.statName.equals(stat) && itemStat.statValue > highest) {
                    highest = itemStat.statValue;
                }
            }
        }
        return highest;
    }
}
