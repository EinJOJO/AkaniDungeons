package it.einjojo.akani.dungeon.mobs;

public interface Spawnable<ENTITY> {
    void spawn();

    boolean forceSpawnOnLag();

}
