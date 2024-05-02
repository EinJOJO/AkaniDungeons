package it.einjojo.akani.dungeon.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonConfig {
    private final JavaPlugin plugin;
    private MobSpawnerConfig mobSpawnerConfig;
    private MobBiomesConfig mobBiomesConfig;

    public DungeonConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.reloadConfig();

        plugin.getConfig().options().copyDefaults(true);
        save();
    }

    private ConfigurationSection getOrCreateSection(String sectionString) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(sectionString);
        if (section == null) {
            return plugin.getConfig().createSection(sectionString);
        }
        return section;
    }

    public void save() {
        plugin.saveConfig();
    }

    public JavaPlugin plugin() {
        return plugin;
    }

    public MobSpawnerConfig mobSpawnerConfig() {
        return mobSpawnerConfig;
    }

    public MobBiomesConfig mobBiomesConfig() {
        return mobBiomesConfig;
    }
}
