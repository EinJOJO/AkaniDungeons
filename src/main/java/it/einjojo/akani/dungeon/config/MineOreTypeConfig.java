package it.einjojo.akani.dungeon.config;

import it.einjojo.akani.dungeon.mine.MineOreType;

import java.util.List;

public interface MineOreTypeConfig {

    void addOreType(MineOreType oreType);

    void removeOreType(MineOreType remove);

    List<MineOreType> types();
}
