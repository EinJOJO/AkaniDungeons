package it.einjojo.akani.dungeon;

import it.einjojo.akani.core.api.AkaniCoreProvider;
import it.einjojo.akani.core.paper.PaperAkaniCore;
import it.einjojo.akani.dungeon.config.DungeonConfigManager;
import it.einjojo.akani.dungeon.lootchest.LootChestManager;
import it.einjojo.akani.dungeon.mine.MineManager;
import it.einjojo.akani.dungeon.mine.SyncOreRenderer;
import it.einjojo.akani.dungeon.mine.factory.MineOreTypeFactory;
import it.einjojo.akani.dungeon.mine.factory.PlacedOreFactory;
import it.einjojo.akani.dungeon.mine.tool.ToolFactory;
import it.einjojo.akani.dungeon.mobs.AsyncMobPopulateChunkSelector;
import it.einjojo.akani.dungeon.mobs.SyncMobSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class AkaniDungeon {
    private final JavaPlugin plugin;
    private final DungeonConfigManager configManager;
    private final AsyncMobPopulateChunkSelector asyncMobPopulateChunkSelector;
    private final SyncOreRenderer syncOreRenderer;
    private final SyncMobSpawner syncMobSpawner;
    private final ToolFactory toolFactory;
    private final PlacedOreFactory placedOreFactory;
    private final MineManager mineManager;
    private final MineOreTypeFactory mineOreTypeFactory;
    private final PaperAkaniCore core;
    //loot chest
    private final LootChestManager lootChestManager;

    public AkaniDungeon(JavaPlugin plugin, DungeonConfigManager configManager) {
        this.plugin = plugin;
        core = (PaperAkaniCore) AkaniCoreProvider.get();
        core.registerMessageProvider(new DungeonMessageProvider());
        this.configManager = configManager;
        syncMobSpawner = new SyncMobSpawner();
        asyncMobPopulateChunkSelector = new AsyncMobPopulateChunkSelector(config().mobSpawnerConfig(), syncMobSpawner);
        mineManager = new MineManager(configManager.mineOreTypeConfig(), () -> core.dataSourceProxy().getConnection());
        syncOreRenderer = new SyncOreRenderer(mineManager);
        placedOreFactory = new PlacedOreFactory();
        mineOreTypeFactory = new MineOreTypeFactory();
        toolFactory = new ToolFactory(configManager.toolConfig());
        //loot chest
        lootChestManager = new LootChestManager(plugin, configManager.lootChestConfig());
    }

    public void load() {
        mineManager.load();
        lootChestManager.load();
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


    public void sendMessage(CommandSender sender, String key) {
        core.messageManager().sendMessage(sender, key);
    }


    public void startSchedulers() {
        asyncMobPopulateChunkSelector.start(plugin, configManager.mobSpawnerConfig().selectorInterval());
        syncMobSpawner.start(plugin, configManager.mobSpawnerConfig().spawnerInterval());
        syncOreRenderer.start(plugin, 5);
        lootChestManager.startTasks();
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

    public SyncOreRenderer syncOreRenderer() {
        return syncOreRenderer;
    }


    public MineOreTypeFactory mineOreTypeFactory() {
        return mineOreTypeFactory;
    }

    public LootChestManager lootChestManager() {
        return lootChestManager;
    }


    public PaperAkaniCore core() {
        return core;
    }
}
