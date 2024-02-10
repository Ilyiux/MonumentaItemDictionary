package dev.eliux.monumentaitemdictionary.util;

import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;

import java.util.*;

import static java.lang.Math.*;
public class Stats {
    // Misc Stats
    public double armor;
    public double agility;
    public Percentage speedPercent;
    public int knockbackRes;
    public int thorns;
    public double fireTickDamage;
    // Health Stats
    public double healthFinal;
    public double currentHealth;
    public Percentage effHealingRate;
    public Percentage healingRate;
    public double regenPerSec;
    public Percentage regenPerSecPercent;
    public double lifeDrainOnCrit;
    public Percentage lifeDrainOnCritPercent;
    // DR Stats
    public Percentage meleeDR;
    public Percentage projectileDR;
    public Percentage magicDR;
    public Percentage blastDR;
    public Percentage fireDR;
    public Percentage fallDR;
    public Percentage ailmentDR;
    // Health Normalized DR Stats
    public Percentage meleeHNDR;
    public Percentage projectileHNDR;
    public Percentage magicHNDR;
    public Percentage blastHNDR;
    public Percentage fireHNDR;
    public Percentage fallHNDR;
    public Percentage ailmentHNDR;
    // EHP Stats
    public double meleeEHP;
    public double projectileEHP;
    public double magicEHP;
    public double blastEHP;
    public double fallEHP;
    public double fireEHP;
    public double ailmentEHP;
    // Melee Stats
    public Percentage attackSpeedPercent;
    public double attackSpeed;
    public Percentage attackDamagePercent;
    public double attackDamage;
    public double attackDamageCrit;
    public double iframeDPS;
    public double iframeCritDPS;
    // Projectile Stats
    public Percentage projectileDamagePercent;
    public double projectileDamage;
    public Percentage projectileSpeedPercent;
    public double projectileSpeed;
    public Percentage throwRatePercent;
    public double throwRate;
    // Magic Stats
    public Percentage magicDamagePercent;
    public Percentage spellPowerPercent;
    public Percentage spellDamage;
    public Percentage spellCooldownPercent;
    private Percentage currentHealthPercent;
    private double speedFlat;
    private Percentage healthPercent;
    private double attackSpeedFlatBonus;
    private double healthFlat;
    private double meleeProt;
    private double projectileProt;
    private double magicProt;
    private double blastProt;
    private double fireProt;
    private double fallProt;
    private double meleeFragility;
    private double projectileFragility;
    private double magicFragility;
    private double blastFragility;
    private double fireFragility;
    private double worldlyProtection;
    private Percentage thornsPercent;
    private double ailmentProt;
    private double aptitude;
    private double ineptitude;
    private double crippling;
    private double corruption;
    private final double vitality;
    private final double tenacity;
    private final double vigor;
    private final double focus;
    private final double perspicacity;
    private final Map<String, Integer> situationalsLevels;
    private final Map<String, Boolean> enabledSituationals;
    private final Map<String, List<ItemStat>> allItemStats = new HashMap<>();

