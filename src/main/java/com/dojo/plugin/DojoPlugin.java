package com.dojo.plugin;

import com.dojo.plugin.arena.ArenaManager;
import com.dojo.plugin.commands.DojoCommand;
import com.dojo.plugin.duel.DuelManager;
import com.dojo.plugin.items.DojoItems;
import com.dojo.plugin.listeners.AdminItemGUIListener;
import com.dojo.plugin.listeners.CraftListener;
import com.dojo.plugin.listeners.GuardianListener;
import com.dojo.plugin.listeners.NpcCrafterListener;
import com.dojo.plugin.listeners.PlayerListener;
import com.dojo.plugin.listeners.WeaponSkillListener;
import com.dojo.plugin.mobs.Kurayami;
import com.dojo.plugin.mobs.GuardianSpawner;
import com.dojo.plugin.rank.RankManager;
import com.dojo.plugin.skills.SkillManager;
import com.dojo.plugin.storage.PlayerDataStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class DojoPlugin extends JavaPlugin {

    private static DojoPlugin instance;

    private PlayerDataStorage storage;
    private ArenaManager arenaManager;
    private RankManager rankManager;
    private DuelManager duelManager;
    private GuardianSpawner guardianSpawner;
    private SkillManager skillManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        DojoItems.init(this);
        Kurayami.init(this);

        this.storage = new PlayerDataStorage(this);
        this.arenaManager = new ArenaManager(this);
        this.rankManager = new RankManager(this, storage);
        this.duelManager = new DuelManager(this, arenaManager, rankManager);
        this.guardianSpawner = new GuardianSpawner(this);
        this.skillManager = new SkillManager(this);

        arenaManager.loadArenas();

        getCommand("dojo").setExecutor(new DojoCommand(this, duelManager, arenaManager, rankManager));

        getServer().getPluginManager().registerEvents(new PlayerListener(this, duelManager), this);
        getServer().getPluginManager().registerEvents(new GuardianListener(guardianSpawner), this);
        getServer().getPluginManager().registerEvents(new CraftListener(), this);
        getServer().getPluginManager().registerEvents(new AdminItemGUIListener(), this);
        getServer().getPluginManager().registerEvents(new NpcCrafterListener(this), this);
        getServer().getPluginManager().registerEvents(new WeaponSkillListener(skillManager), this);

        var world = getServer().getWorlds().get(0);
        guardianSpawner.configure(world, world.getSpawnLocation().getX(), world.getSpawnLocation().getZ(),
                150.0, 120, 300, 5);
        guardianSpawner.start();

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (var w : getServer().getWorlds()) {
                for (var entity : w.getLivingEntities()) {
                    if (Kurayami.isGuardian(entity)) {
                        com.dojo.plugin.mobs.GuardianAttack.tryAttack(this, entity);
                    }
                }
            }
        }, 40L, 20L);

        getLogger().info("Dojo plugin enabled.");
    }

    @Override
    public void onDisable() {
        if (arenaManager != null) {
            arenaManager.saveArenas();
        }
        if (storage != null) {
            storage.saveAll();
        }
        getLogger().info("Dojo plugin disabled.");
    }

    public static DojoPlugin getInstance() {
        return instance;
    }

    public PlayerDataStorage getStorage() {
        return storage;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public DuelManager getDuelManager() {
        return duelManager;
    }

    public GuardianSpawner getGuardianSpawner() {
        return guardianSpawner;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }
}
