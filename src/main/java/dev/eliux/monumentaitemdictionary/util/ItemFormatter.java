package dev.eliux.monumentaitemdictionary.util;

public class ItemFormatter {
    public static boolean shouldBold(String inTier) {
        return inTier.equals("Patron") ||
                inTier.equals("Uncommon") ||
                inTier.equals("Unique") ||
                inTier.equals("Rare") ||
                inTier.equals("Artifact") ||
                inTier.equals("Epic");
    }

    public static boolean shouldUnderline(String inTier) {
        return inTier.equals("Epic");
    }

    public static String formatRegion(String inRegion) {
        return switch (inRegion) {
            case "Valley":
                yield "King's Valley";
            case "Isles":
                yield "Celsian Isles";
            case "Ring":
                yield "Architect's Ring";
            default:
                yield inRegion;
        };
    }

    public static String buildStatString(String name, double value) {
        if (isStat(name)) {
            return (value < 0 ? "" : (isBaseStat(name) ? " " : "+")) + value + formatStat(name);
        } else {
            return formatStat(name) + (isSingleEnchant(name) ? "" : (int)value);
        }
    }

    public static int getMasterworkForRarity(String rarity) {
        return switch (rarity) {
            case "Rare":
                yield 3;
            case "Epic":
                yield 6;
            default:
                yield 0;
        };
    }

