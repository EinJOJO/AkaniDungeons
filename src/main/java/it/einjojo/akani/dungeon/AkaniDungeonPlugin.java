package it.einjojo.akani.dungeon;

import co.aikar.commands.PaperCommandManager;
import it.einjojo.akani.dungeon.command.BuildCommand;
import it.einjojo.akani.dungeon.command.MineOreCommand;
import it.einjojo.akani.dungeon.config.DungeonConfigManager;
import it.einjojo.akani.dungeon.gui.GuiManager;
import it.einjojo.akani.dungeon.listener.DungeonWorldListener;
import it.einjojo.akani.dungeon.listener.InputListener;
import it.einjojo.akani.dungeon.listener.MineListener;
import it.einjojo.akani.dungeon.listener.OreAttackPacketListener;
import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class AkaniDungeonPlugin extends JavaPlugin {
    private static final Logger log = LoggerFactory.getLogger(AkaniDungeonPlugin.class);
    private AkaniDungeon akaniDungeon;
    private PaperCommandManager commandManager;
    private GuiManager guiManager;
    private DungeonWorldListener dungeonWorldListener;

    @Override
    public void onEnable() {
        DungeonConfigManager dungeonConfigManager = new DungeonConfigManager(this);
        try {
            dungeonConfigManager.load();
        } catch (Exception e) {
            log.error("Error loading config", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        akaniDungeon = new AkaniDungeon(this, dungeonConfigManager);
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            akaniDungeon.mineManager().load();
        });
        akaniDungeon.startSchedulers();
        guiManager = new GuiManager(this, dungeonConfigManager.mineOreTypeConfig(), akaniDungeon.mineOreTypeFactory());
        new OreAttackPacketListener(akaniDungeon.mineManager(), akaniDungeon.toolFactory(), this);
        new MineListener(this, akaniDungeon);
        dungeonWorldListener = new DungeonWorldListener(this);
        new InputListener(this);
        registerCommands();
    }


    public void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.enableUnstableAPI("brigadier");
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
            log.warn("Exception in command {}", command.getName(), t);
            return false;
        }, false);
        commandManager.registerDependency(AkaniDungeon.class, akaniDungeon);
        commandManager.registerDependency(GuiManager.class, guiManager);
        commandManager.registerCommand(new MineOreCommand());
        commandManager.registerCommand(new BuildCommand(dungeonWorldListener));

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
