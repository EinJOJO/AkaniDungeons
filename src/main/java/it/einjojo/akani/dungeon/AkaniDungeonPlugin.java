package it.einjojo.akani.dungeon;

import co.aikar.commands.PaperCommandManager;
import it.einjojo.akani.dungeon.command.MineOreCommand;
import it.einjojo.akani.dungeon.config.DungeonConfigManager;
import it.einjojo.akani.dungeon.gui.GuiManager;
import it.einjojo.akani.dungeon.listener.MineListener;
import it.einjojo.akani.dungeon.listener.OreAttackPacketListener;
import org.bukkit.plugin.java.JavaPlugin;

public class AkaniDungeonPlugin extends JavaPlugin {
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
