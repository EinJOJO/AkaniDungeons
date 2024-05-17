package it.einjojo.akani.dungeon.lootchest.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ParticleSpawner {

    String name();

    void spawnParticle(Player receiver, Location location);

}
