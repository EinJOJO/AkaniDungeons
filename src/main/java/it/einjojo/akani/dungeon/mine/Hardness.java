package it.einjojo.akani.dungeon.mine;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public enum Hardness {
    UNDETERMINED("§cNicht Definiert", Material.BARRIER),
    WOOD("§cHolz", Material.OAK_PLANKS),
    STONE("§7Stein", Material.STONE),
    IRON("§fEisen", Material.IRON_INGOT),
    GOLD("§6Gold", Material.GOLD_INGOT),
    DIAMOND("§bDiamant", Material.DIAMOND),
    NETHERITE("§5Netherite", Material.NETHERITE_INGOT);
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

    public final String name;
    private final Material iconMaterial;

    Hardness(String name, Material iconMaterial) {
        this.name = name;
        this.iconMaterial = iconMaterial;
    }

    public static boolean canBreak(Material tool, Hardness hardness) {
        return HARDNESS_MAP.get(tool) != null && HARDNESS_MAP.get(tool).ordinal() >= hardness.ordinal();
    }

    public Component displayName() {
        return Component.text(name);
    }

    public String plainDisplayName() {
        return name;
    }

    public Material icon() {
        return iconMaterial;
    }

}
