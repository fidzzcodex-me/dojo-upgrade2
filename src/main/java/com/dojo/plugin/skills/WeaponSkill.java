package com.dojo.plugin.skills;

public enum WeaponSkill {

    IAIDO_SLASH(
            "Iaido Slash",
            "§7Dash forward and unleash a",
            "§7piercing line slash.",
            8.0,
            1.2,
            10,
            8
    ),
    PIERCING_THRUST(
            "Piercing Thrust",
            "§7Thrust forward, impaling",
            "§7and hurling enemies back.",
            10.0,
            2.0,
            6,
            12
    ),
    WHIRLWIND_STRIKE(
            "Whirlwind Strike",
            "§7Spin rapidly, striking all",
            "§7nearby enemies at once.",
            5.0,
            0.6,
            25,
            10
    );

    private final String displayName;
    private final String loreLine1;
    private final String loreLine2;
    private final double damage;
    private final double knockbackStrength;
    private final int stunTicks;
    private final int cooldownSeconds;

    WeaponSkill(String displayName, String loreLine1, String loreLine2, double damage,
                double knockbackStrength, int stunTicks, int cooldownSeconds) {
        this.displayName = displayName;
        this.loreLine1 = loreLine1;
        this.loreLine2 = loreLine2;
        this.damage = damage;
        this.knockbackStrength = knockbackStrength;
        this.stunTicks = stunTicks;
        this.cooldownSeconds = cooldownSeconds;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLoreLine1() {
        return loreLine1;
    }

    public String getLoreLine2() {
        return loreLine2;
    }

    public double getDamage() {
        return damage;
    }

    public double getKnockbackStrength() {
        return knockbackStrength;
    }

    public int getStunTicks() {
        return stunTicks;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }
}
