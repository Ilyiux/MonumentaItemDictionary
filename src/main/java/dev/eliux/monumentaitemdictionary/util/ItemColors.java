package dev.eliux.monumentaitemdictionary.util;

public class ItemColors {
    public static final int DEFAULT_COLOR = 0xFFFFFF;

    public static final int TEXT_COLOR = 0x555555;
    public static final int TEXT_ENCHANT_COLOR = 0xAAAAAA;
    public static final int TEXT_STAT_COLOR = 0x5555FF;
    public static final int TEXT_BASE_STAT_COLOR = 0x00AA00;
    public static final int TEXT_DEFENSE_COLOR = 0x55FFFF;
    public static final int TEXT_NEGATIVE_COLOR = 0xFC5454;

    public static final int EPIC_COLOR = 0xB114E0;
    public static final int ARTIFACT_COLOR = 0xCD2D28;
    public static final int RARE_COLOR = 0x49C0E2;
    public static final int UNIQUE_COLOR = 0xC6A0C6;
    public static final int PATRON_COLOR = 0x80D817;
    public static final int EVENT_COLOR = 0x7DFCD1;

    public static final int UNCOMMON_COLOR = 0xB1B5BB;
    public static final int TIER5_COLOR = 0xD7D9DD;
    public static final int TIER4_COLOR = 0x969CA4;
    public static final int TIER3_COLOR = 0x969CA4;
    public static final int TIER2_COLOR = 0x969CA4;
    public static final int TIER1_COLOR = 0x969CA4;
    public static final int TIER0_COLOR = 0x808690;

    public static final int WHITE_COLOR = 0xFCFCFC;
    public static final int ORANGE_COLOR = 0xFCA800;
    public static final int MAGENTA_COLOR = 0xFC54FC;
    public static final int LIGHTBLUE_COLOR = 0x49C0E2;
    public static final int YELLOW_COLOR = 0xFCFC54;
    public static final int LIME_COLOR = 0x54FC54;
    public static final int PINK_COLOR = 0xFC68B2;
    public static final int GRAY_COLOR = 0x545454;
    public static final int LIGHTGRAY_COLOR = 0xA8A8A8;
    public static final int CYAN_COLOR = 0x00A8A8;
    public static final int PURPLE_COLOR = 0xA800A8;
    public static final int TEAL_COLOR = 0x46B4B3;
    public static final int SHIFTING_COLOR = 0x7DFCD1;
    public static final int FORUM_COLOR = 0x7E7E00;
    public static final int TOV_COLOR = 0xC6A0C6;
    public static final int DOCKS_COLOR = 0x196281;
    public static final int CARNIVAL_COLOR = 0xCE2D27;
    public static final int DELVES_COLOR = 0xB26F27;
    public static final int RUSH_COLOR = 0xC01E55;
    public static final int HORSEMAN_COLOR = 0x8C3318;
    public static final int MIST_COLOR = 0x664B5A;
    public static final int REMORSE_COLOR = 0xEBE3D3;
    public static final int DEPTHS_COLOR = 0x5C2C85;
    public static final int ELDRASK_COLOR = 0x85CCF7;
    public static final int HEKAWT_COLOR = 0xFCB23D;
    public static final int ISLESDELVES_COLOR = 0xFFEDB8;
    public static final int ISLESCASINO_COLOR = 0xE0E2FF;
    public static final int ISLESOVERWORLD_COLOR = 0xE0EBFF;
    public static final int CELSIANISLES_COLOR = 0xF4F8FF;
    public static final int VALLEYDELVES_COLOR = 0xFFEDB8;
    public static final int ARMORY_COLOR = 0xFFEDB8;
    public static final int VALLEYCASINO_COLOR = 0xFFFAEC;
    public static final int VALLEYOVERWORLD_COLOR = 0xFFFFE8;
    public static final int KINGSVALLEY_COLOR = 0xF5FFF5;
    public static final int LOWTIDESMUGGLER_COLOR = 0xCACAE7;
    public static final int AZACOR_COLOR = 0xF6C5B6;
    public static final int LABS_COLOR = 0xB2AAC1;
    public static final int WILLOWS_COLOR = 0x006300;
    public static final int SANCTUM_COLOR = 0x51A800;
    public static final int REVERIE_COLOR = 0x780E46;
    public static final int CORRIDORS_COLOR = 0x890000;
    public static final int VERDANT_COLOR = 0x158115;
    public static final int KAUL_COLOR = 0x9ED292;
    public static final int SKT_COLOR = 0xBEBEBE;
    public static final int THEWOLFSWOOD_COLOR = 0x4B8D4C;
    public static final int BLUE_COLOR = 0x0C2BA0;
    public static final int PORTAL_COLOR = 0XD9E5E0;
    public static final int PELIASKEEP_COLOR = 0xC2B9A3;
    public static final int RUIN_COLOR = 0x316CA6;
    public static final int SANGUINEHALLS_COLOR = 0xA90000;
    public static final int QUESTREWARD_COLOR = 0xC6A0C6;
    public static final int TRANSMOGRIFIER_COLOR = 0x6E2CA6;
    public static final int ARCHITECTSRING_COLOR = 0xCDCDCD;

