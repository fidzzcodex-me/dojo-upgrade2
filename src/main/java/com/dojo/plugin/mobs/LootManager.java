package com.dojo.plugin.mobs;

import com.dojo.plugin.items.DojoItems;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class LootManager {

    private static final Random random = new Random();

    public static void dropLoot(Location location, int tier) {
        World world = location.getWorld();
        if (world == null) return;

        if (tier <= 4) {
            dropFang(world, location, tier);
        } else if (tier <= 8) {
            dropFang(world, location, 2);
            dropBone(world, location, tier);
        } else {
            dropBone(world, location, 3);
            dropCore(world, location, tier);
        }
    }

    private static void dropFang(World world, Location location, int tier) {
        int amount = 1 + random.nextInt(tier + 1);
        ItemStack fang = DojoItems.createFang();
        fang.setAmount(Math.min(amount, fang.getMaxStackSize()));
        world.dropItemNaturally(location, fang);
    }

    private static void dropBone(World world, Location location, int tier) {
        int amount = 1 + random.nextInt(Math.max(1, tier - 3));
        ItemStack bone = DojoItems.createBone();
        bone.setAmount(Math.min(amount, bone.getMaxStackSize()));
        world.dropItemNaturally(location, bone);
    }

    private static void dropCore(World world, Location location, int tier) {
        double chance = tier == 10 ? 0.6 : 0.25;
        if (random.nextDouble() < chance) {
            world.dropItemNaturally(location, DojoItems.createCore());
        }
    }
}
