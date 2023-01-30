package dev.eliux.monumentaitemdictionary.util;

import java.util.Locale;

public class ItemFormatter {
    public static String[] modifiableSkills = {"alchemist_potion", "gruesome_alchemy", "iron_tincture", "empowering_odor", "energizing_elixir", "brutal_alchemy", "alchemical_artillery", "unstable_amalgam", "bezoar", "taboo", "scorched_earth", "esoteric_enhancements", "panacea", "transmutation_ring", "warding_remedy",
            "rejuvenation", "celestial_blessing", "divine_justice", "heavenly_boon", "sacred_provisions", "cleansing_rain", "hand_of_light", "crusade", "sanctified_armor", "holy_javelin", "choir_bells", "luminous_infusion", "enchanted_prayer", "thurible_procession", "hallowed_beam",
            "arcane_strike", "frost_nova", "mana_lance", "thunder_step", "elemental_arrows", "magma_shield", "spellshock", "prismatic_shield", "astral_omen", "cosmic_moonblade", "sage's_insight", "blizzard", "elemental_spirits", "starfall",
            "advancing_shadows", "dagger_throw", "escape_death", "smokescreen", "by_my_blade", "dodging", "skirmisher", "vicious_combos", "blade_dance", "deadly_ronde", "wind_walk", "bodkin_blitz", "cloak_and_dagger", "coup_de_grace",
            "agility", "eagle_eye", "sharpshooter", "swiftness", "hunting_companion", "wind_bomb", "swift_cuts", "volley", "quickdraw", "whirling_blade", "tactical_maneuver", "pinning_shot", "split_arrow", "predator_strike",
            "amplifying_hex", "choleric_flames", "melancholic_lament", "sanguine_harvest", "phlegmatic_resolve", "cursed_wound", "grasping_claws", "soul_rend", "dark_pact", "judgement_chain", "voodoo_bonds", "haunting_shades", "restless_souls", "withering_gaze",
            "brute_force", "defensive_line", "riposte", "toughness", "counter_strike", "frenzy", "shield_bash", "weapon_mastery", "glorious_battle", "meteor_slam", "rampage", "bodyguard", "challenge", "shield_wall",
            "decay", "inferno", "recoil", "jungle's_nourishment", "rage_of_the_keter", "hex_eater", "sapper", "life_drain", "regicide", "quake", "eruption", "smite", "slayer", "duelist", "regeneration", "thunder_aspect"};

    public static boolean shouldBold(String inTier) {
        return inTier.equals("Patron") ||
                inTier.equals("Key") ||
                inTier.equals("Currency") ||
                inTier.equals("Trophy") ||
                inTier.equals("Uncommon") ||
                inTier.equals("Unique") ||
                inTier.equals("Rare") ||
                inTier.equals("Artifact") ||
                inTier.equals("Epic") ||
                inTier.equals("Legendary");
    }

