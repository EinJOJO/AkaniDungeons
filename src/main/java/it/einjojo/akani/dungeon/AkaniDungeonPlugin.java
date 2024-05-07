package it.einjojo.akani.dungeon;

import co.aikar.commands.PaperCommandManager;
import it.einjojo.akani.dungeon.command.MineOreCommand;
import it.einjojo.akani.dungeon.config.DungeonConfigManager;
import it.einjojo.akani.dungeon.gui.GuiManager;
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

    @Override
    public void onEnable() {
        DungeonConfigManager dungeonConfigManager = new DungeonConfigManager(this);
        dungeonConfigManager.load();
        akaniDungeon = new AkaniDungeon(this, dungeonConfigManager);
        akaniDungeon.startSchedulers();
        guiManager = new GuiManager(dungeonConfigManager.mineOreTypeConfig());
        registerCommands();
        new OreAttackPacketListener(akaniDungeon.mineManager());
        new MineListener(this, akaniDungeon);
    }


    public void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.getCommandCompletions().registerAsyncCompletion("oreTypes", (c) -> akaniDungeon.config().mineOreTypeConfig().types().stream().map(MineOreType::name).toList());
        commandManager.getCommandCompletions().registerStaticCompletion("oreHardness", () -> Arrays.stream(Hardness.values()).map(Enum::name).toList());
        commandManager.getCommandContexts().registerContext(MineOreType.class, (c) -> akaniDungeon.config().mineOreTypeConfig().types().stream().filter(t -> t.name().equals(c.popFirstArg())).findFirst().orElseThrow());
        commandManager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            if (t instanceof NoSuchElementException ex) {
                ((CommandSender) sender.getIssuer()).sendMessage("§cNicht gefunden.");
                return true;
            }
            log.warn("Exception in command {}", command.getName(), t);
            return false;
        }, false);
        commandManager.enableUnstableAPI("brigadier");
        commandManager.registerDependency(AkaniDungeon.class, akaniDungeon);
        commandManager.registerDependency(GuiManager.class, guiManager);
        commandManager.registerCommand(new MineOreCommand());

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
