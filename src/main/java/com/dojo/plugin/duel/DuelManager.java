package com.dojo.plugin.duel;

import com.dojo.plugin.arena.Arena;
import com.dojo.plugin.arena.ArenaManager;
import com.dojo.plugin.rank.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelManager {

    private final JavaPlugin plugin;
    private final ArenaManager arenaManager;
    private final RankManager rankManager;

    private final Map<UUID, UUID> pendingRequests = new HashMap<>();
    private final Map<UUID, DuelSession> activeSessions = new HashMap<>();
    private final Map<UUID, Location> returnLocations = new HashMap<>();

    public DuelManager(JavaPlugin plugin, ArenaManager arenaManager, RankManager rankManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.rankManager = rankManager;
    }

    public boolean isInDuel(UUID uuid) {
        return activeSessions.containsKey(uuid);
    }

    public void sendRequest(Player from, Player to) {
        pendingRequests.put(to.getUniqueId(), from.getUniqueId());
        to.sendMessage("§6§lDOJO §7» §e" + from.getName() + " §7has challenged you to a duel! Type §a/dojo accept §7or §c/dojo deny");
        from.sendMessage("§6§lDOJO §7» §7Duel request sent to §e" + to.getName());
    }

    public boolean hasPendingRequest(UUID target) {
        return pendingRequests.containsKey(target);
    }

    public void acceptRequest(Player accepter) {
        UUID challengerId = pendingRequests.remove(accepter.getUniqueId());
        if (challengerId == null) {
            accepter.sendMessage("§6§lDOJO §7» §cYou have no pending duel requests.");
            return;
        }
        Player challenger = Bukkit.getPlayer(challengerId);
        if (challenger == null || !challenger.isOnline()) {
            accepter.sendMessage("§6§lDOJO §7» §cThat player is no longer online.");
            return;
        }

        Arena arena = arenaManager.findFreeArena();
        if (arena == null) {
            accepter.sendMessage("§6§lDOJO §7» §cNo dojo arena is available right now.");
            challenger.sendMessage("§6§lDOJO §7» §cNo dojo arena is available right now.");
            return;
        }

        startDuel(challenger, accepter, arena);
    }

    public void denyRequest(Player decliner) {
        UUID challengerId = pendingRequests.remove(decliner.getUniqueId());
        if (challengerId == null) {
            decliner.sendMessage("§6§lDOJO §7» §cYou have no pending duel requests.");
            return;
        }
        Player challenger = Bukkit.getPlayer(challengerId);
        decliner.sendMessage("§6§lDOJO §7» §7Duel request declined.");
        if (challenger != null && challenger.isOnline()) {
            challenger.sendMessage("§6§lDOJO §7» §c" + decliner.getName() + " declined your duel request.");
        }
    }

    private void startDuel(Player p1, Player p2, Arena arena) {
        arena.setInUse(true);
        returnLocations.put(p1.getUniqueId(), p1.getLocation());
        returnLocations.put(p2.getUniqueId(), p2.getLocation());

        p1.teleport(arena.getSpawn1());
        p2.teleport(arena.getSpawn2());

        DuelSession session = new DuelSession(p1.getUniqueId(), p2.getUniqueId(), arena);
        activeSessions.put(p1.getUniqueId(), session);
        activeSessions.put(p2.getUniqueId(), session);

        p1.setHealth(20.0);
        p2.setHealth(20.0);
        p1.getInventory().setHeldItemSlot(0);
        p2.getInventory().setHeldItemSlot(0);

        broadcastToDuel(session, "§6§lDOJO §7» §7Duel starting in the dojo arena!");
        runCountdown(session, 5);
    }

    private void runCountdown(DuelSession session, int seconds) {
        session.setFrozen(true);
        new BukkitRunnable() {
            int remaining = seconds;

            @Override
            public void run() {
                Player p1 = Bukkit.getPlayer(session.getPlayer1());
                Player p2 = Bukkit.getPlayer(session.getPlayer2());
                if (p1 == null || p2 == null) {
                    cancel();
                    return;
                }
                if (remaining > 0) {
                    p1.sendMessage("§6§lDOJO §7» §e" + remaining + "...");
                    p2.sendMessage("§6§lDOJO §7» §e" + remaining + "...");
                    remaining--;
                } else {
                    p1.sendMessage("§6§lDOJO §7» §a§lFIGHT!");
                    p2.sendMessage("§6§lDOJO §7» §a§lFIGHT!");
                    session.setFrozen(false);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void handleDeath(Player loser) {
        DuelSession session = activeSessions.get(loser.getUniqueId());
        if (session == null) return;

        UUID winnerId = session.getOpponent(loser.getUniqueId());
        Player winner = Bukkit.getPlayer(winnerId);

        endDuel(session, winner, loser);
    }

    private void endDuel(DuelSession session, Player winner, Player loser) {
        activeSessions.remove(session.getPlayer1());
        activeSessions.remove(session.getPlayer2());
        session.getArena().setInUse(false);

        if (winner != null) {
            rankManager.recordWin(winner.getUniqueId());
            winner.sendMessage("§6§lDOJO §7» §a§lYou won the duel!");
        }
        if (loser != null) {
            rankManager.recordLoss(loser.getUniqueId());
            loser.sendMessage("§6§lDOJO §7» §c§lYou lost the duel.");
        }

        returnPlayer(loser);
        returnPlayer(winner);
    }

    private void returnPlayer(Player player) {
        if (player == null) return;
        Location loc = returnLocations.remove(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (loc != null) {
                    player.teleport(loc);
                }
                player.setHealth(20.0);
                player.setFoodLevel(20);
            }
        }.runTaskLater(plugin, 20L);
    }

    public boolean isFrozen(UUID uuid) {
        DuelSession session = activeSessions.get(uuid);
        return session != null && session.isFrozen();
    }

    private void broadcastToDuel(DuelSession session, String message) {
        Player p1 = Bukkit.getPlayer(session.getPlayer1());
        Player p2 = Bukkit.getPlayer(session.getPlayer2());
        if (p1 != null) p1.sendMessage(message);
        if (p2 != null) p2.sendMessage(message);
    }
}
