package it.einjojo.akani.dungeon.mine.factory;

import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import it.einjojo.akani.dungeon.mine.PlacedOre;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.HashSet;

public record PlacedOreFactory() {

    public PlacedOre createMineOre(Location location, MineOreType oreType) {
        int entityId = SpigotReflectionUtil.generateEntityId();
        return new PlacedOre(entityId, location, oreType, new HashSet<>(), new HashMap<>());
    }

}
