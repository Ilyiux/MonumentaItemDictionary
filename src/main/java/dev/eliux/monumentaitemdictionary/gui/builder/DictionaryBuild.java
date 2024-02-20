package dev.eliux.monumentaitemdictionary.gui.builder;

import dev.eliux.monumentaitemdictionary.gui.charm.DictionaryCharm;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;

import java.util.Arrays;
import java.util.List;

public class DictionaryBuild {
    public final int id;
    public DictionaryItem itemOnButton;
    public String className;
    public String specialization;
    public String region;
    public String name;
    public DictionaryItem mainhand;
    public DictionaryItem offhand;
    public DictionaryItem head;
    public DictionaryItem chestplate;
    public DictionaryItem leggings;
    public DictionaryItem boots;
    public List<DictionaryCharm> charms;
    public List<DictionaryItem> allItems;
    public boolean favorite;
    public DictionaryBuild(String name, List<DictionaryItem> items, List<DictionaryCharm> charms, DictionaryItem itemOnBuildButton, String region, String className, String specialization, boolean favorite, int id) {
        this.name = name;
        this.mainhand = items.get(0);
        this.offhand = items.get(1);
        this.head = items.get(2);
        this.chestplate = items.get(3);
        this.leggings = items.get(4);
        this.boots = items.get(5);
        this.charms = charms;
        this.itemOnButton = itemOnBuildButton;
        this.region = region;
        this.className = className;
        this.specialization = specialization;
        this.favorite = favorite;
        this.id = id;

        allItems = Arrays.asList(mainhand, offhand, head, chestplate, leggings, boots);
    }
}
