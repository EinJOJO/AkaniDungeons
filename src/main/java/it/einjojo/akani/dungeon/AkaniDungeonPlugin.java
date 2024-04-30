package it.einjojo.akani.dungeon;

import co.aikar.commands.PaperCommandManager;
import it.einjojo.akani.dungeon.listener.MineListener;
import it.einjojo.akani.dungeon.listener.OreAttackPacketListener;
import org.bukkit.plugin.java.JavaPlugin;

public class AkaniDungeonPlugin extends JavaPlugin {
    private AkaniDungeon akaniDungeon;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        akaniDungeon = new AkaniDungeon(this);
        akaniDungeon.startSchedulers();
        registerCommands();
        new OreAttackPacketListener(akaniDungeon.mineManager());
        new MineListener(akaniDungeon.mineManager(), this);
    }

    public void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("brigadier");

    }

    @Override
    public void onDisable() {
    }
}
