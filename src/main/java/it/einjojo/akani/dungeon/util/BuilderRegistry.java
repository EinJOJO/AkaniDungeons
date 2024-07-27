package it.einjojo.akani.dungeon.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BuilderRegistry {

    private static final Set<UUID> buildModeSet = new HashSet<>();

    public static void toggleBuild(UUID playerId) {
        if (buildModeSet.contains(playerId)) {
            buildModeSet.remove(playerId);
        } else {
            buildModeSet.add(playerId);
        }
    }

    public static boolean isNotInBuildMode(@NotNull UUID player) {
        return !buildModeSet.contains(player.getUniqueId());
    }

}
