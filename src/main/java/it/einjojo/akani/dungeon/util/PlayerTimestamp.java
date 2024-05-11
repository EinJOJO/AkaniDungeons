package it.einjojo.akani.dungeon.util;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a timestamp of an action that a player has done
 * e.g. opening a chest
 */
public record PlayerTimestamp(UUID player, long timestamp) implements Comparable<PlayerTimestamp> {


    @Override
    public int compareTo(@NotNull PlayerTimestamp o) {
        return Long.compare(timestamp, o.timestamp);
    }
}
