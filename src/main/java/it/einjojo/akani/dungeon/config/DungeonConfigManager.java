package it.einjojo.akani.dungeon.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.einjojo.akani.dungeon.AkaniDungeonPlugin;
import it.einjojo.akani.dungeon.config.json.JsonMineOreTypeConfig;
import it.einjojo.akani.dungeon.config.json.JsonPlacedOreConfig;
import it.einjojo.akani.dungeon.config.json.adapter.ItemRewardAdapter;
import it.einjojo.akani.dungeon.config.json.adapter.MineOreTypeAdapter;
import it.einjojo.akani.dungeon.config.json.adapter.PlacedOreAdapter;
import it.einjojo.akani.dungeon.util.ItemReward;
import it.einjojo.akani.dungeon.mine.MineOreType;
import it.einjojo.akani.dungeon.mine.PlacedOre;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonConfigManager {
    private final JavaPlugin plugin;
    private final Gson gson;
    private final MobSpawnerConfig mobSpawnerConfig;
    private final MobBiomesConfig mobBiomesConfig;
    private final MineOreTypeConfig mineOreTypeConfig;
    private final ToolConfig toolConfig;
    private final PlacedOreConfig placedOreConfig;


    public DungeonConfigManager(AkaniDungeonPlugin plugin) {
        this.plugin = plugin;
        gson = new GsonBuilder()
                .registerTypeAdapter(PlacedOre.class, new PlacedOreAdapter(plugin))
                .registerTypeAdapter(MineOreType.class, new MineOreTypeAdapter())
                .registerTypeAdapter(ItemReward.class, new ItemRewardAdapter())
                .setPrettyPrinting()
                .create();
        mineOreTypeConfig = new JsonMineOreTypeConfig(gson, plugin.getDataFolder().toPath().resolve("oreTypes.json"));
        toolConfig = new ToolConfig.Dummy();
        mobBiomesConfig = new MobBiomesConfig.Dummy();
        mobSpawnerConfig = new MobSpawnerConfig.Dummy();
        this.placedOreConfig = new JsonPlacedOreConfig(gson, plugin.getDataFolder().toPath().resolve("placedOres.json"));
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

    public PlacedOreConfig placedOreConfig() {
        return placedOreConfig;
    }
}
