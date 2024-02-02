package dev.eliux.monumentaitemdictionary.gui.builder;

public class DictionaryBuild {
    public String name;
    public String mainHand;
    public String offHand;
    public String head;
    public String chestplate;
    public String leggings;
    public String boots;
    public String[] charms;
    public DictionaryBuild(String name, String mainHand, String offHand, String head, String chestplate, String leggings, String boots, String[] charms) {
        this.name = name;
        this.mainHand = mainHand;
        this.offHand = offHand;
        this.head = head;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.charms = charms;
    }
}
