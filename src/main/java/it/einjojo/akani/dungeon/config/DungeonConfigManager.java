package it.einjojo.akani.dungeon.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.einjojo.akani.dungeon.config.json.JsonMineOreTypeConfig;
import it.einjojo.akani.dungeon.config.json.adapter.BreakRewardAdapter;
import it.einjojo.akani.dungeon.config.json.adapter.MineOreTypeAdapter;
import it.einjojo.akani.dungeon.mine.BreakReward;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonConfigManager {
    private final JavaPlugin plugin;
    private final Gson gson;
    private final MobSpawnerConfig mobSpawnerConfig;
    private final MobBiomesConfig mobBiomesConfig;
    private final MineOreTypeConfig mineOreTypeConfig;
    private final ToolConfig toolConfig;


    public DungeonConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        gson = new GsonBuilder()
                .registerTypeAdapter(MineOreType.class, new MineOreTypeAdapter())
                .registerTypeAdapter(BreakReward.class, new BreakRewardAdapter())
                .setPrettyPrinting()
                .create();
        mineOreTypeConfig = new JsonMineOreTypeConfig(gson, plugin.getDataFolder().toPath().resolve("oreTypes.json"));
        toolConfig = new ToolConfig.Dummy();
        mobBiomesConfig = new MobBiomesConfig.Dummy();
        mobSpawnerConfig = new MobSpawnerConfig.Dummy();
    }

    public void load() {
        mineOreTypeConfig.load();
    }


    public void save() {
        mineOreTypeConfig.save();
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

    public MineOreTypeConfig mineOreTypeConfig() {
        return mineOreTypeConfig;
    }

    public ToolConfig toolConfig() {
        return toolConfig;
    }

    public Gson gson() {
        return gson;
    }
}