    public static String formatStat(String inStat) {
        return switch (inStat) {
            case "aptitude":
                yield "Aptitude ";
            case "life_drain":
                yield "Life Drain ";
            case "gills":
                yield "Gills";
            case "weightless":
                yield "Weightless";
            case "second_wind":
                yield "Second Wind ";
            case "throw_rate_base":
                yield " Throw Rate";
            case "respiration":
                yield "Respiration ";
            case "sustenance":
                yield "Sustenance ";
            case "rage_of_the_keter":
                yield "Rage of the Keter";
            case "magic_prot":
                yield "Magic Protection ";
            case "lure":
                yield "Lure ";
            case "evasion":
                yield "Evasion ";
            case "jungle_s_nourishment":
                yield "Jungle's Nourishment";
            case "unbreakable":
                yield "Unbreakable";
            case "attack_speed_base":
                yield " Attack Speed";
            case "piercing":
                yield "Piercing ";
            case "shrapnel":
                yield "Curse of Shrapnel ";
            case "adaptability":
                yield "Adaptability";
            case "multishot":
                yield "Multishot";
            case "intuition":
                yield "Intuition";
            case "inure":
                yield "Inure ";
            case "speed_percent":
                yield "% Speed";
            case "speed_flat":
                yield " Speed";
            case "punch":
                yield "Punch ";
            case "crippling":
                yield "Curse of Crippling ";
            case "attack_speed_percent":
                yield "% Attack Speed";
            case "quake":
                yield "Quake ";
            case "hex_eater":
                yield "Hex Eater ";
            case "bleeding":
                yield "Bleeding ";
            case "ineptitude":
                yield "Ineptitude ";
            case "projectile_prot":
                yield "Projectile Protection ";
            case "decay":
                yield "Decay ";
            case "anemia":
                yield "Curse of Anemia ";
            case "efficiency":
                yield "Efficiency ";
            case "chaotic":
                yield "Chaotic ";
            case "magic_damage_percent":
                yield "% Magic Damage";
            case "fire_aspect_p":
                yield "Fire Aspect Ranged ";
            case "radiant":
                yield "Radiant";
            case "melee_prot":
                yield "Melee Protection ";
            case "resurrection":
                yield "Resurrection";
            case "soul_speed":
                yield "Soul Speed ";
            case "point_blank":
                yield "Point Blank ";
            case "arcane_thrust":
                yield "Arcane Thrust ";
            case "smite":
                yield "Smite ";
            case "feather_falling":
                yield "Feather Falling ";
            case "projectile_damage_percent":
                yield "% Projectile Damage";
            case "two_handed":
                yield "Two Handed";
            case "max_health_percent":
                yield "% Max Health";
            case "slayer":
                yield "Slayer ";
            case "steadfast":
                yield "Steadfast ";
            case "looting":
                yield "Looting ";
            case "thunder_aspect_m":
                yield "Thunder Aspect Melee ";
            case "attack_damage_percent":
                yield "% Attack Damage";
            case "irreparability":
                yield "Curse of Irreparability";
            case "projectile_speed_base":
                yield " Projectile Speed";
            case "mending":
                yield "Mending";
            case "agility":
                yield " Agility";
            case "corruption":
                yield "Curse of Corruption";
            case "eruption":
                yield "Eruption ";
            case "silk_touch":
                yield "Silk Touch";
            case "depth_strider":
                yield "Depth Strider ";
            case "quick_charge":
                yield "Quick Charge ";
            case "sweeping_edge":
                yield "Sweeping Edge ";
            case "protection_of_the_depths":
                yield "Protection of the Depths";
            case "unbreaking":
                yield "Unbreaking ";
            case "attack_speed_flat":
                yield " Attack Speed";
            case "retrieval":
                yield "Retrieval ";
            case "armor":
                yield " Armor";
            case "fortune":
                yield "Fortune ";
            case "reflexes":
                yield "Reflexes ";
            case "infinity_bow":
                yield "Infinity";
            case "shielding":
                yield "Shielding ";
            case "darksight":
                yield "Darksight";
            case "sniper":
                yield "Sniper ";
            case "triage":
                yield "Triage ";
            case "sapper":
                yield "Sapper ";
            case "abyssal":
                yield "Abyssal ";
            case "throw_rate_percent":
                yield "% Throw Rate";
            case "thunder_aspect_p":
                yield "Thunder Aspect Ranged ";
            case "adrenaline":
                yield "Adrenaline ";
            case "projectile_damage_base":
                yield " Projectile Damage";
            case "knockback":
                yield "Knockback ";
            case "duelist":
                yield "Duelist ";
            case "poise":
                yield "Poise ";
            case "ethereal":
                yield "Ethereal ";
            case "max_health_flat":
                yield " Max Health";
            case "fire_prot":
                yield "Fire Protection ";
            case "recoil":
                yield "Recoil ";
            case "blast_prot":
                yield "Blast Protection ";
            case "ice_aspect_p":
                yield "Ice Aspect Ranged ";
            case "thorns_percent":
                yield "% Thorns Damage";
            case "infinity_tool":
                yield "Infinity Food";
            case "regicide":
                yield "Regicide ";
            case "multitool":
                yield "Multitool ";
            case "riptide":
                yield "Riptide ";
            case "thorns_flat":
                yield " Thorns";
            case "vanishing":
                yield "Curse of Vanishing ";
            case "void_tether":
                yield "Void Tether";
            case "fire_aspect_m":
                yield "Fire Aspect Melee ";
            case "ice_aspect_m":
                yield "Ice Aspect Melee ";
            case "attack_damage_base":
                yield " Attack Damage";
            case "regen":
                yield "Regeneration ";
            case "knockback_resistance_flat":
                yield " Knockback Resistance";
            case "projectile_speed_percent":
                yield "% Projectile Speed";
            case "ashes_of_eternity":
                yield "Ashes of Eternity";
            case "spell_power_base":
                yield "% Spell Power";
            case "inferno":
                yield "Inferno ";
            case "tempo":
                yield "Tempo ";
            case "aqua_affinity":
                yield "Aqua Affinity";
            case "cloaked":
                yield "Cloaked ";
            case "earth_aspect_m":
                yield "Earth Aspect Melee ";
            case "earth_aspect_p":
                yield "Earth Aspect Ranged ";
            case "wind_aspect_m":
                yield "Wind Aspect Melee ";
            case "wind_aspect_p":
                yield "Wind Aspect Ranged ";
            case "excavator":
                yield "Excavator";
            case "fire_fragility":
                yield "Fire Fragility ";
            case "magic_fragility":
                yield "Magic Fragility ";
            case "melee_fragility":
                yield "Melee Fragility ";
            case "projectile_fragility":
                yield "Projectile Fragility ";
            case "first_strike":
                yield "First Strike ";
            case "guard":
                yield "Guard ";
            case "stamina":
                yield "Stamina ";
            case "trivium":
                yield "Trivium ";
            case "worldly_protection":
                yield "Worldly Protection ";
            default:
                yield inStat;
        };
    }

