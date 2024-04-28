package it.einjojo.akani.dungeon;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AkaniDungeonPlugin extends JavaPlugin {
    private AkaniDungeon akaniDungeon;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        akaniDungeon = new AkaniDungeon(this);
        akaniDungeon.enable();
        registerCommands();
    }

    public void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("brigadier");

    }

    @Override
    public void onDisable() {
        if (akaniDungeon != null) akaniDungeon.disable();
    }
}
