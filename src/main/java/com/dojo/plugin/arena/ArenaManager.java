package com.dojo.plugin.arena;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager {

    private final JavaPlugin plugin;
    private final File file;
    private final Map<String, Arena> arenas = new HashMap<>();

    public ArenaManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "arenas.yml");
    }

    public Arena createArena(String name) {
        Arena arena = new Arena(name);
        arenas.put(name.toLowerCase(), arena);
        return arena;
    }

    public Arena getArena(String name) {
        return arenas.get(name.toLowerCase());
    }

    public Map<String, Arena> getArenas() {
        return arenas;
    }

    public Arena findFreeArena() {
        for (Arena arena : arenas.values()) {
            if (arena.isReady() && !arena.isInUse()) {
                return arena;
            }
        }
        return null;
    }

    public void loadArenas() {
        if (!file.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.getConfigurationSection("arenas") == null) {
            return;
        }
        for (String name : config.getConfigurationSection("arenas").getKeys(false)) {
            Arena arena = createArena(name);
            String path = "arenas." + name;
            if (config.contains(path + ".spawn1")) {
                arena.setSpawn1(deserializeLocation(config, path + ".spawn1"));
            }
            if (config.contains(path + ".spawn2")) {
                arena.setSpawn2(deserializeLocation(config, path + ".spawn2"));
            }
        }
        plugin.getLogger().info("Loaded " + arenas.size() + " dojo arena(s).");
    }

    public void saveArenas() {
        YamlConfiguration config = new YamlConfiguration();
        for (Arena arena : arenas.values()) {
            String path = "arenas." + arena.getName();
            if (arena.getSpawn1() != null) {
                serializeLocation(config, path + ".spawn1", arena.getSpawn1());
            }
            if (arena.getSpawn2() != null) {
                serializeLocation(config, path + ".spawn2", arena.getSpawn2());
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save arenas.yml: " + e.getMessage());
        }
    }

    private void serializeLocation(YamlConfiguration config, String path, Location loc) {
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
    }

    private Location deserializeLocation(YamlConfiguration config, String path) {
        World world = plugin.getServer().getWorld(config.getString(path + ".world"));
        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }
}
