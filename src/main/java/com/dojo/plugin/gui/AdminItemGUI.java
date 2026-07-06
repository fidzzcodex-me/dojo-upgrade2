package com.dojo.plugin.gui;

import com.dojo.plugin.items.DojoItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AdminItemGUI {

    public static final String TITLE = "§6§lDojo Items §7(Admin)";

    public static Inventory build() {
        Inventory inv = Bukkit.createInventory(null, 9, TITLE);

        inv.setItem(1, DojoItems.createKatana());
        inv.setItem(2, DojoItems.createYari());
        inv.setItem(3, DojoItems.createNunchaku());

        ItemStack fang = DojoItems.createFang();
        fang.setAmount(16);
        inv.setItem(5, fang);

        ItemStack bone = DojoItems.createBone();
        bone.setAmount(16);
        inv.setItem(6, bone);

        ItemStack core = DojoItems.createCore();
        core.setAmount(4);
        inv.setItem(7, core);

        return inv;
    }
}
