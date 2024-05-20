package it.einjojo.akani.dungeon.mobs.spawnable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public record VanillaMobSpawnable(EntityType type, Location location) implements Spawnable<Entity> {

    @Override
    public Entity spawn() {
        Entity entity = location.getWorld().spawnEntity(location, type);
        entity.setCustomNameVisible(true);
        entity.customName(Component.text(entity.getType().name()).color(NamedTextColor.RED));
        entity.setPersistent(false);
        return entity;
    }

    @Override
    public boolean forceSpawnOnLag() {
        return false;
    }


}
