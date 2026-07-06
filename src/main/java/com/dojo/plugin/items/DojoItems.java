package com.dojo.plugin.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DojoItems {

    public static final String KATANA_ID = "dojo_katana";
    public static final String YARI_ID = "dojo_yari";
    public static final String NUNCHAKU_ID = "dojo_nunchaku";

    public static final String FANG_ID = "kurayami_fang";
    public static final String BONE_ID = "kurayami_bone";
    public static final String CORE_ID = "kurayami_core";

    private static NamespacedKey idKey;

    public static void init(org.bukkit.plugin.Plugin plugin) {
        idKey = new NamespacedKey(plugin, "dojo_item_id");
    }

    public static NamespacedKey getIdKey() {
        return idKey;
    }

    public static ItemStack createKatana() {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Dojo Katana");
        List<String> lore = new ArrayList<>();
        lore.add("§7A finely honed blade");
        lore.add("§7forged in the dojo.");
        lore.add("");
        lore.add("§eFast, balanced strikes");
        lore.add("§7Damage: §c7.0  §7Speed: §b1.8");
        lore.add("");
        lore.add("§d§lSkill: §dIaido Slash");
        lore.add("§7Dash forward and unleash a");
        lore.add("§7piercing line slash.");
        lore.add("§7Damage: §c8.0  §7Stun: §b0.5s  §7Cooldown: §a8s");
        lore.add("§8Right-click to activate");
        meta.setLore(lore);
        meta.setCustomModelData(101);
        addAttribute(meta, Attribute.GENERIC_ATTACK_DAMAGE, 7.0);
        addAttribute(meta, Attribute.GENERIC_ATTACK_SPEED, 1.8);
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, KATANA_ID);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createYari() {
        ItemStack item = new ItemStack(Material.TRIDENT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Dojo Yari");
        List<String> lore = new ArrayList<>();
        lore.add("§7A long spear favored");
        lore.add("§7for keeping enemies at bay.");
        lore.add("");
        lore.add("§eLong reach, high damage");
        lore.add("§7Damage: §c9.0  §7Speed: §b1.0");
        lore.add("");
        lore.add("§d§lSkill: §dPiercing Thrust");
        lore.add("§7Thrust forward, impaling");
        lore.add("§7and hurling enemies back.");
        lore.add("§7Damage: §c10.0  §7Stun: §b0.3s  §7Cooldown: §a12s");
        lore.add("§8Right-click to activate");
        meta.setLore(lore);
        meta.setCustomModelData(102);
        addAttribute(meta, Attribute.GENERIC_ATTACK_DAMAGE, 9.0);
        addAttribute(meta, Attribute.GENERIC_ATTACK_SPEED, 1.0);
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, YARI_ID);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createNunchaku() {
        ItemStack item = new ItemStack(Material.GOLDEN_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Dojo Nunchaku");
        List<String> lore = new ArrayList<>();
        lore.add("§7Rapid strikes,");
        lore.add("§7hard to master.");
        lore.add("");
        lore.add("§eVery fast, low damage per hit");
        lore.add("§7Damage: §c3.0  §7Speed: §b3.2");
        lore.add("");
        lore.add("§d§lSkill: §dWhirlwind Strike");
        lore.add("§7Spin rapidly, striking all");
        lore.add("§7nearby enemies at once.");
        lore.add("§7Damage: §c5.0  §7Stun: §b1.25s  §7Cooldown: §a10s");
        lore.add("§8Right-click to activate");
        meta.setLore(lore);
        meta.setCustomModelData(103);
        addAttribute(meta, Attribute.GENERIC_ATTACK_DAMAGE, 3.0);
        addAttribute(meta, Attribute.GENERIC_ATTACK_SPEED, 3.2);
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, NUNCHAKU_ID);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFang() {
        ItemStack item = new ItemStack(Material.PRISMARINE_CRYSTALS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§fKurayami Fang");
        List<String> lore = new ArrayList<>();
        lore.add("§7A jagged fang torn from");
        lore.add("§7a weakened Kurayami.");
        lore.add("§8Dropped by: Wakate, Kenshi");
        meta.setLore(lore);
        meta.setCustomModelData(201);
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, FANG_ID);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createBone() {
        ItemStack item = new ItemStack(Material.PRISMARINE_SHARD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eKurayami Bone");
        List<String> lore = new ArrayList<>();
        lore.add("§7A dense bone fragment,");
        lore.add("§7hardened by dark training.");
        lore.add("§8Dropped by: Bushi, Ronin");
        meta.setLore(lore);
        meta.setCustomModelData(202);
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, BONE_ID);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createCore() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§5§lKurayami Core");
        List<String> lore = new ArrayList<>();
        lore.add("§7A pulsing core of pure darkness.");
        lore.add("§7Only the strongest Kurayami");
        lore.add("§7carry one within them.");
        lore.add("§8Dropped by: Kurayami, Kurayami no Oni");
        meta.setLore(lore);
        meta.setCustomModelData(210);
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, CORE_ID);
        item.setItemMeta(meta);
        return item;
    }

    public static String getItemId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (idKey == null) return null;
        return meta.getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
    }

    private static void addAttribute(ItemMeta meta, Attribute attribute, double amount) {
        AttributeModifier modifier = new AttributeModifier(
                UUID.randomUUID(),
                "dojo_weapon_modifier",
                amount,
                AttributeModifier.Operation.ADD_NUMBER,
                org.bukkit.inventory.EquipmentSlot.HAND
        );
        meta.addAttributeModifier(attribute, modifier);
    }
}
