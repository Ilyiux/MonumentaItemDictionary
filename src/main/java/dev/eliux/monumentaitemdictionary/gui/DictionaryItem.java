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
    public ArrayList<ItemStat> stats;

    public DictionaryItem(String name, String type, String region, String tier, String location, String baseItem, String originalItem, ArrayList<ItemStat> stats) {
        this.name = name;
        this.type = type;
        this.region = region;
        this.tier = tier;
        this.location = location;
        this.baseItem = baseItem;
        this.originalItem = originalItem;
        this.stats = stats;
    }

    public boolean hasStat(String stat) {
        for (ItemStat itemStat : stats) {
            if (itemStat.statName.equals(stat)) {
                return true;
            }
        }
        return false;
    }

    public double getStat(String stat) {
        for (ItemStat itemStat : stats) {
            if (itemStat.statName.equals(stat)) {
                return itemStat.statValue;
            }
        }
        return -1.0;
    }
}
