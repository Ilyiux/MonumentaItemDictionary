package dev.eliux.monumentaitemdictionary.util;

import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;

import java.util.*;

import static java.lang.Math.*;
public class Stats {
    private final Map<String, Integer> situationals;
    private final Map<String, List<ItemStat>> allItemStats = new HashMap<>();
    public Percentage currentHealthPercent;
    public Percentage speedPercent;
    public double speedFlat;
    public double agility;
    public double armor;
    public double knockbackRes;
    public double thorns;
    public Percentage healthPercent;
    public double healthFlat;
    public double healthFinal;
    public Percentage healingRate;
    public Percentage effHealingRate;
    public double regenPerSec;
    public Percentage regenPerSecPercent;
    public double lifeDrainOnCrit;
    public Percentage lifeDrainOnCritPercent;
    public double meleeProt;
    public double projectileProt;
    public double magicProt;
    public double blastProt;
    public double fireProt;
    public double fallProt;
    public double meleeFragility;
    public double projectileFragility;
    public double magicFragility;
    public double blastFragility;
    public double fireFragility;
    public Percentage ailmentHNDR;
    public Percentage fallHNDR;
    public Percentage meleeHNDR;
    public Percentage magicHNDR;
    public Percentage projectileHNDR;
    public Percentage blastHNDR;
    public Percentage fireHNDR;
    public Percentage meleeDR;
    public Percentage projectileDR;
    public Percentage magicDR;
    public Percentage blastDR;
    public Percentage fallDR;
    public Percentage fireDR;
    public Percentage ailmentDR;
    public double meleeEHP;
    public double projectileEHP;
    public double magicEHP;
    public double blastEHP;
    public double fallEHP;
    public double fireEHP;
    public double ailmentEHP;
    public boolean hasMoreArmor;
    public boolean hasMoreAgility;
    public boolean hasEqualDefenses;
    public double worldlyProtection;
    public Percentage attackDamagePercent;
    public Percentage attackSpeedPercent;
    public double attackSpeed;
    public double attackSpeedFlatBonus;
    public double attackDamage;
    public double attackDamageCrit;
    public double iframeDPS;
    public double iframeCritDPS;
    public Percentage projectileDamagePercent;
    public double projectileDamage;
    public Percentage projectileSpeedPercent;
    public double projectileSpeed;
    public double throwRate;
    public Percentage throwRatePercent;
    public Percentage magicDamagePercent;
    public Percentage spellPowerPercent;
    public Percentage spellDamage;
    public Percentage spellCooldownPercent;
    public double aptitude;
    public double ineptitude;
    public double crippling;
    public double corruption;
    public boolean twoHanded;
    public final double vitality;
    public final double tenacity;
    public final double vigor;
    public final double focus;
    public final double perspicacity;
    public Percentage thornsPercent;
    public double currentHealh;
    public double ailmentProt;
    private List<String> itemTypes = new ArrayList<>(Arrays.asList("mainhand", "offhand", "helmet", "chestplate", "leggings", "boots"));
    public boolean weightless;

    public Stats(List<DictionaryItem> items, Map<String, Integer> situationals, Map<String, Integer> infusions, double currentHealthPercent) {
        this.situationals = situationals;
        this.vitality = (infusions.get("vitality") != null) ? infusions.get("vitality") : 0;
        this.tenacity = (infusions.get("tenacity") != null) ? infusions.get("tenacity") : 0;
        this.vigor = (infusions.get("vigor") != null) ? infusions.get("vigor") : 0;
        this.focus = (infusions.get("focus") != null) ? infusions.get("focus") : 0;
        this.perspicacity = (infusions.get("perspicacity") != null) ? infusions.get("perspicacity") : 0;

        this.currentHealthPercent = new Percentage(currentHealthPercent, true);

        for (int i=0;i<itemTypes.size();i++) {
            if (items.get(i) != null) {
                DictionaryItem item = items.get(i);
                List<ItemStat> itemStats = item.getStatsFromMasterwork(item.getMaxMasterwork() - 1);
                this.allItemStats.put(itemTypes.get(i), itemStats);
            }
        }
        
        setDefaultValues();
        sumAllStats();
        adjustStats();
        calculateDefenseStats();
        calculateOffenseStats();
    }

