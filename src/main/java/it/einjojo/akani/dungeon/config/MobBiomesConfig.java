package it.einjojo.akani.dungeon.config;

import org.bukkit.block.Biome;

import java.util.List;
import java.util.Map;

@Deprecated
public interface MobBiomesConfig {

    Map<Biome, List<String>> biomeAssignments();

    List<String> mobIds(Biome biome);


    public static class Dummy implements MobBiomesConfig {

        @Override
        public Map<Biome, List<String>> biomeAssignments() {
            return Map.of();
        }

        @Override
        public List<String> mobIds(Biome biome) {
            return List.of();
        }
    }
}
