package it.einjojo.akani.dungeon.lootchest.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EpicParticleSpawner implements ParticleSpawner {
    public static final String NAME = "epic";

    @Override
    public void spawnParticle(Player receiver, Location location) {

    }

    @Override
    public String name() {
        return NAME;
    }
}
