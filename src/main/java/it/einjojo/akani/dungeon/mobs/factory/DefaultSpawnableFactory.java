package it.einjojo.akani.dungeon.mobs.factory;

import it.einjojo.akani.dungeon.mobs.spawnable.Spawnable;
import it.einjojo.akani.dungeon.mobs.spawnable.VanillaMob;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class DefaultSpawnableFactory implements SpawnableFactory {
    @Override
    public Spawnable<?> create(String mobId, Location spawnLocation) {
        try {
            EntityType entityType = EntityType.valueOf(mobId);
            return new VanillaMob(entityType, spawnLocation);
        } catch (IllegalArgumentException ignore) {
        }
        throw new IllegalArgumentException("Unknown mob id: " + mobId);
    }
}
