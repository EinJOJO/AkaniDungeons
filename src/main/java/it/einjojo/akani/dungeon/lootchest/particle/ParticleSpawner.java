package it.einjojo.akani.dungeon.lootchest.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ParticleSpawner {

    void spawnParticle(Player receiver, Location location);

}
