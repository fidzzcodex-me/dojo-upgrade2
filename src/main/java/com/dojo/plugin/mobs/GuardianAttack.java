package com.dojo.plugin.mobs;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class GuardianAttack {

    private static final Random random = new Random();
    private static final Map<UUID, Long> lastAttackTime = new HashMap<>();

    private static final int TELEGRAPH_TICKS = 20;
    private static final int MIN_COOLDOWN_MS = 4000;
    private static final int MAX_COOLDOWN_MS = 7000;

    public static void tryAttack(JavaPlugin plugin, LivingEntity guardian) {
        if (!Kurayami.isGuardian(guardian)) return;

        UUID id = guardian.getUniqueId();
        Long last = lastAttackTime.get(id);
        long now = System.currentTimeMillis();
        if (last != null && now - last < MIN_COOLDOWN_MS) return;

        Player target = findNearestPlayer(guardian, 12.0);
        if (target == null) return;

        lastAttackTime.put(id, now + random.nextInt(MAX_COOLDOWN_MS - MIN_COOLDOWN_MS));

        int tier = Kurayami.getTier(guardian);
        boolean useCircle = random.nextBoolean();

        if (useCircle) {
            telegraphCircle(plugin, guardian, target, tier);
        } else {
            telegraphLine(plugin, guardian, target, tier);
        }
    }

    private static Player findNearestPlayer(LivingEntity guardian, double range) {
        Player closest = null;
        double closestDist = range;
        for (org.bukkit.entity.Entity entity : guardian.getWorld().getNearbyEntities(guardian.getLocation(), range, range, range)) {
            if (entity instanceof Player player) {
                double dist = player.getLocation().distance(guardian.getLocation());
                if (dist < closestDist) {
                    closest = player;
                    closestDist = dist;
                }
            }
        }
        return closest;
    }

    private static void telegraphCircle(JavaPlugin plugin, LivingEntity guardian, Player target, int tier) {
        double radius = 1.5 + (tier * 0.35);
        Location center = target.getLocation();
        double damage = 2.0 + (tier * 0.8);

        guardian.getWorld().playSound(center, Sound.ENTITY_ZOMBIE_AMBIENT, 1.0f, 0.5f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= TELEGRAPH_TICKS) {
                    detonateCircle(guardian, center, radius, damage);
                    cancel();
                    return;
                }
                drawCircleOutline(center, radius);
                ticks += 2;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private static void telegraphLine(JavaPlugin plugin, LivingEntity guardian, Player target, int tier) {
        double length = 4.0 + (tier * 0.9);
        double damage = 2.5 + (tier * 0.9);
        Location origin = guardian.getLocation();
        Vector direction = target.getLocation().toVector().subtract(origin.toVector()).normalize();

        guardian.getWorld().playSound(origin, Sound.ENTITY_ZOMBIE_AMBIENT, 1.0f, 0.6f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= TELEGRAPH_TICKS) {
                    detonateLine(guardian, origin, direction, length, damage);
                    cancel();
                    return;
                }
                drawLineOutline(origin, direction, length);
                ticks += 2;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private static void drawCircleOutline(Location center, double radius) {
        int points = 24;
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI * i) / points;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location point = new Location(center.getWorld(), x, center.getY() + 0.1, z);
            center.getWorld().spawnParticle(
                    Particle.REDSTONE,
                    point,
                    2, 0.02, 0.02, 0.02, 0,
                    new Particle.DustOptions(Color.fromRGB(220, 20, 20), 1.8f)
            );
        }
    }

    private static void drawLineOutline(Location origin, Vector direction, double length) {
        int steps = (int) (length * 3);
        for (int i = 0; i < steps; i++) {
            double dist = (length / steps) * i;
            Location point = origin.clone().add(direction.clone().multiply(dist)).add(0, 0.1, 0);
            origin.getWorld().spawnParticle(
                    Particle.REDSTONE,
                    point,
                    2, 0.05, 0.02, 0.05, 0,
                    new Particle.DustOptions(Color.fromRGB(220, 20, 20), 1.8f)
            );
        }
    }

    private static void detonateCircle(LivingEntity guardian, Location center, double radius, double damage) {
        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 1.3f);
        center.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, center, 1);

        for (org.bukkit.entity.Entity entity : center.getWorld().getNearbyEntities(center, radius, 2, radius)) {
            if (entity instanceof Player player) {
                double dist = player.getLocation().distance(center);
                if (dist <= radius) {
                    player.damage(damage, guardian);
                }
            }
        }
    }

    private static void detonateLine(LivingEntity guardian, Location origin, Vector direction, double length, double damage) {
        origin.getWorld().playSound(origin, Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 1.5f);

        for (org.bukkit.entity.Entity entity : origin.getWorld().getNearbyEntities(origin, length, 3, length)) {
            if (entity instanceof Player player) {
                Vector toPlayer = player.getLocation().toVector().subtract(origin.toVector());
                double dist = toPlayer.length();
                if (dist > length) continue;
                double angle = direction.angle(toPlayer.normalize());
                if (angle < Math.toRadians(15)) {
                    player.damage(damage, guardian);
                }
            }
        }
    }

    public static void cleanup(UUID guardianId) {
        lastAttackTime.remove(guardianId);
    }
}
