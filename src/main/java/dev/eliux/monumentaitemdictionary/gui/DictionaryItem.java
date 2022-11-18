package dev.eliux.monumentaitemdictionary.gui;

import dev.eliux.monumentaitemdictionary.util.ItemStat;

import java.util.ArrayList;

public class DictionaryItem {
    public String name;
    public String type;
    public String region;
    public String tier;
    public String location;
    public String baseItem;
    public String originalItem;
    public ArrayList<ArrayList<ItemStat>> stats;

    public boolean hasMasterwork;

    public DictionaryItem(String name, String type, String region, String tier, String location, String baseItem, String originalItem, ArrayList<ArrayList<ItemStat>> stats, boolean hasMasterwork) {
        this.name = name;
        this.type = type;
        this.region = region;
        this.tier = tier;
        this.location = location;
        this.baseItem = baseItem;
        this.originalItem = originalItem;
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