    private void calculateOffenseStats() {
        List<ItemStat> mainhand = allItemStats.get("mainhand");
        if (this.situationals.get("versatile") > 0) {
            double extraAttackDamagePercent = (this.projectileDamagePercent.perc - 100) * 0.5;
            double extraProjectileDamagePercent = (this.attackDamagePercent.perc - 100) * 0.4;
            this.attackDamagePercent.add(extraAttackDamagePercent, true);
            this.projectileDamagePercent.add(extraProjectileDamagePercent, true);
        }

        this.attackDamage = sumNumberStat(mainhand, "attack_damage_base", this.attackDamage)
                * this.attackDamagePercent.val
                * (1 + 0.0075 * this.vigor)
                * ((this.currentHealthPercent.perc <= 50) ? 1 - 0.1 * this.crippling : 1);
        this.attackSpeed = (sumNumberStat(mainhand, "attack_speed_base", this.attackSpeed) + this.attackSpeedFlatBonus) * this.attackDamagePercent.val;
        this.attackDamageCrit = (attackDamage * 1.5);
        this.iframeDPS = ((this.attackSpeed >= 2) ? attackDamage * 2 : attackDamage * attackSpeed);
        this.iframeCritDPS = ((this.attackSpeed >= 2) ? attackDamageCrit * 2 : attackDamageCrit * attackSpeed);

        this.projectileDamage = sumNumberStat(mainhand, "projectile_damage_base", projectileDamage)
                * this.projectileDamagePercent.val
                * (1 + 0.0075 * this.focus);
        this.projectileSpeed = sumNumberStat(mainhand, "projectile_speed_base", this.projectileSpeed) * this.projectileDamagePercent.val;
        this.throwRate = sumNumberStat(mainhand, "throw_rate_base", this.throwRate) * this.throwRatePercent.val;

        this.spellPowerPercent.add(sumNumberStat(mainhand, "spell_power_base", 0), true);
        this.spellDamage = (
                this.spellPowerPercent.duplicate()
                        .mulP(this.magicDamagePercent)
                        .mul(1 + 0.0075 * this.perspicacity, false)
                );
        this.spellCooldownPercent = this.spellCooldownPercent.mul(pow(0.95, this.aptitude + this.ineptitude), false);
            }

