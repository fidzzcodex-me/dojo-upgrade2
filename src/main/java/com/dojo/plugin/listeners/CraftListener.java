package com.dojo.plugin.listeners;

import com.dojo.plugin.gui.DojoCraftGUI;
import com.dojo.plugin.items.DojoItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(DojoCraftGUI.TITLE)) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String itemId = DojoItems.getItemId(clicked);
        if (itemId == null) return;

        DojoCraftGUI.Recipe recipe = DojoCraftGUI.recipeFor(itemId);
        if (recipe == null) return;

        Player player = (Player) event.getWhoClicked();

        int ownedFang = DojoCraftGUI.countMaterial(player, DojoItems.FANG_ID);
        int ownedBone = DojoCraftGUI.countMaterial(player, DojoItems.BONE_ID);
        int ownedCore = DojoCraftGUI.countMaterial(player, DojoItems.CORE_ID);

        if (ownedFang < recipe.fang() || ownedBone < recipe.bone() || ownedCore < recipe.core()) {
            player.sendMessage("§6§lDOJO §7» §cYou don't have enough materials for this weapon.");
            player.sendMessage("§7Need: §f" + recipe.fang() + "x Fang §7| §e" + recipe.bone() + "x Bone §7| §d" + recipe.core() + "x Core");
            player.sendMessage("§7Have: §f" + ownedFang + "x Fang §7| §e" + ownedBone + "x Bone §7| §d" + ownedCore + "x Core");
            return;
        }

        DojoCraftGUI.removeMaterial(player, DojoItems.FANG_ID, recipe.fang());
        if (recipe.bone() > 0) {
            DojoCraftGUI.removeMaterial(player, DojoItems.BONE_ID, recipe.bone());
        }
        if (recipe.core() > 0) {
            DojoCraftGUI.removeMaterial(player, DojoItems.CORE_ID, recipe.core());
        }

        ItemStack result = DojoCraftGUI.resultFor(itemId);
        player.getInventory().addItem(result);
        player.sendMessage("§6§lDOJO §7» §aYou crafted " + result.getItemMeta().getDisplayName() + "§a!");
    }
}
