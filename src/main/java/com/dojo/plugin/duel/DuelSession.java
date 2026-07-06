package com.dojo.plugin.duel;

import com.dojo.plugin.arena.Arena;

import java.util.UUID;

public class DuelSession {

    private final UUID player1;
    private final UUID player2;
    private final Arena arena;
    private boolean frozen;

    public DuelSession(UUID player1, UUID player2, Arena arena) {
        this.player1 = player1;
        this.player2 = player2;
        this.arena = arena;
        this.frozen = false;
    }

    public UUID getPlayer1() {
        return player1;
    }

    public UUID getPlayer2() {
        return player2;
    }

    public Arena getArena() {
        return arena;
    }

    public UUID getOpponent(UUID uuid) {
        if (uuid.equals(player1)) return player2;
        if (uuid.equals(player2)) return player1;
        return null;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
}
