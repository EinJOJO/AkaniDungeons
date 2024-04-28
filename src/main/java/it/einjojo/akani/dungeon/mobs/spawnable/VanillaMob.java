package it.einjojo.akani.dungeon.mobs.spawnable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public record VanillaMob(EntityType type, Location location) implements Spawnable<Entity> {

    @Override
    public Entity spawn() {
        return location.getWorld().spawnEntity(location, type);
    }

    @Override
    public boolean forceSpawnOnLag() {
        return false;
    }

    @Override
    public void postSpawn(Entity entity) {
        entity.setCustomNameVisible(true);
        entity.customName(Component.text(entity.getType().name()).color(NamedTextColor.RED));
        entity.setPersistent(false);
    }
}
