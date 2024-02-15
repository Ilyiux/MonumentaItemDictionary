package dev.eliux.monumentaitemdictionary.gui.builder;

import dev.eliux.monumentaitemdictionary.gui.charm.DictionaryCharm;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;

import java.util.Arrays;
import java.util.List;

public class DictionaryBuild {
    public final DictionaryItem itemOnButton;
    public String name;
    public DictionaryItem mainhand;
    public DictionaryItem offhand;
    public DictionaryItem head;
    public DictionaryItem chestplate;
    public DictionaryItem leggings;
    public DictionaryItem boots;
    public List<DictionaryCharm> charms;
    public List<DictionaryItem> allItems;
    public DictionaryBuild(String name, List<DictionaryItem> items, List<DictionaryCharm> charms, DictionaryItem itemOnBuildButton) {
        this.name = name;
        this.mainhand = items.get(0);
        this.offhand = items.get(1);
        this.head = items.get(2);
        this.chestplate = items.get(3);
        this.leggings = items.get(4);
        this.boots = items.get(5);
        this.charms = charms;
        this.itemOnButton = itemOnBuildButton;

        allItems = Arrays.asList(mainhand, offhand, head, chestplate, leggings, boots);
    }


}
