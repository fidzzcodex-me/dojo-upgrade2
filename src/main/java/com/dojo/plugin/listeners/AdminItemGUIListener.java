package com.dojo.plugin.listeners;

import com.dojo.plugin.gui.AdminItemGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AdminItemGUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(AdminItemGUI.TITLE)) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack toGive = clicked.clone();
        player.getInventory().addItem(toGive);
        player.sendMessage("§6§lDOJO §7» §aGave " + toGive.getAmount() + "x " + toGive.getItemMeta().getDisplayName() + "§a.");
    }
}