    private void calculateDefenseStats() {
        Map<String, Map<String, Percentage>> drs =  calculateDamageReductions();

        String drType = (this.situationals.get("second_wind") > 0) ? "second_wind" : "base";

        this.meleeDR = drs.get("melee").get(drType);
        this.projectileDR = drs.get("projectile").get(drType);
        this.magicDR = drs.get("magic").get(drType);
        this.blastDR = drs.get("blast").get(drType);
        this.fireDR = drs.get("fire").get(drType);
        this.fallDR = drs.get("fall").get(drType);
        this.ailmentDR = drs.get("ailment").get(drType);

        if (this.situationals.get("second_wind") == 0 || drType.equals("base")) {
            this.meleeEHP = (this.healthFinal * this.currentHealthPercent.val / (1 - drs.get("melee").get("base").val));
            this.projectileEHP = (this.healthFinal * this.currentHealthPercent.val / (1 - drs.get("projectile").get("base").val));
            this.magicEHP = (this.healthFinal * this.currentHealthPercent.val / (1 - drs.get("magic").get("base").val));
            this.blastEHP = (this.healthFinal * this.currentHealthPercent.val / (1 - drs.get("blast").get("base").val));
            this.fireEHP = (this.healthFinal * this.currentHealthPercent.val / (1 - drs.get("fire").get("base").val));
            this.fallEHP = (this.healthFinal * this.currentHealthPercent.val / (1 - drs.get("fall").get("base").val));
            this.ailmentEHP = (this.healthFinal * this.currentHealthPercent.val / (1 - drs.get("ailment").get("base").val));
        } else {
            double hpNoSecondWind = max(0, (this.currentHealh - this.healthFinal * 0.5));
            double hpSecondWind = min(this.currentHealh, this.healthFinal * 0.5);
            this.meleeEHP = (hpNoSecondWind / (1 - drs.get("melee").get("base").val) + hpSecondWind / (1 - drs.get("melee").get("second_wind").val));
            this.projectileEHP = (hpNoSecondWind / (1 - drs.get("projectile").get("base").val) + hpSecondWind / (1 - drs.get("projectile").get("second_wind").val));
            this.magicEHP = (hpNoSecondWind / (1 - drs.get("magic").get("base").val) + hpSecondWind / (1 - drs.get("magic").get("second_wind").val));
            this.blastEHP = (hpNoSecondWind / (1 - drs.get("blast").get("base").val) + hpSecondWind / (1 - drs.get("blast").get("second_wind").val));
            this.fireEHP = (hpNoSecondWind / (1 - drs.get("fire").get("base").val) + hpSecondWind / (1 - drs.get("fire").get("second_wind").val));
            this.fallEHP = (hpNoSecondWind / (1 - drs.get("fall").get("base").val) + hpSecondWind / (1 - drs.get("fall").get("second_wind").val));
            this.ailmentEHP = (hpNoSecondWind / (1 - drs.get("ailment").get("base").val) + hpSecondWind / (1 - drs.get("ailment").get("second_wind").val));
        }

        this.meleeHNDR = new Percentage((1 - ((1 - drs.get("melee").get(drType).val) / (this.healthFinal / 20))), false);
        this.projectileHNDR = new Percentage((1 - ((1 - drs.get("projectile").get(drType).val) / (this.healthFinal / 20))), false);
        this.magicHNDR = new Percentage((1 - ((1 - drs.get("magic").get(drType).val) / (this.healthFinal / 20))), false);
        this.blastHNDR = new Percentage((1 - ((1 - drs.get("blast").get(drType).val) / (this.healthFinal / 20))), false);
        this.fireHNDR = new Percentage((1 - ((1 - drs.get("fire").get(drType).val) / (this.healthFinal / 20))), false);
        this.fallHNDR = new Percentage((1 - ((1 - drs.get("fall").get(drType).val) / (this.healthFinal / 20))), false);
        this.ailmentHNDR = new Percentage((1 - ((1 - drs.get("ailment").get(drType).val) / (this.healthFinal / 20))), false);
    }

