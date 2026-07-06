package com.dojo.plugin.commands;

import com.dojo.plugin.DojoPlugin;
import com.dojo.plugin.arena.Arena;
import com.dojo.plugin.arena.ArenaManager;
import com.dojo.plugin.duel.DuelManager;
import com.dojo.plugin.items.DojoItems;
import com.dojo.plugin.listeners.NpcCrafterListener;
import com.dojo.plugin.mobs.Kurayami;
import com.dojo.plugin.rank.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

public class DojoCommand implements CommandExecutor {

    private final DojoPlugin plugin;
    private final DuelManager duelManager;
    private final ArenaManager arenaManager;
    private final RankManager rankManager;

    public DojoCommand(DojoPlugin plugin, DuelManager duelManager, ArenaManager arenaManager, RankManager rankManager) {
        this.plugin = plugin;
        this.duelManager = duelManager;
        this.arenaManager = arenaManager;
        this.rankManager = rankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6§lDOJO §7» §7Usage: /dojo <duel|accept|deny|rank|arena|item>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "duel":
                return handleDuel(sender, args);
            case "accept":
                return handleAccept(sender);
            case "deny":
                return handleDeny(sender);
            case "rank":
                return handleRank(sender, args);
            case "arena":
                return handleArena(sender, args);
            case "craftsman":
                return handleCraftsman(sender);
            case "guardian":
                return handleGuardianSpawn(sender, args);
            case "item":
                return handleGiveItem(sender, args);
            default:
                sender.sendMessage("§6§lDOJO §7» §cUnknown subcommand.");
                return true;
        }
    }

    private boolean handleDuel(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage("§6§lDOJO §7» §7Usage: /dojo duel <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("§6§lDOJO §7» §cPlayer not found.");
            return true;
        }
        if (target.equals(player)) {
            player.sendMessage("§6§lDOJO §7» §cYou cannot duel yourself.");
            return true;
        }
        if (duelManager.isInDuel(player.getUniqueId()) || duelManager.isInDuel(target.getUniqueId())) {
            player.sendMessage("§6§lDOJO §7» §cOne of you is already in a duel.");
            return true;
        }
        duelManager.sendRequest(player, target);
        return true;
    }

    private boolean handleAccept(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }
        duelManager.acceptRequest(player);
        return true;
    }

    private boolean handleDeny(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }
        duelManager.denyRequest(player);
        return true;
    }

    private boolean handleRank(CommandSender sender, String[] args) {
        Player target;
        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§6§lDOJO §7» §cPlayer not found.");
                return true;
            }
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            sender.sendMessage("§cConsole must specify a player: /dojo rank <player>");
            return true;
        }

        RankManager.Belt belt = rankManager.getBelt(target.getUniqueId());
        int points = rankManager.getPoints(target.getUniqueId());
        int wins = rankManager.getWins(target.getUniqueId());
        int losses = rankManager.getLosses(target.getUniqueId());

        sender.sendMessage("§6§l=== " + target.getName() + "'s Dojo Rank ===");
        sender.sendMessage("§7Belt: §e" + belt.displayName);
        sender.sendMessage("§7Points: §a" + points);
        sender.sendMessage("§7Wins: §a" + wins + " §7| Losses: §c" + losses);
        return true;
    }

    private boolean handleArena(CommandSender sender, String[] args) {
        if (!sender.hasPermission("dojo.admin")) {
            sender.sendMessage("§6§lDOJO §7» §cYou do not have permission to manage arenas.");
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage("§6§lDOJO §7» §7Usage: /dojo arena <create|setspawn1|setspawn2> <name>");
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "create": {
                if (args.length < 3) {
                    player.sendMessage("§6§lDOJO §7» §7Usage: /dojo arena create <name>");
                    return true;
                }
                arenaManager.createArena(args[2]);
                player.sendMessage("§6§lDOJO §7» §aArena '" + args[2] + "' created. Use setspawn1/setspawn2 next.");
                return true;
            }
            case "setspawn1": {
                if (args.length < 3) {
                    player.sendMessage("§6§lDOJO §7» §7Usage: /dojo arena setspawn1 <name>");
                    return true;
                }
                Arena arena = arenaManager.getArena(args[2]);
                if (arena == null) {
                    player.sendMessage("§6§lDOJO §7» §cArena not found.");
                    return true;
                }
                arena.setSpawn1(player.getLocation());
                player.sendMessage("§6§lDOJO §7» §aSpawn 1 set for arena '" + args[2] + "'.");
                return true;
            }
            case "setspawn2": {
                if (args.length < 3) {
                    player.sendMessage("§6§lDOJO §7» §7Usage: /dojo arena setspawn2 <name>");
                    return true;
                }
                Arena arena = arenaManager.getArena(args[2]);
                if (arena == null) {
                    player.sendMessage("§6§lDOJO §7» §cArena not found.");
                    return true;
                }
                arena.setSpawn2(player.getLocation());
                player.sendMessage("§6§lDOJO §7» §aSpawn 2 set for arena '" + args[2] + "'.");
                return true;
            }
            default:
                player.sendMessage("§6§lDOJO §7» §cUnknown arena subcommand.");
                return true;
        }
    }

    private boolean handleCraftsman(CommandSender sender) {
        if (!sender.hasPermission("dojo.admin")) {
            sender.sendMessage("§6§lDOJO §7» §cYou do not have permission to spawn NPCs.");
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }
        Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), org.bukkit.entity.EntityType.VILLAGER);
        villager.setCustomName("§6§lDojo Craftsman");
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setProfession(Villager.Profession.WEAPONSMITH);
        villager.getPersistentDataContainer().set(
                new org.bukkit.NamespacedKey(plugin, NpcCrafterListener.MARKER),
                PersistentDataType.STRING,
                "true"
        );
        player.sendMessage("§6§lDOJO §7» §aDojo Craftsman spawned. Right-click to open crafting menu.");
        return true;
    }

    private boolean handleGuardianSpawn(CommandSender sender, String[] args) {
        if (!sender.hasPermission("dojo.admin")) {
            sender.sendMessage("§6§lDOJO §7» §cYou do not have permission to spawn a Kurayami.");
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }
        int tier = 1;
        if (args.length >= 2) {
            try {
                tier = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("§6§lDOJO §7» §cUsage: /dojo guardian <tier 1-10>");
                return true;
            }
        }
        Kurayami.spawn(player.getLocation(), tier);
        player.sendMessage("§6§lDOJO §7» §aSpawned a tier " + tier + " Kurayami.");
        return true;
    }

    private boolean handleGiveItem(CommandSender sender, String[] args) {
        if (!sender.hasPermission("dojo.admin")) {
            sender.sendMessage("§6§lDOJO §7» §cYou do not have permission to open Dojo item menu.");
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }
        player.openInventory(com.dojo.plugin.gui.AdminItemGUI.build());
        return true;
    }
}
