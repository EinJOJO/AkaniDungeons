package it.einjojo.akani.dungeon.config;

import org.bukkit.configuration.ConfigurationSection;

public class MobSpawnerConfig {
    private final int selectorInterval;
    private final int spawnerInterval;

    public MobSpawnerConfig(ConfigurationSection section) {
        section.addDefault("selectorInterval", 200);
        section.addDefault("spawnerInterval", 2);
        selectorInterval = section.getInt("selectorInterval");
        spawnerInterval = section.getInt("spawnerInterval");
    }

    public int selectorInterval() {
        return selectorInterval;
    }

    public int spawnerInterval() {
        return spawnerInterval;
    }
}