    public Stats(List<DictionaryItem> items, Map<String, Boolean> enabledSituationals, Map<String, Integer> infusions, double currentHealthPercent) {
        this.enabledSituationals = enabledSituationals;
        vitality = (infusions.get("vitality") > 0) ? infusions.get("vitality") : 0;
        tenacity = (infusions.get("tenacity") > 0) ? infusions.get("tenacity") : 0;
        vigor = (infusions.get("vigor") > 0) ? infusions.get("vigor") : 0;
        focus = (infusions.get("focus") > 0) ? infusions.get("focus") : 0;
        perspicacity = (infusions.get("perspicacity") > 0) ? infusions.get("perspicacity") : 0;

        this.currentHealthPercent = new Percentage(currentHealthPercent, true);

        this.situationalsLevels = new HashMap<>() {{
            put("shielding", 0);
            put("poise", 0);
            put("inure", 0);
            put("steadfast", 0);
            put("guard", 0);
            put("ethereal", 0);
            put("reflexes", 0);
            put("evasion", 0);
            put("tempo", 0);
            put("cloaked", 0);
            put("adaptability", 0);
            put("second_wind", 0);
            put("versatile", 0);
        }};

        List<String> itemTypes = new ArrayList<>(Arrays.asList("mainhand", "offhand", "helmet", "chestplate", "leggings", "boots"));
        for (int i = 0; i< itemTypes.size(); i++) {
            if (items.get(i) != null) {
                DictionaryItem item = items.get(i);
                List<ItemStat> itemStats = item.hasMasterwork ? item.getStatsFromMasterwork(item.getMaxMasterwork() - 1) : item.getStatsNoMasterwork();
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
        if (enabledSituationals.get("versatile")) {
            double extraAttackDamagePercent = (projectileDamagePercent.perc - 100) * 0.5;
            double extraProjectileDamagePercent = (attackDamagePercent.perc - 100) * 0.4;
            attackDamagePercent.add(extraAttackDamagePercent, true);
            projectileDamagePercent.add(extraProjectileDamagePercent, true);
        }

        attackDamage = sumNumberStat(mainhand, "attack_damage_base", attackDamage)
                * attackDamagePercent.val
                * (1 + 0.0075 * vigor)
                * ((currentHealthPercent.perc <= 50) ? 1 - 0.1 * crippling : 1);
        attackSpeed = (sumNumberStat(mainhand, "attack_speed_base", attackSpeed) + attackSpeedFlatBonus) * attackDamagePercent.val;
        attackDamageCrit = (attackDamage * 1.5);
        iframeDPS = ((attackSpeed >= 2) ? attackDamage * 2 : attackDamage * attackSpeed);
        iframeCritDPS = ((attackSpeed >= 2) ? attackDamageCrit * 2 : attackDamageCrit * attackSpeed);

        projectileDamage = sumNumberStat(mainhand, "projectile_damage_base", projectileDamage)
                * projectileDamagePercent.val
                * (1 + 0.0075 * focus);
        projectileSpeed = sumNumberStat(mainhand, "projectile_speed_base", projectileSpeed) * projectileDamagePercent.val;
        throwRate = sumNumberStat(mainhand, "throw_rate_base", throwRate) * throwRatePercent.val;

        spellPowerPercent.add(sumNumberStat(mainhand, "spell_power_base", 0), true);
        spellDamage = (
                spellPowerPercent.duplicate()
                        .mulP(magicDamagePercent)
                        .mul(1 + 0.0075 * perspicacity, false)
                );
        spellCooldownPercent = spellCooldownPercent.mul(pow(0.95, aptitude + ineptitude), false);
            }

    private void calculateDefenseStats() {
        Map<String, Map<String, Percentage>> drs =  calculateDamageReductions();

        String drType = (enabledSituationals.get("second_wind")) ? "second_wind" : "base";

        meleeDR = drs.get("melee").get(drType);
        projectileDR = drs.get("projectile").get(drType);
        magicDR = drs.get("magic").get(drType);
        blastDR = drs.get("blast").get(drType);
        fireDR = drs.get("fire").get(drType);
        fallDR = drs.get("fall").get(drType);
        ailmentDR = drs.get("ailment").get(drType);

        if (situationalsLevels.get("second_wind") == 0 || drType.equals("base")) {
            meleeEHP = (healthFinal * currentHealthPercent.val / (1 - drs.get("melee").get("base").val));
            projectileEHP = (healthFinal * currentHealthPercent.val / (1 - drs.get("projectile").get("base").val));
            magicEHP = (healthFinal * currentHealthPercent.val / (1 - drs.get("magic").get("base").val));
            blastEHP = (healthFinal * currentHealthPercent.val / (1 - drs.get("blast").get("base").val));
            fireEHP = (healthFinal * currentHealthPercent.val / (1 - drs.get("fire").get("base").val));
            fallEHP = (healthFinal * currentHealthPercent.val / (1 - drs.get("fall").get("base").val));
            ailmentEHP = (healthFinal * currentHealthPercent.val / (1 - drs.get("ailment").get("base").val));
        } else {
            double hpNoSecondWind = max(0, (currentHealth - healthFinal * 0.5));
            double hpSecondWind = min(currentHealth, healthFinal * 0.5);
            meleeEHP = (hpNoSecondWind / (1 - drs.get("melee").get("base").val) + hpSecondWind / (1 - drs.get("melee").get("second_wind").val));
            projectileEHP = (hpNoSecondWind / (1 - drs.get("projectile").get("base").val) + hpSecondWind / (1 - drs.get("projectile").get("second_wind").val));
            magicEHP = (hpNoSecondWind / (1 - drs.get("magic").get("base").val) + hpSecondWind / (1 - drs.get("magic").get("second_wind").val));
            blastEHP = (hpNoSecondWind / (1 - drs.get("blast").get("base").val) + hpSecondWind / (1 - drs.get("blast").get("second_wind").val));
            fireEHP = (hpNoSecondWind / (1 - drs.get("fire").get("base").val) + hpSecondWind / (1 - drs.get("fire").get("second_wind").val));
            fallEHP = (hpNoSecondWind / (1 - drs.get("fall").get("base").val) + hpSecondWind / (1 - drs.get("fall").get("second_wind").val));
            ailmentEHP = (hpNoSecondWind / (1 - drs.get("ailment").get("base").val) + hpSecondWind / (1 - drs.get("ailment").get("second_wind").val));
        }

        meleeHNDR = new Percentage((1 - ((1 - drs.get("melee").get(drType).val) / (healthFinal / 20))), false);
        projectileHNDR = new Percentage((1 - ((1 - drs.get("projectile").get(drType).val) / (healthFinal / 20))), false);
        magicHNDR = new Percentage((1 - ((1 - drs.get("magic").get(drType).val) / (healthFinal / 20))), false);
        blastHNDR = new Percentage((1 - ((1 - drs.get("blast").get(drType).val) / (healthFinal / 20))), false);
        fireHNDR = new Percentage((1 - ((1 - drs.get("fire").get(drType).val) / (healthFinal / 20))), false);
        fallHNDR = new Percentage((1 - ((1 - drs.get("fall").get(drType).val) / (healthFinal / 20))), false);
        ailmentHNDR = new Percentage((1 - ((1 - drs.get("ailment").get(drType).val) / (healthFinal / 20))), false);
    }

    private Map<String, Map<String, Percentage>> calculateDamageReductions() {
        double armor = Math.max(this.armor, 0);
        double agility = Math.max(this.agility, 0);

        boolean moreAgility = agility > armor;
        boolean moreArmor = armor > agility;
        boolean hasEqual = armor == agility;
        boolean hasNothing = (hasEqual && armor == 0);

        double situationalArmor = (enabledSituationals.get("adaptability")) ? min(max(agility, armor), 30) * 0.2 : min(armor, 30) * 0.2;
        double situationalAgility = (enabledSituationals.get("adaptability")) ? min(max(agility, armor), 30) * 0.2 : min(agility, 30) * 0.2;

        double etherealSit =    (enabledSituationals.get("ethereal")) ? situationalAgility * situationalsLevels.get("ethereal") : 0;
        double tempoSit =       (enabledSituationals.get("tempo")) ? situationalAgility * situationalsLevels.get("tempo") : 0;
        double evasionSit =     (enabledSituationals.get("evasion")) ? situationalAgility * situationalsLevels.get("evasion") : 0;
        double reflexesSit =    (enabledSituationals.get("reflexes")) ? situationalAgility * situationalsLevels.get("reflexes") : 0;
        double shieldingSit =   (enabledSituationals.get("shielding")) ? situationalArmor * situationalsLevels.get("shielding") : 0;
        double poiseSit =       (enabledSituationals.get("poise")) ? ((currentHealthPercent.val >= 0.9) ? situationalArmor * situationalsLevels.get("poise") : 0) : 0;
        double inureSit =       (enabledSituationals.get("inure")) ? situationalArmor * situationalsLevels.get("inure") : 0;
        double guardSit =       (enabledSituationals.get("guard")) ? situationalArmor * situationalsLevels.get("guard") : 0;
        double cloakedSit =     (enabledSituationals.get("cloaked")) ? situationalAgility * situationalsLevels.get("cloaked") : 0;

        double steadfastScaling = 0.33;
        double steadfastMaxScaling = 20;
        double steadfastLowerBound = 1 - (steadfastMaxScaling / steadfastScaling / 100);
        double steadfastArmor = (1 - max(steadfastLowerBound, min(1, currentHealthPercent.val))) * steadfastScaling *
                min(((enabledSituationals.get("adaptability") && moreAgility) ? agility : (moreArmor) ? armor : (situationalsLevels.get("adaptability") == 0) ? armor : 0), 30);
        double steadfastSit = (enabledSituationals.get("steadfast")) ? steadfastArmor * situationalsLevels.get("steadfast") : 0;

        double sumSits = etherealSit + tempoSit + evasionSit + reflexesSit + shieldingSit + poiseSit + inureSit + guardSit + cloakedSit;
        double sumArmorSits = shieldingSit + poiseSit + inureSit + guardSit;
        double sumAgiSits = etherealSit + tempoSit + evasionSit + reflexesSit + cloakedSit;

        double armorPlusSits = armor;
        if (enabledSituationals.get("adaptability")) {
            if (moreArmor) {
                armorPlusSits += sumSits;
            }
        } else {
            armorPlusSits += sumArmorSits;
        }
        double armorPlusSitsSteadfast = armorPlusSits + steadfastSit;

        double agilityPlusSits = agility;
        if (enabledSituationals.get("adaptability")) {
            if (moreAgility) {
                agilityPlusSits += sumSits;
            }
        } else {
            agilityPlusSits += sumAgiSits;
        }

        double halfArmor = armorPlusSitsSteadfast / 2;
        double halfAgility = agilityPlusSits / 2;

        Map<String, Double> meleeDamage =       calculateDamageTaken(hasNothing,    meleeProt,         meleeFragility,        2, armorPlusSitsSteadfast,  agilityPlusSits);
        Map<String, Double> projectileDamage =  calculateDamageTaken(hasNothing,    projectileProt,    projectileFragility,   2, armorPlusSitsSteadfast,  agilityPlusSits);
        Map<String, Double> magicDamage =       calculateDamageTaken(hasNothing,    magicProt,         magicFragility,        2, armorPlusSitsSteadfast,  agilityPlusSits);
        Map<String, Double> blastDamage =       calculateDamageTaken(hasNothing,    blastProt,         blastFragility,        2, armorPlusSitsSteadfast,  agilityPlusSits);
        Map<String, Double> fireDamage =        calculateDamageTaken(hasNothing,    fireProt,          fireFragility,         2, halfArmor,               halfAgility);
        Map<String, Double> fallDamage =        calculateDamageTaken(hasNothing,    fallProt,          0,                          3, halfArmor,               halfAgility);
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

        double baseDmg = (noArmor) ? 100 * (1 - worldlyProtection * 0.1) * pow(0.96, (prot * protModifier - fragility * protModifier)) :
                100 * (1 - worldlyProtection * 0.1) * pow(0.96, ((prot * protModifier - fragility * protModifier) + earmor + eagility) - (0.5 * earmor * eagility / (earmor + eagility))) * (1 - (tenacity * 0.005));
        double secondwindDmg = baseDmg * pow(0.9, situationalsLevels.get("second_wind")) * (1-(tenacity * 0.005));


        damageTaken.put("base", baseDmg);
        damageTaken.put("second_wind", secondwindDmg);

        return damageTaken;
    }

    private void adjustStats() {
        healthFinal = healthFlat * healthPercent.val * (1 + 0.01*vitality);
        currentHealth = healthFinal * currentHealthPercent.val;
        speedPercent = speedPercent
                .mul((speedFlat)/0.1, false)
                .mul(((currentHealthPercent.perc <= 50) ? 1 - 0.1 * crippling : 1), false);
        knockbackRes = (knockbackRes > 10) ? 100 : knockbackRes * 10;

        effHealingRate = new Percentage(((20 / healthFinal) * healingRate.val), false);
        regenPerSec = 0.33 * sqrt(regenPerSec) * healingRate.val;
        regenPerSecPercent = new Percentage((regenPerSec / healthFinal), false);

        lifeDrainOnCrit = (sqrt(lifeDrainOnCrit)) * healingRate.val;
        lifeDrainOnCritPercent = new Percentage((lifeDrainOnCrit / healthFinal), false);

        thorns *= (double) thornsPercent.val;
    }

    private Double sumNumberStat(List<ItemStat> itemStats, String statName, double defaultIncrement) {
        if (itemStats == null) return 0.0;
        double statValue = 0.0;
        for (ItemStat stat : itemStats) {
            if (stat.statName.equals(statName)) {
                statValue = stat.statValue;
                break;
            }
        }
        return (statValue != 0.0) ? statValue : defaultIncrement;
    }

    private double sumEnchantmentStat(List<ItemStat> itemStats, String enchName, double perLevelMultiplier) {
        if (itemStats == null) return perLevelMultiplier;
        double enchLevel = 0.0;
        for (ItemStat stat : itemStats) {
            if (stat.statName.equals(enchName)) {
                enchLevel = stat.statValue;
                break;
            }
        }
        return (enchLevel != 0.0) ? enchLevel * perLevelMultiplier : perLevelMultiplier;
    }
    private void sumAllStats() {
        allItemStats.keySet().forEach(type -> {
            List<ItemStat> itemStats = allItemStats.get(type);
            healthPercent.add(sumNumberStat(itemStats, "max_health_percent", 0), true);
            healthFlat += sumNumberStat(itemStats, "max_health_flat", 0);
            agility += sumNumberStat(itemStats, "agility", 0);
            armor += sumNumberStat(itemStats, "armor", 0);
            speedPercent.add(sumNumberStat(itemStats, "speed_percent", 0), true);
            speedFlat += sumNumberStat(itemStats, "speed_flat", 0);
            knockbackRes += sumNumberStat(itemStats, "knockback_resistance_flat", 0);
            thorns += sumNumberStat(itemStats, "thorns_flat", 0);
            throwRatePercent.add(sumNumberStat(itemStats, "throw_rate_percent", 0), true);
            fireTickDamage += sumNumberStat(itemStats, "inferno", 0);

            healingRate
                    .add(sumEnchantmentStat(itemStats, "curse_of_anemia", -10), true)
                    .add(sumEnchantmentStat(itemStats, "sustenance", 10), true);
            regenPerSec += sumEnchantmentStat(itemStats, "regeneration", 1);
            lifeDrainOnCrit += sumEnchantmentStat(itemStats, "life_drain", 1);

            meleeProt += sumNumberStat(itemStats, "melee_protection", 0);
            projectileProt += sumNumberStat(itemStats, "projectile_protection", 0);
            magicProt += sumNumberStat(itemStats, "magic_protection", 0);
            blastProt += sumNumberStat(itemStats, "blast_protection", 0);
            fireProt += sumNumberStat(itemStats, "fire_protection", 0);
            fallProt += sumNumberStat(itemStats, "feather_falling", 0);

            meleeFragility += sumNumberStat(itemStats, "melee_fragility", 0);
            projectileFragility += sumNumberStat(itemStats, "projectile_fragility", 0);
            magicFragility += sumNumberStat(itemStats, "magic_fragility", 0);
            blastFragility += sumNumberStat(itemStats, "blast_fragility", 0);
            fireFragility += sumNumberStat(itemStats, "fire_fragility", 0);

            attackDamagePercent.add(sumNumberStat(itemStats, "attack_damage_percent", 0), true);
            attackSpeedPercent.add(sumNumberStat(itemStats, "attack_speed_percent", 0), true);
            attackSpeedFlatBonus += sumNumberStat(itemStats, "attack_speed_flat", 0);

            projectileDamagePercent.add(sumNumberStat(itemStats, "projectile_damage_percent", 0), true);
            projectileSpeedPercent.add(sumNumberStat(itemStats, "projectile_speed_percent", 0), true);

            magicDamagePercent.add(sumNumberStat(itemStats, "magic_damage_percent", 0), true);

            aptitude += (double) sumEnchantmentStat(itemStats, "aptitude", 1);
            ineptitude += (double) sumEnchantmentStat(itemStats, "ineptitude", -1);

            worldlyProtection += sumNumberStat(itemStats, "worldly_protection", 0);

            situationalsLevels.put("shielding", (int) (situationalsLevels.get("shielding") + sumNumberStat(itemStats, "shielding", 0))) ;
            situationalsLevels.put("poise", (int) (situationalsLevels.get("poise") + sumNumberStat(itemStats, "poise", 0)));
            situationalsLevels.put("inure", (int) (situationalsLevels.get("inure") + sumNumberStat(itemStats, "inure", 0)));
            situationalsLevels.put("steadfast", (int) (situationalsLevels.get("steadfast") + sumNumberStat(itemStats, "steadfast", 0)));
            situationalsLevels.put("guard", (int) (situationalsLevels.get("guard") + sumNumberStat(itemStats, "guard", 0)));
            situationalsLevels.put("ethereal", (int) (situationalsLevels.get("ethereal") + sumNumberStat(itemStats, "ethereal", 0)));
            situationalsLevels.put("reflexes", (int) (situationalsLevels.get("reflexes") + sumNumberStat(itemStats, "reflexes", 0)));
            situationalsLevels.put("evasion", (int) (situationalsLevels.get("evasion") + sumNumberStat(itemStats, "evasion", 0)));
            situationalsLevels.put("tempo", (int) (situationalsLevels.get("tempo") + sumNumberStat(itemStats, "tempo", 0)));
            situationalsLevels.put("cloaked", (int) (situationalsLevels.get("cloaked") + sumNumberStat(itemStats, "cloaked", 0)));
            situationalsLevels.put("adaptability", (int) (situationalsLevels.get("adaptability") + sumNumberStat(itemStats, "adaptability", 0)));
            situationalsLevels.put("second_wind", (int) (situationalsLevels.get("second_wind") + sumNumberStat(itemStats, "second_wind", 0)));

            crippling += sumNumberStat(itemStats, "curse_of_crippling", 0);
            corruption += sumNumberStat(itemStats, "curse_of_corruption", 0);
        });
    }
    private void setDefaultValues() {
        agility = 0;
        armor = 0;
        speedPercent = new Percentage(100, true);
        speedFlat = 0.1;
        knockbackRes = 0;
        thorns = 0;
        fireTickDamage = 1;
        thornsPercent = new Percentage(100, true);

        healthPercent = new Percentage(100, true);
        healthFlat = 20;
        healthFinal = 20;
        currentHealth = 20;
        healingRate = new Percentage(100, true);
        effHealingRate = new Percentage(100, true);
        regenPerSec = 0;
        regenPerSecPercent = new Percentage(0, true);
        lifeDrainOnCrit = 0;
        lifeDrainOnCritPercent = new Percentage(0, true);

        meleeProt = 0;
        projectileProt = 0;
        magicProt = 0;
        blastProt = 0;
        fireProt = 0;
        fallProt = 0;
        ailmentProt = 0;

        meleeFragility = 0;
        projectileFragility = 0;
        magicFragility = 0;
        blastFragility = 0;
        fireFragility = 0;

        meleeHNDR = new Percentage(0, true);
        projectileHNDR = new Percentage(0, true);
        magicHNDR = new Percentage(0, true);
        blastHNDR = new Percentage(0, true);
        fireHNDR = new Percentage(0, true);
        fallHNDR = new Percentage(0, true);
        ailmentHNDR = new Percentage(0, true);

        meleeDR = new Percentage(0, true);
        projectileDR = new Percentage(0, true);
        magicDR = new Percentage(0, true);
        blastDR = new Percentage(0, true);
        fireDR = new Percentage(0, true);
        fallDR = new Percentage(0, true);
        ailmentDR = new Percentage(0, true);

        meleeEHP = 0;
        projectileEHP = 0;
        magicEHP = 0;
        blastEHP = 0;
        fireEHP = 0;
        fallEHP = 0;
        ailmentEHP = 0;

        worldlyProtection = 0;

        attackDamagePercent = new Percentage(100, true);
        attackSpeedPercent = new Percentage(100, true);
        attackSpeed = 4;
        attackSpeedFlatBonus = 0;
        attackDamage = 1;
        attackDamageCrit = 1.5;
        iframeDPS = 2;
        iframeCritDPS = 3;

        projectileDamagePercent = new Percentage(100, true);
        projectileDamage = 0;
        projectileSpeedPercent = new Percentage(100, true);
        projectileSpeed = 0;
        throwRatePercent = new Percentage(100, true);
        throwRate = 0;

        magicDamagePercent = new Percentage(100, true);
        spellPowerPercent = new Percentage(100, true);
        spellDamage = new Percentage(100, true);
        spellCooldownPercent = new Percentage(100, true);

        aptitude = 0;
        ineptitude = 0;
        crippling = 0;
        corruption = 0;
    }
}
