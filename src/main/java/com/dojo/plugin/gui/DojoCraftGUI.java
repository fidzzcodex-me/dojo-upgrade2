package com.dojo.plugin.gui;

import com.dojo.plugin.items.DojoItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DojoCraftGUI {

    public static final String TITLE = "§6§lDojo Craftsman";

    public record Recipe(int fang, int bone, int core) {}

    private static final Map<String, Recipe> RECIPES = new LinkedHashMap<>();

    static {
        RECIPES.put(DojoItems.NUNCHAKU_ID, new Recipe(24, 6, 0));
        RECIPES.put(DojoItems.KATANA_ID, new Recipe(32, 14, 1));
        RECIPES.put(DojoItems.YARI_ID, new Recipe(40, 20, 2));
    }

    public static Inventory build() {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        inv.setItem(10, craftEntry(DojoItems.createNunchaku(), DojoItems.NUNCHAKU_ID));
        inv.setItem(13, craftEntry(DojoItems.createKatana(), DojoItems.KATANA_ID));
        inv.setItem(16, craftEntry(DojoItems.createYari(), DojoItems.YARI_ID));

        return inv;
    }

    private static ItemStack craftEntry(ItemStack displayItem, String itemId) {
        Recipe recipe = RECIPES.get(itemId);
        ItemStack item = displayItem.clone();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>(meta.hasLore() ? meta.getLore() : new ArrayList<>());
        lore.add("");
        lore.add("§7Cost:");
        lore.add("§f " + recipe.fang() + "x Kurayami Fang");
        if (recipe.bone() > 0) {
            lore.add("§e " + recipe.bone() + "x Kurayami Bone");
        }
        if (recipe.core() > 0) {
            lore.add("§d " + recipe.core() + "x Kurayami Core");
        }
        lore.add("§eClick to craft");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static Recipe recipeFor(String itemId) {
        return RECIPES.get(itemId);
    }

    public static ItemStack resultFor(String itemId) {
        return switch (itemId) {
            case DojoItems.KATANA_ID -> DojoItems.createKatana();
            case DojoItems.YARI_ID -> DojoItems.createYari();
            case DojoItems.NUNCHAKU_ID -> DojoItems.createNunchaku();
            default -> null;
        };
    }

    public static int countMaterial(Player player, String materialId) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && materialId.equals(DojoItems.getItemId(item))) {
                count += item.getAmount();
            }
        }
        return count;
    }

    public static void removeMaterial(Player player, String materialId, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack item : contents) {
            if (item != null && materialId.equals(DojoItems.getItemId(item))) {
                int toRemove = Math.min(remaining, item.getAmount());
                item.setAmount(item.getAmount() - toRemove);
                remaining -= toRemove;
                if (remaining <= 0) break;
            }
        }
    }
}
