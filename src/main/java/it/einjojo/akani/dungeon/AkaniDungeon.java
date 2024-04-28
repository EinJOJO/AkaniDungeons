package it.einjojo.akani.dungeon;

import it.einjojo.akani.dungeon.config.DungeonConfig;
import it.einjojo.akani.dungeon.mine.SyncOreRenderer;
import it.einjojo.akani.dungeon.mobs.AsyncMobPopulateChunkSelector;
import it.einjojo.akani.dungeon.mobs.SyncMobSpawner;
import it.einjojo.akani.dungeon.mobs.factory.DefaultSpawnableFactory;
import it.einjojo.akani.dungeon.mobs.factory.SpawnableFactory;
import org.bukkit.plugin.java.JavaPlugin;

public class AkaniDungeon {
    private final JavaPlugin plugin;
    private final DungeonConfig config;
    private final AsyncMobPopulateChunkSelector asyncMobPopulateChunkSelector;
    private final SyncOreRenderer syncOreRenderer;
    private final SyncMobSpawner syncMobSpawner;
    private final SpawnableFactory spawnableFactory;

    public AkaniDungeon(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = new DungeonConfig(plugin);
        asyncMobPopulateChunkSelector = new AsyncMobPopulateChunkSelector(this);
        syncOreRenderer = new SyncOreRenderer();
        syncMobSpawner = new SyncMobSpawner(this);
        spawnableFactory = new DefaultSpawnableFactory();
    }

    protected void enable() {
        startSchedulers();
    }

    protected void disable() {


    }


    public void startSchedulers() {
        asyncMobPopulateChunkSelector.start(plugin, config.mobSpawnerConfig().selectorInterval());
        syncOreRenderer.start(plugin, 2);
        syncMobSpawner.start(plugin, config.mobSpawnerConfig().spawnerInterval());
    }

    public DungeonConfig config() {
        return config;
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
}
