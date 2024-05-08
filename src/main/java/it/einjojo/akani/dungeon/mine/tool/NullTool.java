package it.einjojo.akani.dungeon.mine.tool;

public class NullTool implements Tool {
    @Override
    public float damage() {
        return 0;
    }

    @Override
    public float lootMultiplier() {
        return 0;
    }

    @Override
    public boolean isBypassMaximumLoot() {
        return false;
    }

    @Override
    public ToolType type() {
        return null;
    }
}
