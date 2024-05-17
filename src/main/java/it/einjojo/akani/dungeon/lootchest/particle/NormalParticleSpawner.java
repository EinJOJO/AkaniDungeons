package it.einjojo.akani.dungeon.lootchest.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NormalParticleSpawner implements ParticleSpawner {
    public static final String NAME = "normal";
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void spawnParticle(Player receiver, Location location) {
        receiver.spawnParticle(org.bukkit.Particle.VILLAGER_HAPPY, location, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }
}
