package it.einjojo.akani.dungeon.mine;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public enum Hardness {
    UNDETERMINED,
    WOOD,
    STONE,
    GOLD,
    IRON,
    DIAMOND,
    NETHERITE;
    public static final Map<Material, Hardness> HARDNESS_MAP = new HashMap<>();

    static {
        for (Material material : Material.values()) {
            if (!(material.name().endsWith("_AXE") || material.name().endsWith("_PICKAXE") || material.name().endsWith("_HOE") || material.name().endsWith("_SHOVEL"))) {
                continue;
            }
            Hardness hardness;
            if (material.name().startsWith("DIAMOND")) {
                hardness = DIAMOND;
            } else if (material.name().startsWith("NETHERITE")) {
                hardness = NETHERITE;
            } else if (material.name().startsWith("IRON")) {
                hardness = IRON;
            } else if (material.name().startsWith("GOLD")) {
                hardness = GOLD;
            } else if (material.name().startsWith("STONE")) {
                hardness = STONE;
            } else if (material.name().startsWith("WOOD")) {
                hardness = WOOD;
            } else {
                hardness = UNDETERMINED;
            }
            HARDNESS_MAP.put(material, hardness);
        }
    }

    public static boolean canBreak(Material tool, Hardness hardness) {
        return HARDNESS_MAP.get(tool) != null && HARDNESS_MAP.get(tool).ordinal() >= hardness.ordinal();
    }



}