    public static int getColorForTier(String itemTier) {
        return switch (itemTier) {
            case "Epic":
                yield EPIC_COLOR;
            case "Artifact":
                yield ARTIFACT_COLOR;
            case "Rare":
                yield RARE_COLOR;
            case "Unique":
                yield UNIQUE_COLOR;
            case "Event":
                yield EVENT_COLOR;
            case "Patron":
                yield PATRON_COLOR;
            case "Uncommon":
                yield UNCOMMON_COLOR;
            case "Tier 5":
                yield TIER5_COLOR;
            case "Tier 4":
                yield TIER4_COLOR;
            case "Tier 3":
                yield TIER3_COLOR;
            case "Tier 2":
                yield TIER2_COLOR;
            case "Tier 1":
                yield TIER1_COLOR;
            case "Tier 0":
                yield TIER0_COLOR;
            default:
                yield DEFAULT_COLOR;
        };
    }

    public static int getColorForLocation(String itemLocation) {
        return switch (itemLocation) {
            case "White":
                yield WHITE_COLOR;
            case "Orange":
                yield ORANGE_COLOR;
            case "Magenta":
                yield MAGENTA_COLOR;
            case "Light Blue":
                yield LIGHTBLUE_COLOR;
            case "Yellow":
                yield YELLOW_COLOR;
            case "Lime":
                yield LIME_COLOR;
            case "Pink":
                yield PINK_COLOR;
            case "Gray":
                yield GRAY_COLOR;
            case "Light Gray":
                yield LIGHTGRAY_COLOR;
            case "Cyan":
                yield CYAN_COLOR;
            case "Purple":
                yield PURPLE_COLOR;
            case "Teal":
                yield TEAL_COLOR;
            case "Shifting":
                yield SHIFTING_COLOR;
            case "Forum":
                yield FORUM_COLOR;
            case "TOV":
                yield TOV_COLOR;
            case "Docks":
                yield DOCKS_COLOR;
            case "Carnival":
                yield CARNIVAL_COLOR;
            case "Delves":
                yield DELVES_COLOR;
            case "Rush":
                yield RUSH_COLOR;
            case "Horseman":
                yield HORSEMAN_COLOR;
            case "Mist":
                yield MIST_COLOR;
            case "Remorse":
                yield REMORSE_COLOR;
            case "Depths":
                yield DEPTHS_COLOR;
            case "Eldrask":
                yield ELDRASK_COLOR;
            case "Hekawt":
                yield HEKAWT_COLOR;
            case "Isles Delves":
                yield ISLESDELVES_COLOR;
            case "Isles Casino":
                yield ISLESCASINO_COLOR;
            case "Isles Overworld":
                yield ISLESOVERWORLD_COLOR;
            case "Celsian Isles":
                yield CELSIANISLES_COLOR;
            case "Valley Delves":
                yield VALLEYDELVES_COLOR;
            case "Armory":
                yield ARMORY_COLOR;
            case "Valley Casino":
                yield VALLEYCASINO_COLOR;
            case "Valley Overworld":
                yield VALLEYOVERWORLD_COLOR;
            case "King's Valley":
                yield KINGSVALLEY_COLOR;
            case "Lowtide Smuggler":
                yield LOWTIDESMUGGLER_COLOR;
            case "Azacor":
                yield AZACOR_COLOR;
            case "Labs":
                yield LABS_COLOR;
            case "Willows":
                yield WILLOWS_COLOR;
            case "Sanctum":
                yield SANCTUM_COLOR;
            case "Verdant":
                yield VERDANT_COLOR;
            case "Corridors":
                yield CORRIDORS_COLOR;
            case "Reverie":
                yield REVERIE_COLOR;
            case "Kaul":
                yield KAUL_COLOR;
            case "SKT":
                yield SKT_COLOR;
            case "The Wolfswood":
                yield THEWOLFSWOOD_COLOR;
            case "Blue":
                yield BLUE_COLOR;
            case "PORTAL":
                yield PORTAL_COLOR;
            case "Pelias' Keep":
                yield PELIASKEEP_COLOR;
            case "Ruin":
                yield RUIN_COLOR;
            case "Sanguine Halls":
                yield SANGUINEHALLS_COLOR;
            case "Quest Reward":
                yield QUESTREWARD_COLOR;
            case "Transmogrifier":
                yield TRANSMOGRIFIER_COLOR;
            case "Architect's Ring":
                yield ARCHITECTSRING_COLOR;
            default:
                yield DEFAULT_COLOR;
        };
    }

    public static int getColorForStat(String itemStat, double value) {
        if (value < 0 || ItemFormatter.isCurseEnchant(itemStat))
            return TEXT_NEGATIVE_COLOR;

        if (itemStat.equals("armor") || itemStat.equals("agility"))
            return TEXT_DEFENSE_COLOR;

        if (ItemFormatter.isBaseStat(itemStat))
            return TEXT_BASE_STAT_COLOR;

        if (ItemFormatter.isStat(itemStat))
            return TEXT_STAT_COLOR;

        return TEXT_ENCHANT_COLOR;
    }
}