    public static boolean shouldUnderline(String inTier) {
        return inTier.equals("Epic") ||
                inTier.equals("Legendary");
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
            return (value < 0 ? "" : (isBaseStat(name) ? " " : "+")) + value + (isPercentStat(name) ? "" : " ") + formatStat(name);
        } else {
            return formatStat(name) + " " + (isSingleEnchant(name) ? "" : (int)value);
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

    public static int getNumberForTier(String inTier) {
        return switch (inTier) {
            case "Legendary": yield 19;
            case "Epic": yield 18;
            case "Artifact": yield 17;
            case "Rare": yield 16;
            case "Base": yield 15;
            case "Unique": yield 14;
            case "Patron": yield 13;
            case "Uncommon": yield 12;
            case "Tier 5": yield 11;
            case "Tier 4": yield 10;
            case "Tier 3": yield 9;
            case "Tier 2": yield 8;
            case "Tier 1": yield 7;
            case "Tier 0": yield 6;
            case "Obfuscated": yield 5;
            case "Currency": yield 4;
            case "Key": yield 3;
            case "Event": yield 2;
            case "Trophy": yield 1;
            default: yield 0;
        };
    }

    public static int getNumberForRegion(String inRegion) {
        return switch (inRegion) {
            case "Ring": yield 3;
            case "Isles": yield 2;
            case "Valley": yield 1;
            default: yield 0;
        };
    }

    public static String formatStat(String inStat) {
        /*
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
         */
        String stat = inStat;
        stat = stat.replace("_s_", "'s_");
        if (stat.endsWith("_prot")) stat = stat.substring(0, stat.lastIndexOf("_prot")) + "_protection";
        if (stat.endsWith("_base")) stat = stat.substring(0, stat.lastIndexOf("_base")) + "";
        if (stat.endsWith("_flat")) stat = stat.substring(0, stat.lastIndexOf("_flat")) + "";
        if (stat.endsWith("_percent")) stat = "%_" + stat.substring(0, stat.lastIndexOf("_percent"));
        if (stat.endsWith("_bow")) stat = stat.substring(0, stat.lastIndexOf("_bow")) + "";
        if (stat.endsWith("_tool")) stat = stat.substring(0, stat.lastIndexOf("_tool")) + "_food";
        if (stat.endsWith("_m")) stat = stat.substring(0, stat.lastIndexOf("_m")) + "_melee";
        if (stat.endsWith("_p")) stat = stat.substring(0, stat.lastIndexOf("_p")) + "_ranged";
        stat = stat.replace("_", " ");

        if (stat.length() > 1) stat = stat.substring(0, 1).toUpperCase() + stat.substring(1);
        for (int i = 0; i < stat.length() - 1; i++) {
            if (stat.charAt(i) == ' ') {
                stat = stat.substring(0, i + 1) + stat.substring(i + 1, i + 2).toUpperCase() + stat.substring(i + 2);
            }
        }
        return stat;
    }

    public static String getSkillFromCharmStat(String stat) {
        for (String s : modifiableSkills) {
            if (stat.startsWith(s)) return s;
        }
        return "ERR";
    }

    public static String formatCharmTier(String tier) {
        return (tier.equals("Base") ? "" : tier + " ") + "Charm";
    }

    public static String formatCharmStat(String inStat) {
        String stat = inStat;

        if (stat.endsWith("_flat")) stat = stat.substring(0, stat.lastIndexOf("_flat")) + "";
        if (stat.endsWith("_percent")) stat = "%_" + stat.substring(0, stat.lastIndexOf("_percent"));
        stat = stat.replace("_", " ");

        if (stat.length() > 1) stat = stat.substring(0, 1).toUpperCase() + stat.substring(1);
        for (int i = 0; i < stat.length() - 1; i++) {
            if (stat.charAt(i) == ' ') {
                stat = stat.substring(0, i + 1) + stat.substring(i + 1, i + 2).toUpperCase() + stat.substring(i + 2);
            }
        }

        return stat;
    }

    public static String formatCharmSkill(String inSkill) {
        String skill = inSkill;

        skill = skill.replace("_", " ");

        if (skill.length() > 1) skill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
        for (int i = 0; i < skill.length() - 1; i++) {
            if (skill.charAt(i) == ' ') {
                skill = skill.substring(0, i + 1) + skill.substring(i + 1, i + 2).toUpperCase() + skill.substring(i + 2);
            }
        }

        return skill;
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
                inEnchant.equals("curse_of_irreparability") ||
                inEnchant.equals("mending") ||
                inEnchant.equals("silk_touch") ||
                inEnchant.equals("curse_of_corruption") ||
                inEnchant.equals("protection_of_the_depths") ||
                inEnchant.equals("infinity_bow") ||
                inEnchant.equals("infinity_tool") ||
                inEnchant.equals("infinity") ||
                inEnchant.equals("aqua_affinity") ||
                inEnchant.equals("ashes_of_eternity") ||
                inEnchant.equals("void_tether") ||
                inEnchant.equals("excavator") ||
                inEnchant.equals("darksight") ||
                inEnchant.equals("material") ||
                inEnchant.equals("alchemical_utensil") ||
                inEnchant.equals("broomstick") ||
                inEnchant.equals("clucking") ||
                inEnchant.equals("oinking") ||
                inEnchant.equals("throwing_knife") ||
                inEnchant.equals("liquid_courage") ||
                inEnchant.equals("intoxicating_warmth") ||
                inEnchant.equals("temporal_bender") ||
                inEnchant.equals("curse_of_ephemerality") ||
                inEnchant.equals("instant_drink") ||
                inEnchant.equals("divine_aura") ||
                inEnchant.equals("persistence");
    }

    public static boolean isCurseEnchant(String inEnchant) {
        return inEnchant.equals("ineptitude") ||
                inEnchant.equals("curse_of_shrapnel") ||
                inEnchant.equals("curse_of_vanishing") ||
                inEnchant.equals("curse_of_corruption") ||
                inEnchant.equals("curse_of_crippling") ||
                inEnchant.equals("curse_of_irreparability") ||
                inEnchant.equals("two_handed") ||
                inEnchant.equals("fire_fragility") ||
                inEnchant.equals("melee_fragility") ||
                inEnchant.equals("blast_fragility") ||
                inEnchant.equals("projectile_fragility") ||
                inEnchant.equals("magic_fragility") ||
                inEnchant.equals("curse_of_anemia") ||
                inEnchant.equals("curse_of_ephemerality");
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

    public static boolean isPercentStat(String inStat) {
        return inStat.endsWith("_percent");
    }

    public static boolean isBaseStat(String inStat) {
        return inStat.equals("projectile_damage_base") ||
                inStat.equals("projectile_speed_base") ||
                inStat.equals("attack_speed_base") ||
                inStat.equals("attack_damage_base") ||
                inStat.equals("throw_rate_base");
    }

    public static boolean isHiddenStat(String inStat) {
        return inStat.equals("noglint") ||
                inStat.equals("hideenchants") ||
                inStat.equals("hideinfo");
    }
}
