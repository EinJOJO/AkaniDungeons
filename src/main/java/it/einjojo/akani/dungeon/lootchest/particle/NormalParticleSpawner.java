package it.einjojo.akani.dungeon.lootchest.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NormalParticleSpawner implements ParticleSpawner {

    @Override
    public void spawnParticle(Player receiver, Location location) {
        receiver.spawnParticle(org.bukkit.Particle.VILLAGER_HAPPY, location, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }
}
