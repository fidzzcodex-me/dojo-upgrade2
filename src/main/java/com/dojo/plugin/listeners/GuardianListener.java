package com.dojo.plugin.listeners;

import com.dojo.plugin.mobs.Kurayami;
import com.dojo.plugin.mobs.GuardianSpawner;
import com.dojo.plugin.mobs.LootManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GuardianListener implements Listener {

    private final GuardianSpawner spawner;
    private static final double VISIBILITY_RANGE = 30.0;

    public GuardianListener(GuardianSpawner spawner) {
        this.spawner = spawner;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!Kurayami.isGuardian(entity)) return;

        entity.getWorld().spawnParticle(
                org.bukkit.Particle.REDSTONE,
                entity.getLocation().add(0, 1, 0),
                15, 0.3, 0.3, 0.3,
                new org.bukkit.Particle.DustOptions(org.bukkit.Color.fromRGB(180, 0, 0), 1.5f)
        );

        Kurayami.updateBossBar(entity);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!Kurayami.isGuardian(entity)) return;

        int tier = Kurayami.getTier(entity);
        event.getDrops().clear();
        LootManager.dropLoot(entity.getLocation(), tier);

        Kurayami.cleanup(entity);
        com.dojo.plugin.mobs.GuardianAttack.cleanup(entity.getUniqueId());
        spawner.onGuardianRemoved();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        player.getWorld().getNearbyEntities(player.getLocation(), VISIBILITY_RANGE, VISIBILITY_RANGE, VISIBILITY_RANGE)
                .forEach(nearby -> {
                    if (nearby instanceof LivingEntity living && Kurayami.isGuardian(living)) {
                        Kurayami.addViewer(living, player);
                    }
                });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.getWorld().getEntities().forEach(nearby -> {
            if (nearby instanceof LivingEntity living && Kurayami.isGuardian(living)) {
                Kurayami.removeViewer(living, player);
            }
        });
    }
}
