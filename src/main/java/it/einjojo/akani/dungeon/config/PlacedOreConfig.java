package it.einjojo.akani.dungeon.config;

import it.einjojo.akani.dungeon.mine.PlacedOre;

import java.util.Collection;
import java.util.List;

public interface PlacedOreConfig {

    List<PlacedOre> load();

    void save(Collection<PlacedOre> ores);

}
