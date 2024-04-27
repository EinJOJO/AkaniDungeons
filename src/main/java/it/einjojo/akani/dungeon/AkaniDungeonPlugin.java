package it.einjojo.akani.dungeon;

import org.bukkit.plugin.java.JavaPlugin;

public class AkaniDungeonPlugin extends JavaPlugin {
    private AkaniDungeon akaniDungeon;

    @Override
    public void onEnable() {
        akaniDungeon = new AkaniDungeon(this);
        akaniDungeon.enable();
    }

    @Override
    public void onDisable() {
        if (akaniDungeon != null) akaniDungeon.disable();
    }
}
