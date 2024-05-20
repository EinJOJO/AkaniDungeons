package it.einjojo.akani.dungeon.mobs;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import it.einjojo.akani.dungeon.mobs.spawnable.MythicMobSpawnable;
import it.einjojo.akani.dungeon.mobs.spawnable.Spawnable;
import it.einjojo.akani.dungeon.mobs.spawnable.VanillaMobSpawnable;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.Optional;

public class SpawnableFactory {

    public Spawnable<?> create(String mobId, Location spawnLocation) {
        try {
            EntityType entityType = EntityType.valueOf(mobId);
            return createVanillaMob(entityType, spawnLocation);
        } catch (IllegalArgumentException ignore) {
        }
        Optional<MythicMob> mob = MythicBukkit.inst().getMobManager().getMythicMob(mobId);
        if (mob.isPresent()) {
            return createMythicMob(mob.get(), spawnLocation);
        }
        throw new IllegalArgumentException("Unknown mob id: " + mobId);
    }

    public VanillaMobSpawnable createVanillaMob(EntityType entityType, Location spawnLocation) {
        return new VanillaMobSpawnable(entityType, spawnLocation);
    }

    public MythicMobSpawnable createMythicMob(MythicMob mythicMob, Location spawnLocation) {
        return new MythicMobSpawnable(mythicMob, spawnLocation);
    }
}
