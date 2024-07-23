package it.einjojo.akani.dungeon.lootchest.particle;

import it.einjojo.akani.dungeon.util.ParticleUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Blue particle spawner
 * @since 1.2.8
 *
 */
public class VeryRareParticleSpawner implements ParticleSpawner{
    public static final String NAME = "very_rare";
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void spawnParticle(Player receiver, Location location) {
        ParticleUtil.spawnCircle(receiver, location, 2, Color.BLUE);
    }
}
