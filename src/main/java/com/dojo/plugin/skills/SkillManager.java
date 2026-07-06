package com.dojo.plugin.skills;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public SkillManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isOnCooldown(Player player, WeaponSkill skill) {
        Long expiry = cooldowns.get(player.getUniqueId());
        return expiry != null && expiry > System.currentTimeMillis();
    }

    public long getRemainingSeconds(Player player) {
        Long expiry = cooldowns.get(player.getUniqueId());
        if (expiry == null) return 0;
        long remaining = (expiry - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    private void setCooldown(Player player, WeaponSkill skill) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (skill.getCooldownSeconds() * 1000L));
    }

    public void useSkill(Player player, WeaponSkill skill) {
        if (isOnCooldown(player, skill)) {
            player.sendMessage("§6§lDOJO §7» §cSkill on cooldown: " + getRemainingSeconds(player) + "s remaining.");
            return;
        }

        setCooldown(player, skill);

        switch (skill) {
            case IAIDO_SLASH -> executeIaidoSlash(player, skill);
            case PIERCING_THRUST -> executePiercingThrust(player, skill);
            case WHIRLWIND_STRIKE -> executeWhirlwindStrike(player, skill);
        }
    }

    private void executeIaidoSlash(Player player, WeaponSkill skill) {
        Vector direction = player.getLocation().getDirection().normalize();
        Location origin = player.getLocation();

        player.setVelocity(direction.clone().multiply(1.4).setY(0.1));
        player.getWorld().playSound(origin, Sound.ITEM_TRIDENT_RIPTIDE_3, 1.0f, 1.6f);

        new BukkitRunnable() {
            int step = 0;

            @Override
            public void run() {
                if (step >= 6) {
                    cancel();
                    return;
                }
                Location point = origin.clone().add(direction.clone().multiply(step * 0.8)).add(0, 1, 0);
                spawnThickParticle(point, Color.fromRGB(200, 0, 0));
                step++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        for (LivingEntity target : nearbyTargetsInCone(player, 4.0, direction)) {
            damageAndStun(player, target, skill);
        }
    }

    private void executePiercingThrust(Player player, WeaponSkill skill) {
        Vector direction = player.getLocation().getDirection().normalize();
        Location origin = player.getLocation().add(0, 1, 0);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 0.8f);

        new BukkitRunnable() {
            int step = 0;

            @Override
            public void run() {
                if (step >= 8) {
                    cancel();
                    return;
                }
                Location point = origin.clone().add(direction.clone().multiply(step * 0.7));
                spawnThickParticle(point, Color.fromRGB(180, 20, 20));
                step++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        for (LivingEntity target : nearbyTargetsInCone(player, 6.0, direction)) {
            damageAndStun(player, target, skill);
            Vector knockDir = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(knockDir.multiply(skill.getKnockbackStrength()).setY(0.3));
        }
    }

    private void executeWhirlwindStrike(Player player, WeaponSkill skill) {
        Location center = player.getLocation().add(0, 1, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.2f, 1.0f);

        new BukkitRunnable() {
            double angle = 0;
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 20) {
                    cancel();
                    return;
                }
                double radius = 2.2;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                Location point = center.clone().add(x, 0, z);
                spawnThickParticle(point, Color.fromRGB(220, 30, 30));
                angle += Math.PI / 6;
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        for (LivingEntity target : player.getWorld().getNearbyEntities(player.getLocation(), 2.5, 2.5, 2.5)
                .stream().filter(e -> e instanceof LivingEntity && e != player).map(e -> (LivingEntity) e).toList()) {
            damageAndStun(player, target, skill);
        }
    }

    private void damageAndStun(Player source, LivingEntity target, WeaponSkill skill) {
        target.damage(skill.getDamage(), source);
        target.addPotionEffect(new PotionEffect(getSlowness(), skill.getStunTicks(), 250, false, true));
        target.addPotionEffect(new PotionEffect(getJumpBoost(), skill.getStunTicks(), 128, false, true));
    }

    private PotionEffectType getSlowness() {
        PotionEffectType type = Bukkit.getRegistry(PotionEffectType.class)
                .get(org.bukkit.NamespacedKey.minecraft("slowness"));
        return type != null ? type : PotionEffectType.getByName("SLOW");
    }

    private PotionEffectType getJumpBoost() {
        PotionEffectType type = Bukkit.getRegistry(PotionEffectType.class)
                .get(org.bukkit.NamespacedKey.minecraft("jump_boost"));
        return type != null ? type : PotionEffectType.getByName("JUMP");
    }

    private java.util.List<LivingEntity> nearbyTargetsInCone(Player player, double range, Vector direction) {
        java.util.List<LivingEntity> results = new java.util.ArrayList<>();
        for (org.bukkit.entity.Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), range, range, range)) {
            if (!(entity instanceof LivingEntity living) || entity == player) continue;
            Vector toTarget = living.getLocation().toVector().subtract(player.getLocation().toVector());
            if (toTarget.length() > range) continue;
            double angle = direction.angle(toTarget.normalize());
            if (angle < Math.toRadians(50)) {
                results.add(living);
            }
        }
        return results;
    }

    private void spawnThickParticle(Location location, Color color) {
        location.getWorld().spawnParticle(
                Particle.REDSTONE,
                location,
                12, 0.15, 0.15, 0.15, 0,
                new Particle.DustOptions(color, 2.8f)
        );
        location.getWorld().spawnParticle(
                Particle.CRIT,
                location,
                6, 0.1, 0.1, 0.1, 0.02
        );
    }
}
