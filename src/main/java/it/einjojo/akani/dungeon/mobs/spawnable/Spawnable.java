package it.einjojo.akani.dungeon.mobs.spawnable;

public interface Spawnable<ENTITY> {
    ENTITY spawn();

    boolean forceSpawnOnLag();

    default void postSpawnObject(Object object) {
        postSpawn((ENTITY)(object));
    }

    void postSpawn(ENTITY entity);

}
