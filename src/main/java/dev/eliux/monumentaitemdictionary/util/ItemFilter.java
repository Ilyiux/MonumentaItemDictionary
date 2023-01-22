package dev.eliux.monumentaitemdictionary.util;

public class ItemFilter {
    public String option;
    public String value;
    public int comparator;
    public double constant;

    // comparator info:
    // 0 -> Exists
    // 1 -> Not Exists
    // 2 -> >=
    // 3 -> >
    // 4 -> =
    // 5 -> <=
    // 6 -> <

    public ItemFilter() {
        this.option = "";
        this.value = "";
        this.comparator = 0;
        this.constant = 0.0;
    }

    public ItemFilter(String option, String value, int comparator, double constant) {
        this.option = option;
        this.value = value;
        this.comparator = comparator;
        this.constant = constant;
    }

    public void incrementComparator() {
        comparator ++;
        if (option.equals("Stat")) {
            if (comparator >= 7) comparator = 0;
        } else {
            if (comparator >= 2) comparator = 0;
        }
    }
}
