package it.einjojo.akani.dungeon.config;

import org.bukkit.block.Biome;

import java.util.List;
import java.util.Map;

public interface MobBiomesConfig {

    public Map<Biome, List<String>> biomeAssignments();

    public List<String> mobIds(Biome biome);


}
