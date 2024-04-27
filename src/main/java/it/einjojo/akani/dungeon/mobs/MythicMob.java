package it.einjojo.akani.dungeon.mobs;

public record MythicMob(String mobId, boolean forceSpawnOnLag) implements Spawnable<Void> {

    public void spawn() {

    }

}