    public static boolean isSingleEnchant(String inEnchant) {
        return inEnchant.equals("gills") ||
                inEnchant.equals("weightless") ||
                inEnchant.equals("rage_of_the_keter") ||
                inEnchant.equals("jungle_s_nourishment") ||
                inEnchant.equals("unbreakable") ||
                inEnchant.equals("adaptability") ||
                inEnchant.equals("multishot") ||
                inEnchant.equals("intuition") ||
                inEnchant.equals("radiant") ||
                inEnchant.equals("resurrection") ||
                inEnchant.equals("two_handed") ||
                inEnchant.equals("irreparability") ||
                inEnchant.equals("mending") ||
                inEnchant.equals("silk_touch") ||
                inEnchant.equals("corruption") ||
                inEnchant.equals("protection_of_the_depths") ||
                inEnchant.equals("infinity_bow") ||
                inEnchant.equals("infinity_tool") ||
                inEnchant.equals("aqua_affinity") ||
                inEnchant.equals("ashes_of_eternity") ||
                inEnchant.equals("void_tether") ||
                inEnchant.equals("excavator") ||
                inEnchant.equals("darksight");
    }

    public static boolean isCurseEnchant(String inEnchant) {
        return inEnchant.equals("ineptitude") ||
                inEnchant.equals("shrapnel") ||
                inEnchant.equals("vanishing") ||
                inEnchant.equals("corruption") ||
                inEnchant.equals("crippling") ||
                inEnchant.equals("irreparability") ||
                inEnchant.equals("two_handed") ||
                inEnchant.equals("fire_fragility") ||
                inEnchant.equals("melee_fragility") ||
                inEnchant.equals("projectile_fragility") ||
                inEnchant.equals("magic_fragility") ||
                inEnchant.equals("anemia");
    }

    public static boolean isStat(String inStat) {
        return inStat.equals("armor") ||
                inStat.equals("agility") ||
                inStat.equals("spell_power_base") ||
                inStat.equals("projectile_speed_percent") ||
                inStat.equals("knockback_resistance_flat") ||
                inStat.equals("attack_damage_base") ||
                inStat.equals("thorns_percent") ||
                inStat.equals("max_health_flat") ||
                inStat.equals("projectile_damage_base") ||
                inStat.equals("throw_rate_percent") ||
                inStat.equals("attack_speed_flat") ||
                inStat.equals("projectile_speed_base") ||
                inStat.equals("attack_damage_percent") ||
                inStat.equals("max_health_percent") ||
                inStat.equals("projectile_damage_percent") ||
                inStat.equals("magic_damage_percent") ||
                inStat.equals("attack_speed_percent") ||
                inStat.equals("thorns_flat") ||
                inStat.equals("speed_flat") ||
                inStat.equals("speed_percent") ||
                inStat.equals("attack_speed_base") ||
                inStat.equals("throw_rate_base");
    }

    public static boolean isBaseStat(String inStat) {
        return inStat.equals("projectile_damage_base") ||
                inStat.equals("projectile_speed_base") ||
                inStat.equals("attack_speed_base") ||
                inStat.equals("attack_damage_base") ||
                inStat.equals("throw_rate_base");
    }
}
