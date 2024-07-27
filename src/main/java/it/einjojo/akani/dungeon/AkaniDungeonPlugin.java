package it.einjojo.akani.dungeon;

import it.einjojo.akani.dungeon.listener.*;
import it.einjojo.akani.util.commands.PaperCommandManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import it.einjojo.akani.dungeon.command.BuildCommand;
import it.einjojo.akani.dungeon.command.ChunkMobsCommand;
import it.einjojo.akani.dungeon.command.LootChestCommand;
import it.einjojo.akani.dungeon.command.MineOreCommand;
import it.einjojo.akani.dungeon.config.DungeonConfigManager;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class AkaniDungeonPlugin extends JavaPlugin {
    private static AkaniDungeonPlugin singleton;
    private AkaniDungeon akaniDungeon;
    private PaperCommandManager commandManager;
    private DungeonWorldListener dungeonWorldListener;

    public static AkaniDungeonPlugin get() {
        return singleton;
    }

    @Override
    public void onEnable() {
        singleton = this;
        DungeonConfigManager dungeonConfigManager = new DungeonConfigManager(this);
        try {
            dungeonConfigManager.load();
        } catch (Exception e) {
            getSLF4JLogger().error("Error loading config", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        akaniDungeon = new AkaniDungeon(this, dungeonConfigManager);
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            getSLF4JLogger().info("Loading dungeon");
            akaniDungeon.load();
        });
        akaniDungeon.startSchedulers();
        registerListener();
        registerCommands();
    }

    protected void registerListener() {
        new OreAttackPacketListener(akaniDungeon.mineManager(), akaniDungeon.toolFactory(), this);
        new MineListener(this, akaniDungeon);
        dungeonWorldListener = new DungeonWorldListener(this);
        new InputListener(this);
        // LootChestListener is registered inside the manager
    }

    public void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.getCommandCompletions().registerAsyncCompletion("mythicmobs", (c) -> MythicBukkit.inst().getMobManager().getMobNames());
        commandManager.getCommandCompletions().registerAsyncCompletion("lootChests", (c) -> akaniDungeon.lootChestManager().lootChests().stream().map(LootChest::name).toList());
        commandManager.getCommandCompletions().registerAsyncCompletion("oreTypes", (c) -> akaniDungeon.config().mineOreTypeConfig().types().stream().map(MineOreType::name).toList());
        commandManager.getCommandCompletions().registerStaticCompletion("oreHardness", () -> Arrays.stream(Hardness.values()).map(Enum::name).toList());
        commandManager.getCommandContexts().registerContext(MineOreType.class, (c) -> {
            String input = c.popFirstArg();
            System.out.println(input);
            return akaniDungeon.config().mineOreTypeConfig().types().stream().filter(t -> t.name().equalsIgnoreCase(input)).findFirst().orElseThrow();
        });
        commandManager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            if (t instanceof NoSuchElementException ex) {
                ((CommandSender) sender.getIssuer()).sendMessage("§cNicht gefunden.");
                return true;
            }
            getSLF4JLogger().warn("Exception in command {}", command.getName(), t);
            return false;
        }, false);
        commandManager.registerDependency(AkaniDungeon.class, akaniDungeon);
        commandManager.registerCommand(new MineOreCommand());
        commandManager.registerCommand(new BuildCommand(dungeonWorldListener));
        commandManager.registerCommand(new LootChestCommand());
        commandManager.registerCommand(new ChunkMobsCommand(akaniDungeon.config().mobSpawnerConfig()));

    }

    public AkaniDungeon akaniDungeon() {
        return akaniDungeon;
    }

    public PaperCommandManager commandManager() {
        return commandManager;
    }

    @Override
    public void onDisable() {
    }


}
