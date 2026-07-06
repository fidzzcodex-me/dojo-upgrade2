package com.dojo.plugin.mobs;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class GuardianSpawner {

    private final JavaPlugin plugin;
    private final Random random = new Random();

    private World world;
    private double centerX;
    private double centerZ;
    private double radius;
    private int minDelaySeconds;
    private int maxDelaySeconds;
    private int maxAlive;

    private int currentlyAlive = 0;

    public GuardianSpawner(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void configure(World world, double centerX, double centerZ, double radius,
                           int minDelaySeconds, int maxDelaySeconds, int maxAlive) {
        this.world = world;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radius = radius;
        this.minDelaySeconds = minDelaySeconds;
        this.maxDelaySeconds = maxDelaySeconds;
        this.maxAlive = maxAlive;
    }

    public void start() {
        scheduleNext();
    }

    private void scheduleNext() {
        int delay = minDelaySeconds + random.nextInt(Math.max(1, maxDelaySeconds - minDelaySeconds));
        new BukkitRunnable() {
            @Override
            public void run() {
                trySpawn();
                scheduleNext();
            }
        }.runTaskLater(plugin, delay * 20L);
    }

    private void trySpawn() {
        if (world == null) return;
        if (currentlyAlive >= maxAlive) return;

        double angle = random.nextDouble() * Math.PI * 2;
        double dist = random.nextDouble() * radius;
        double x = centerX + Math.cos(angle) * dist;
        double z = centerZ + Math.sin(angle) * dist;
        int y = world.getHighestBlockYAt((int) x, (int) z) + 1;

        Location loc = new Location(world, x, y, z);

        int tier = weightedRandomTier();

        LivingEntity entity = Kurayami.spawn(loc, tier);
        currentlyAlive++;

        plugin.getLogger().info("Spawned Dojo Guardian tier " + tier + " at " + (int) x + "," + y + "," + (int) z);
    }

    public void onGuardianRemoved() {
        currentlyAlive = Math.max(0, currentlyAlive - 1);
    }

    private int weightedRandomTier() {
        int roll = random.nextInt(100);
        if (roll < 40) return 1 + random.nextInt(3);
        if (roll < 70) return 4 + random.nextInt(3);
        if (roll < 92) return 7 + random.nextInt(3);
        return 10;
    }
}
