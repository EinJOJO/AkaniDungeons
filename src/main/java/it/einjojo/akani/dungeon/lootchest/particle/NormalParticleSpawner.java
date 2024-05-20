package it.einjojo.akani.dungeon.lootchest.particle;

import it.einjojo.akani.dungeon.util.ParticleUtil;
import org.bukkit.Color;
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
        ParticleUtil.spawnCircle(receiver, location, 1f, Color.WHITE);
    }
}
