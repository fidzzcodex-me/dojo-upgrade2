package com.dojo.plugin.rank;

import com.dojo.plugin.storage.PlayerDataStorage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class RankManager {

    public enum Belt {
        WHITE(0, "White Belt"),
        YELLOW(50, "Yellow Belt"),
        GREEN(150, "Green Belt"),
        BLUE(300, "Blue Belt"),
        BROWN(500, "Brown Belt"),
        BLACK(800, "Black Belt");

        public final int requiredPoints;
        public final String displayName;

        Belt(int requiredPoints, String displayName) {
            this.requiredPoints = requiredPoints;
            this.displayName = displayName;
        }

        public static Belt fromPoints(int points) {
            Belt result = WHITE;
            for (Belt belt : values()) {
                if (points >= belt.requiredPoints) {
                    result = belt;
                }
            }
            return result;
        }

        public Belt next() {
            Belt[] all = values();
            int idx = this.ordinal();
            if (idx + 1 < all.length) {
                return all[idx + 1];
            }
            return this;
        }
    }

    private final JavaPlugin plugin;
    private final PlayerDataStorage storage;

    public RankManager(JavaPlugin plugin, PlayerDataStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public int getPoints(UUID uuid) {
        YamlConfiguration config = storage.get(uuid);
        return config.getInt("points", 0);
    }

    public Belt getBelt(UUID uuid) {
        return Belt.fromPoints(getPoints(uuid));
    }

    public void addPoints(UUID uuid, int amount) {
        YamlConfiguration config = storage.get(uuid);
        int current = config.getInt("points", 0);
        Belt before = Belt.fromPoints(current);
        int updated = Math.max(0, current + amount);
        config.set("points", updated);
        storage.save(uuid);

        Belt after = Belt.fromPoints(updated);
        if (after != before && amount > 0) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.sendMessage("§6§lDOJO §7» §aYou have been promoted to §e" + after.displayName + "§a!");
            }
        }
    }

    public void recordWin(UUID uuid) {
        YamlConfiguration config = storage.get(uuid);
        config.set("wins", config.getInt("wins", 0) + 1);
        addPoints(uuid, 25);
    }

    public void recordLoss(UUID uuid) {
        YamlConfiguration config = storage.get(uuid);
        config.set("losses", config.getInt("losses", 0) + 1);
        addPoints(uuid, -10);
    }

    public int getWins(UUID uuid) {
        return storage.get(uuid).getInt("wins", 0);
    }

    public int getLosses(UUID uuid) {
        return storage.get(uuid).getInt("losses", 0);
    }
}
