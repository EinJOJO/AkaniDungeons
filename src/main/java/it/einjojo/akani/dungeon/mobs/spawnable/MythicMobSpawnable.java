package it.einjojo.akani.dungeon.mobs.spawnable;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;

public record MythicMobSpawnable(MythicMob mythicMob, Location location) implements Spawnable<ActiveMob> {

    public ActiveMob spawn() {
        return mythicMob.spawn(BukkitAdapter.adapt(location), 1);
    }

    @Override
    public boolean forceSpawnOnLag() {
        return false;
    }
}