    private Map<String, Map<String, Percentage>> calculateDamageReductions() {
        double armor = Math.max(this.armor, 0);
        double agility = Math.max(this.agility, 0);

        boolean moreAgility = agility > armor;
        boolean moreArmor = armor > agility;
        boolean hasEqual = armor == agility;
        boolean hasNothing = (hasEqual && armor == 0);

        double situationalArmor = (this.situationals.get("adaptability") > 0) ? min(max(agility, armor), 30) * 0.2 : min(armor, 30) * 0.2;
        double situationalAgility = (this.situationals.get("adaptability") > 0) ? min(max(agility, armor), 30) * 0.2 : min(agility, 30) * 0.2;

        double etherealSit =    (this.situationals.get("ethereal") > 0) ? situationalAgility * this.situationals.get("ethereal") : 0;
        double tempoSit =       (this.situationals.get("tempo") > 0) ? situationalAgility * this.situationals.get("tempo") : 0;
        double evasionSit =     (this.situationals.get("evasion") > 0) ? situationalAgility * this.situationals.get("evasion") : 0;
        double reflexesSit =    (this.situationals.get("reflexes") > 0) ? situationalAgility * this.situationals.get("reflexes") : 0;
        double shieldingSit =   (this.situationals.get("shielding") > 0) ? situationalArmor * this.situationals.get("shielding") : 0;
        double poiseSit =       (this.situationals.get("poise") > 0) ? ((this.currentHealthPercent.val >= 0.9) ? situationalArmor * this.situationals.get("poise") : 0) : 0;
        double inureSit =       (this.situationals.get("inure") > 0) ? situationalArmor * this.situationals.get("inure") : 0;
        double guardSit =       (this.situationals.get("guard") > 0) ? situationalArmor * this.situationals.get("guard") : 0;
        double cloakedSit =     (this.situationals.get("cloaked") > 0) ? situationalAgility * this.situationals.get("cloaked") : 0;

        double steadfastScaling = 0.33;
        double steadfastMaxScaling = 20;
        double steadfastLowerBound = 1 - (steadfastMaxScaling / steadfastScaling / 100);
        double steadfastArmor = (1 - max(steadfastLowerBound, min(1, this.currentHealthPercent.val))) * steadfastScaling *
                min(((this.situationals.get("adaptability") > 0 && moreAgility) ? agility : (moreArmor) ? armor : (this.situationals.get("adaptability") == 0) ? armor : 0), 30);
        double steadfastSit = (this.situationals.get("steadfast") > 0) ? steadfastArmor * this.situationals.get("steadfast") : 0;

        double sumSits = etherealSit + tempoSit + evasionSit + reflexesSit + shieldingSit + poiseSit + inureSit + guardSit + cloakedSit;
        double sumArmorSits = shieldingSit + poiseSit + inureSit + guardSit;
        double sumAgiSits = etherealSit + tempoSit + evasionSit + reflexesSit + cloakedSit;

        double armorPlusSits = armor;
        if (this.situationals.get("adaptability") > 0) {
            if (moreArmor) {
                armorPlusSits += sumSits;
            }
        } else {
            armorPlusSits += sumArmorSits;
        }
        double armorPlusSitsSteadfast = armorPlusSits + steadfastSit;

        double agilityPlusSits = agility;
        if (this.situationals.get("adaptability") > 0) {
            if (moreAgility) {
                agilityPlusSits += sumSits;
            }
        } else {
            agilityPlusSits += sumAgiSits;
        }

        double halfArmor = armorPlusSitsSteadfast / 2;
        double halfAgility = agilityPlusSits / 2;

        Map<String, Double> meleeDamage =       calculateDamageTaken(hasNothing,    this.meleeProt,         this.meleeFragility,        2, armorPlusSitsSteadfast,  agilityPlusSits);
        Map<String, Double> projectileDamage =  calculateDamageTaken(hasNothing,    this.projectileProt,    this.projectileFragility,   2, armorPlusSitsSteadfast,  agilityPlusSits);
        Map<String, Double> magicDamage =       calculateDamageTaken(hasNothing,    this.magicProt,         this.magicFragility,        2, armorPlusSitsSteadfast,  agilityPlusSits);
        Map<String, Double> blastDamage =       calculateDamageTaken(hasNothing,    this.blastProt,         this.blastFragility,        2, armorPlusSitsSteadfast,  agilityPlusSits);
        Map<String, Double> fireDamage =        calculateDamageTaken(hasNothing,    this.fireProt,          this.fireFragility,         2, halfArmor,               halfAgility);
        Map<String, Double> fallDamage =        calculateDamageTaken(hasNothing,    this.fallProt,          0,                          3, halfArmor,               halfAgility);
        Map<String, Double> ailmentDamage =     calculateDamageTaken(true,          0,                      0,                          0, 0,                       0);

        Map<String, Map<String, Percentage>> reductions = new HashMap<>();
        reductions.put("melee", new HashMap<String, Percentage>() {{
            put("base", new Percentage(100 - meleeDamage.get("base"), true));
            put("second_wind", new Percentage(100 - meleeDamage.get("second_wind"), true));
        }});
        reductions.put("projectile", new HashMap<String, Percentage>() {{
            put("base", new Percentage(100 - projectileDamage.get("base"), true));
            put("second_wind", new Percentage(100 - projectileDamage.get("second_wind"), true));
        }});
        reductions.put("magic", new HashMap<String, Percentage>() {{
            put("base", new Percentage(100 - magicDamage.get("base"), true));
            put("second_wind", new Percentage(100 - magicDamage.get("second_wind"), true));
        }});
        reductions.put("blast", new HashMap<String, Percentage>() {{
            put("base", new Percentage(100 - blastDamage.get("base"), true));
            put("second_wind", new Percentage(100 - blastDamage.get("second_wind"), true));
        }});
        reductions.put("fire", new HashMap<String, Percentage>() {{
            put("base", new Percentage(100 - fireDamage.get("base"), true));
            put("second_wind", new Percentage(100 - fireDamage.get("second_wind"), true));
        }});
        reductions.put("fall", new HashMap<String, Percentage>() {{
            put("base", new Percentage(100 - fallDamage.get("base"), true));
            put("second_wind", new Percentage(100 - fallDamage.get("second_wind"), true));
        }});
        reductions.put("ailment", new HashMap<String, Percentage>() {{
            put("base", new Percentage(100 - ailmentDamage.get("base"), true));
            put("second_wind", new Percentage(100 - ailmentDamage.get("second_wind"), true));
        }});

        return reductions;
    }

