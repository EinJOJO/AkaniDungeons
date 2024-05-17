package it.einjojo.akani.dungeon.lootchest.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LegendaryParticleSpawner implements ParticleSpawner {
    public static final String NAME = "legendary";

    @Override
    public void spawnParticle(Player receiver, Location location) {

    }

    @Override
    public String name() {
        return NAME;
    }
}
