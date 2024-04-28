package it.einjojo.akani.dungeon.mobs.spawnable;

import org.bukkit.entity.Entity;

public record MythicMob(String mobId, boolean forceSpawnOnLag) implements Spawnable<Entity> {

    public Entity spawn() {
        return null;
    }

    @Override
    public void postSpawn(Entity entity) {

    }
}
