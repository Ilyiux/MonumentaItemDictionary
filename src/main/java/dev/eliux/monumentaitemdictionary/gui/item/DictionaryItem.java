package dev.eliux.monumentaitemdictionary.gui.item;

import dev.eliux.monumentaitemdictionary.util.ItemFormatter;
import dev.eliux.monumentaitemdictionary.util.ItemStat;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class DictionaryItem implements Comparable<DictionaryItem> {
    public String name; // will exist
    public String type; // will exist
    public String region;
    public String tier;
    public String location;
    public int fishTier;
    public boolean isFish;
    public String baseItem; // will exist
    public String lore; // will exist
    public ArrayList<String> nbt; // will exist
    public ArrayList<ArrayList<ItemStat>> stats; // will exist

    public boolean hasMasterwork;

    public DictionaryItem(String name, String type, String region, String tier, String location, int fishTier, boolean isFish, String baseItem, String lore, ArrayList<String> nbt, ArrayList<ArrayList<ItemStat>> stats, boolean hasMasterwork) {
        this.name = name;
        this.type = type;
        this.region = region;
        this.tier = tier;
        this.location = location;
        this.fishTier = fishTier;
        this.isFish = isFish;
        this.baseItem = baseItem;
        this.lore = lore;
        this.nbt = nbt;
        this.stats = stats;
        this.hasMasterwork = hasMasterwork;
    }

    public boolean hasRegion() {
        return !region.isEmpty();
    }

    public boolean hasTier() {
        return !tier.isEmpty();
    }

    public boolean hasLocation() {
        return !location.isEmpty();
    }

    public void addMasterworkTier(ArrayList<ItemStat> newStats, String newNbt, int tier) {
        stats.set(tier, newStats);
        nbt.set(tier, newNbt);
    }

    public ArrayList<ItemStat> getStatsNoMasterwork() {
        return stats.get(0);
    }

    public ArrayList<ItemStat> getStatsFromMasterwork(int tier) {
        return stats.get(tier);
    }

    public String getNbtNoMasterwork() {
        return nbt.get(0);
    }

    public String getNbtFromMasterwork(int tier) {
        return nbt.get(tier);
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

    @Override
    public int compareTo(@NotNull DictionaryItem o) {
        int regionComparison = ItemFormatter.getNumberForRegion(o.region) - ItemFormatter.getNumberForRegion(this.region);
        if (regionComparison != 0) {
            return regionComparison;
        }

        int tierComparison = ItemFormatter.getNumberForTier(o.tier) - ItemFormatter.getNumberForTier(this.tier);
        if (tierComparison != 0) {
            return tierComparison;
        }

        return name.compareTo(o.name);
    }
}
