package it.einjojo.akani.dungeon;

import it.einjojo.akani.dungeon.config.DungeonConfigManager;
import it.einjojo.akani.dungeon.mine.MineManager;
import it.einjojo.akani.dungeon.mine.SyncOreRenderer;
import it.einjojo.akani.dungeon.mine.factory.MineOreTypeFactory;
import it.einjojo.akani.dungeon.mine.factory.PlacedOreFactory;
import it.einjojo.akani.dungeon.mine.tool.ToolFactory;
import it.einjojo.akani.dungeon.mobs.AsyncMobPopulateChunkSelector;
import it.einjojo.akani.dungeon.mobs.SyncMobSpawner;
import it.einjojo.akani.dungeon.mobs.factory.DefaultSpawnableFactory;
import it.einjojo.akani.dungeon.mobs.factory.SpawnableFactory;
import org.bukkit.plugin.java.JavaPlugin;

public class AkaniDungeon {
    private final JavaPlugin plugin;
    private final DungeonConfigManager configManager;
    private final AsyncMobPopulateChunkSelector asyncMobPopulateChunkSelector;
    private final SyncOreRenderer syncOreRenderer;
    private final SyncMobSpawner syncMobSpawner;
    private final SpawnableFactory spawnableFactory;
    private final ToolFactory toolFactory;
    private final PlacedOreFactory placedOreFactory;
    private final MineManager mineManager;
    private final MineOreTypeFactory mineOreTypeFactory;

    public AkaniDungeon(JavaPlugin plugin, DungeonConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        asyncMobPopulateChunkSelector = new AsyncMobPopulateChunkSelector(this);
        mineManager = new MineManager();
        syncOreRenderer = new SyncOreRenderer(mineManager);
        syncMobSpawner = new SyncMobSpawner(this);
        spawnableFactory = new DefaultSpawnableFactory();
        placedOreFactory = new PlacedOreFactory();
        mineOreTypeFactory = new MineOreTypeFactory();
        toolFactory = new ToolFactory(configManager.toolConfig());

    }

    public ToolFactory toolFactory() {
        return toolFactory;
    }

    public PlacedOreFactory placedOreFactory() {
        return placedOreFactory;
    }

    public MineManager mineManager() {
        return mineManager;
    }


    public void startSchedulers() {
        asyncMobPopulateChunkSelector.start(plugin, configManager.mobSpawnerConfig().selectorInterval());
        syncOreRenderer.start(plugin, 5);
        syncMobSpawner.start(plugin, configManager.mobSpawnerConfig().spawnerInterval());
    }

    public DungeonConfigManager config() {
        return configManager;
    }

    public DungeonConfigManager configManager() {
        return configManager;
    }

    public JavaPlugin plugin() {
        return plugin;
    }

    public AsyncMobPopulateChunkSelector asyncMobPopulateChunkSelector() {
        return asyncMobPopulateChunkSelector;
    }

    public SyncOreRenderer syncOreRenderer() {
        return syncOreRenderer;
    }

    public SyncMobSpawner syncMobSpawner() {
        return syncMobSpawner;
    }

    public SpawnableFactory spawnableFactory() {
        return spawnableFactory;
    }

    public MineOreTypeFactory mineOreTypeFactory() {
        return mineOreTypeFactory;
    }
}
