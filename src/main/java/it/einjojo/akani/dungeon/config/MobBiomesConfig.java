package it.einjojo.akani.dungeon.config;

import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobBiomesConfig {
    private final ConfigurationSection section;
    private final Map<Biome, List<String>> biomeAssignments = new HashMap<>();

    public MobBiomesConfig(ConfigurationSection section) {
        this.section = section;
        setDefaults();
        for (String biomeName : section.getKeys(false)) {
            biomeAssignments.put(Biome.valueOf(biomeName), section.getStringList(biomeName));
        }
    }

    public void setDefaults() {
        if (!section.getKeys(false).isEmpty()) {
            return;
        }
        for (Biome biome : Biome.values()) {
            section.addDefault(biome.name(), List.of());
        }
    }

    public Map<Biome, List<String>> biomeAssignments() {
        return biomeAssignments;
    }

    public List<String> mobIds(Biome biome) {
        return biomeAssignments.get(biome);
    }

}
