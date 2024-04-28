package it.einjojo.akani.dungeon.mobs.factory;

import it.einjojo.akani.dungeon.mobs.spawnable.Spawnable;
import org.bukkit.Location;

public interface SpawnableFactory {

    Spawnable<?> create(String mobId, Location spawnLocation);

}
