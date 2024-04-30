package it.einjojo.akani.dungeon.mine;

import io.github.retrooper.packetevents.util.SpigotReflectionUtil;

public record MineOreFactory() {

    public MineOre createMineOre() {
        int entityId = SpigotReflectionUtil.generateEntityId();
        return null;
        //return new MineOre(entityId);
    }

}
