package it.einjojo.akani.dungeon.mine.factory;

import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import it.einjojo.akani.dungeon.mine.MineOre;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.HashSet;

public record MineOreFactory() {



    public MineOre createMineOre(Location location, MineOreType oreType) {
        int entityId = SpigotReflectionUtil.generateEntityId();

        return new MineOre(entityId, location, oreType, new HashSet<>(), new HashMap<>());
    }

}
