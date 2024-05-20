package it.einjojo.akani.dungeon.lootchest.particle;

import it.einjojo.akani.dungeon.util.ParticleUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EpicParticleSpawner implements ParticleSpawner {
    public static final String NAME = "epic";
    private float radius = 0;
    private long lastIncrease = System.currentTimeMillis();

    @Override
    public void spawnParticle(Player receiver, Location location) {
        ParticleUtil.spawnCircle(receiver, location, radius, Color.PURPLE);
        increaseRadius();
    }

    private void increaseRadius() {
        if (System.currentTimeMillis() - lastIncrease < 100) {
            return;
        }
        radius = (radius + 0.2f) % 2;
        lastIncrease = System.currentTimeMillis();
    }

    @Override
    public String name() {
        return NAME;
    }
}
