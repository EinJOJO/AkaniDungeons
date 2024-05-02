package it.einjojo.akani.dungeon.config;

import it.einjojo.akani.dungeon.mine.MineOreType;

import java.util.List;

public interface MineOreTypeConfig {

    void load();

    void save();

    void addOreType(MineOreType oreType);

    void removeOreType(MineOreType remove);

    List<MineOreType> types();
}
