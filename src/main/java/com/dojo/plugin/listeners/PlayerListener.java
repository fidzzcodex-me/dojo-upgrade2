package com.dojo.plugin.listeners;

import com.dojo.plugin.duel.DuelManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener {

    private final JavaPlugin plugin;
    private final DuelManager duelManager;

    public PlayerListener(JavaPlugin plugin, DuelManager duelManager) {
        this.plugin = plugin;
        this.duelManager = duelManager;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (duelManager.isFrozen(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (duelManager.isInDuel(player.getUniqueId())) {
            event.setKeepInventory(true);
            event.getDrops().clear();
            event.setDeathMessage(null);

            player.spigot().respawn();
            duelManager.handleDeath(player);
        }
    }
}
