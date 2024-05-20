package it.einjojo.akani.dungeon.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

public class ParticleUtil {

    public static void spawnCircle(Player player, Location location, float radius, Color color) {
        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            Particle.REDSTONE.builder()
                    .location(location.clone().add(radius * Math.cos(angle), 0, radius * Math.sin(angle)))
                    .color(color)
                    .receivers(List.of(player))
                    .spawn();
        }
    }

}
