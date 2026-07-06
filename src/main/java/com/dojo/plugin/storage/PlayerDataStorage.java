package com.dojo.plugin.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataStorage {

    private final JavaPlugin plugin;
    private final File dataFolder;
    private final Map<UUID, YamlConfiguration> cache = new HashMap<>();

    public PlayerDataStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    private File fileFor(UUID uuid) {
        return new File(dataFolder, uuid.toString() + ".yml");
    }

    public YamlConfiguration get(UUID uuid) {
        return cache.computeIfAbsent(uuid, id -> {
            File file = fileFor(id);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            return config;
        });
    }

    public void save(UUID uuid) {
        YamlConfiguration config = cache.get(uuid);
        if (config == null) return;
        try {
            config.save(fileFor(uuid));
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save player data for " + uuid + ": " + e.getMessage());
        }
    }

    public void saveAll() {
        for (UUID uuid : cache.keySet()) {
            save(uuid);
        }
    }
}
