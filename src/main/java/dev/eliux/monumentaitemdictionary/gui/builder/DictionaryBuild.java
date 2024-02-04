package dev.eliux.monumentaitemdictionary.gui.builder;

import dev.eliux.monumentaitemdictionary.gui.charm.DictionaryCharm;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;

import java.util.Arrays;
import java.util.List;

public class DictionaryBuild {
    public String name;
    public DictionaryItem mainhand;
    public DictionaryItem offhand;
    public DictionaryItem head;
    public DictionaryItem chestplate;
    public DictionaryItem leggings;
    public DictionaryItem boots;
    public List<DictionaryCharm> charms;
    public List<DictionaryItem> allItems;
    public DictionaryBuild(String name, DictionaryItem mainhand, DictionaryItem offhand, DictionaryItem head, DictionaryItem chestplate, DictionaryItem leggings, DictionaryItem boots, List<DictionaryCharm> charms) {
        this.name = name;
        this.mainhand = mainhand;
        this.offhand = offhand;
        this.head = head;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.charms = charms;

        allItems = Arrays.asList(mainhand, offhand, head, chestplate, leggings, boots);
    }
}