    private Map<String, Double> calculateDamageTaken(boolean noArmor, double prot, double fragility, double protModifier, double earmor, double eagility) {
        Map<String, Double> damageTaken = new HashMap<>();

        double baseDmg = (noArmor) ? 100 * (1 - this.worldlyProtection * 0.1) * pow(0.96, (prot * protModifier - fragility * protModifier)) :
                100 * (1 - this.worldlyProtection * 0.1) * pow(0.96, ((prot * protModifier - fragility * protModifier) + earmor + eagility) - (0.5 * earmor * eagility / (earmor + eagility))) * (1 - (this.tenacity * 0.005));
        double secondwindDmg = baseDmg * pow(0.9, this.situationals.get("second_wind")) * (1-(this.tenacity * 0.005));


        damageTaken.put("base", baseDmg);
        damageTaken.put("second_wind", secondwindDmg);

        return damageTaken;
    }

    private void adjustStats() {
        this.healthFinal = this.healthFlat * this.healthPercent.val * (1 + 0.01*this.vitality);
        this.currentHealh = this.healthFinal * this.currentHealthPercent.val;
        this.speedPercent = this.speedPercent
                .mul((this.speedFlat)/0.1, false)
                .mul(((this.currentHealthPercent.perc <= 50) ? 1 - 0.1 * this.crippling : 1), false);
        this.knockbackRes = (this.knockbackRes > 10) ? 100 : this.knockbackRes * 10;

        this.effHealingRate = new Percentage(((20 / this.healthFinal) * this.healingRate.val), false);
        this.regenPerSec = 0.33 * sqrt(this.regenPerSec) * this.healingRate.val;
        this.regenPerSecPercent = new Percentage((this.regenPerSec / this.healthFinal), false);

        this.lifeDrainOnCrit = (sqrt(this.lifeDrainOnCrit)) * this.healingRate.val;
        this.lifeDrainOnCritPercent = new Percentage((lifeDrainOnCrit / this.healthFinal), false);

        this.thorns *= (double) this.thornsPercent.val;
    }

    private Double sumNumberStat(List<ItemStat> itemStats, String statName, double defaultIncrement) {
        if (itemStats == null) return 0.0;
        double statValue = 0.0;
        for (ItemStat stat : itemStats) {
            if (statName.equals("mainhand")) {
                System.out.println(stat.statName);
                System.out.println(stat.statValue);
                System.out.println();
            }
            if (stat.statName.equals(statName)) {
                statValue = stat.statValue;
                break;
            }
        }
        return (statValue > 0.0) ? statValue : defaultIncrement;
    }

