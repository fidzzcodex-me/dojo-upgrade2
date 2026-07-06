package com.dojo.plugin.listeners;

import com.dojo.plugin.gui.DojoCraftGUI;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class NpcCrafterListener implements Listener {

    public static final String MARKER = "dojo_craftsman";
    private final NamespacedKey markerKey;

    public NpcCrafterListener(Plugin plugin) {
        this.markerKey = new NamespacedKey(plugin, MARKER);
    }

    public NamespacedKey getMarkerKey() {
        return markerKey;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) return;
        if (!villager.getPersistentDataContainer().has(markerKey, PersistentDataType.STRING)) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().openInventory(DojoCraftGUI.build());
    }
}
