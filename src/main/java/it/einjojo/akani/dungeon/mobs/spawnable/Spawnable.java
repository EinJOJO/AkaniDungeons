package it.einjojo.akani.dungeon.mobs.spawnable;

public interface Spawnable<ENTITY> {
    ENTITY spawn();

    boolean forceSpawnOnLag();


}