    private double sumEnchantmentStat(List<ItemStat> itemStats, String enchName, double perLevelMultiplier) {
        if (itemStats == null) return perLevelMultiplier;
        double enchLevel = 0;
        for (ItemStat stat : itemStats) {
            if (stat.statName.equals(enchName)) {
                enchLevel = stat.statValue;
                break;
            }
        }
        return enchLevel * perLevelMultiplier;
    }
    private void sumAllStats() {
        this.allItemStats.keySet().forEach(type -> {
            List<ItemStat> itemStats = this.allItemStats.get(type);
            this.healthPercent.add(sumNumberStat(itemStats, "max_health_percent", 0), true);
            this.healthFlat += sumNumberStat(itemStats, "max_health_flat", 0);
            this.agility += sumNumberStat(itemStats, "agility", 0);
            this.armor += sumNumberStat(itemStats, "armor", 0);
            this.speedPercent.add(sumNumberStat(itemStats, "speed_percent", 0), true);
            this.speedFlat += sumNumberStat(itemStats, "speed_flat", 0);
            this.knockbackRes += sumNumberStat(itemStats, "knockback_resistance_flat", 0);
            this.thorns += sumNumberStat(itemStats, "thorns_flat", 0);
            this.throwRatePercent.add(sumNumberStat(itemStats, "throw_rate_percent", 0), true);

            this.healingRate
                    .add(this.sumEnchantmentStat(itemStats, "curse_of_anemia", -10), true)
                    .add(this.sumEnchantmentStat(itemStats, "sustenance", 10), true);
            this.regenPerSec += this.sumEnchantmentStat(itemStats, "regeneration", 1);
            this.lifeDrainOnCrit += this.sumEnchantmentStat(itemStats, "life_drain", 1);

            this.meleeProt += sumNumberStat(itemStats, "melee_protection", 0);
            this.projectileProt += sumNumberStat(itemStats, "projectile_protection", 0);
            this.magicProt += sumNumberStat(itemStats, "magic_protection", 0);
            this.blastProt += sumNumberStat(itemStats, "blast_protection", 0);
            this.fireProt += sumNumberStat(itemStats, "fire_protection", 0);
            this.fallProt += sumNumberStat(itemStats, "feather_falling", 0);

            this.meleeFragility += sumNumberStat(itemStats, "melee_fragility", 0);
            this.projectileFragility += sumNumberStat(itemStats, "projectile_fragility", 0);
            this.magicFragility += sumNumberStat(itemStats, "magic_fragility", 0);
            this.blastFragility += sumNumberStat(itemStats, "blast_fragility", 0);
            this.fireFragility += sumNumberStat(itemStats, "fire_fragility", 0);

            this.attackDamagePercent.add(sumNumberStat(itemStats, "attack_damage_percent", 0), true);
            this.attackSpeedPercent.add(sumNumberStat(itemStats, "attack_speed_percent", 0), true);
            this.attackSpeedFlatBonus += sumNumberStat(itemStats, "attack_speed_flat", 0);

            this.projectileDamagePercent.add(sumNumberStat(itemStats, "projectile_damage_percent", 0), true);
            this.projectileSpeedPercent.add(sumNumberStat(itemStats, "projectile_speed_percent", 0), true);

            this.magicDamagePercent.add(sumNumberStat(itemStats, "magic_damage_percent", 0), true);

            this.aptitude += (double) this.sumEnchantmentStat(itemStats, "aptitude", 1);
            this.ineptitude += (double) this.sumEnchantmentStat(itemStats, "ineptitude", -1);

            this.worldlyProtection += sumNumberStat(itemStats, "worldly_protection", 0);

            this.situationals.put("shielding", (int) (this.situationals.get("shielding") + sumNumberStat(itemStats, "shielding", 0))) ;
            this.situationals.put("poise", (int) (this.situationals.get("poise") + sumNumberStat(itemStats, "poise", 0)));
            this.situationals.put("inure", (int) (this.situationals.get("inure") + sumNumberStat(itemStats, "inure", 0)));
            this.situationals.put("steadfast", (int) (this.situationals.get("steadfast") + sumNumberStat(itemStats, "steadfast", 0)));
            this.situationals.put("guard", (int) (this.situationals.get("guard") + sumNumberStat(itemStats, "guard", 0)));
            this.situationals.put("ethereal", (int) (this.situationals.get("ethereal") + sumNumberStat(itemStats, "ethereal", 0)));
            this.situationals.put("reflexes", (int) (this.situationals.get("reflexes") + sumNumberStat(itemStats, "reflexes", 0)));
            this.situationals.put("evasion", (int) (this.situationals.get("evasion") + sumNumberStat(itemStats, "evasion", 0)));
            this.situationals.put("tempo", (int) (this.situationals.get("tempo") + sumNumberStat(itemStats, "tempo", 0)));
            this.situationals.put("cloaked", (int) (this.situationals.get("cloaked") + sumNumberStat(itemStats, "cloaked", 0)));
            this.situationals.put("adaptability", (int) (this.situationals.get("adaptability") + sumNumberStat(itemStats, "adaptability", 0)));
            this.situationals.put("second_wind", (int) (this.situationals.get("second_wind") + sumNumberStat(itemStats, "second_wind", 0)));

            this.crippling += sumNumberStat(itemStats, "curse_of_crippling", 0);
            this.corruption += sumNumberStat(itemStats, "curse_of_corruption", 0);
        });
    }
    private void setDefaultValues() {
        this.agility = 0;
        this.armor = 0;
        this.speedPercent = new Percentage(100, true);
        this.speedFlat = 0.1;
        this.knockbackRes = 0;
        this.thorns = 0;
        this.thornsPercent = new Percentage(100, true);

        this.healthPercent = new Percentage(100, true);
        this.healthFlat = 20;
        this.healthFinal = 20;
        this.currentHealh = 20;
        this.healingRate = new Percentage(100, true);
        this.effHealingRate = new Percentage(100, true);
        this.regenPerSec = 0;
        this.regenPerSecPercent = new Percentage(100, true);
        this.lifeDrainOnCrit = 0;
        this.lifeDrainOnCritPercent = new Percentage(100, true);

        this.meleeProt = 0;
        this.projectileProt = 0;
        this.magicProt = 0;
        this.blastProt = 0;
        this.fireProt = 0;
        this.fallProt = 0;
        this.ailmentProt = 0;

        this.meleeFragility = 0;
        this.projectileFragility = 0;
        this.magicFragility = 0;
        this.blastFragility = 0;
        this.fireFragility = 0;

        this.meleeHNDR = new Percentage(0, true);
        this.projectileHNDR = new Percentage(0, true);
        this.magicHNDR = new Percentage(0, true);
        this.blastHNDR = new Percentage(0, true);
        this.fireHNDR = new Percentage(0, true);
        this.fallHNDR = new Percentage(0, true);
        this.ailmentHNDR = new Percentage(0, true);

        this.meleeDR = new Percentage(0, true);
        this.projectileDR = new Percentage(0, true);
        this.magicDR = new Percentage(0, true);
        this.blastDR = new Percentage(0, true);
        this.fireDR = new Percentage(0, true);
        this.fallDR = new Percentage(0, true);
        this.ailmentDR = new Percentage(0, true);

        this.meleeEHP = 0;
        this.projectileEHP = 0;
        this.magicEHP = 0;
        this.blastEHP = 0;
        this.fireEHP = 0;
        this.fallEHP = 0;
        this.ailmentEHP = 0;

        this.hasMoreArmor = false;
        this.hasMoreAgility = false;
        this.hasEqualDefenses = false;
        this.worldlyProtection = 0;

        this.attackDamagePercent = new Percentage(100, true);
        this.attackSpeedPercent = new Percentage(100, true);
        this.attackSpeed = 4;
        this.attackSpeedFlatBonus = 0;
        this.attackDamage = 1;
        this.attackDamageCrit = 1.5;
        this.iframeDPS = 2;
        this.iframeCritDPS = 3;

        this.projectileDamagePercent = new Percentage(100, true);
        this.projectileDamage = 0;
        this.projectileSpeedPercent = new Percentage(100, true);
        this.projectileSpeed = 0;
        this.throwRatePercent = new Percentage(100, true);
        this.throwRate = 0;

        this.magicDamagePercent = new Percentage(100, true);
        this.spellPowerPercent = new Percentage(100, true);
        this.spellDamage = new Percentage(100, true);
        this.spellCooldownPercent = new Percentage(100, true);

        this.aptitude = 0;
        this.ineptitude = 0;
        this.crippling = 0;
        this.corruption = 0;

        this.twoHanded = allItemStats.containsKey("two_handed");
        this.weightless = allItemStats.containsKey("weightless");
    }
}
