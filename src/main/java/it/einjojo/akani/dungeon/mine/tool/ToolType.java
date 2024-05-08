package it.einjojo.akani.dungeon.mine.tool;

import it.einjojo.akani.dungeon.mine.Hardness;
import org.bukkit.Material;

public enum ToolType {
    PICKAXE,
    AXE,
    SHOVEL,
    HOE,
    SWORD,
    SHEARS;

    public static Material material(ToolType toolType, Hardness hardness) {
        if (toolType == SHEARS) {
            return Material.SHEARS;
        }
        String prefix = switch (hardness) {
            default -> "IRON_";
            case WOOD -> "WOODEN_";
            case STONE -> "STONE_";
            case IRON -> "IRON_";
            case GOLD -> "GOLDEN_";
            case DIAMOND -> "DIAMOND_";
            case NETHERITE -> "NETHERITE_";
        };
        String suffix = switch (toolType) {
            case PICKAXE -> "PICKAXE";
            case AXE -> "AXE";
            case SHOVEL -> "SHOVEL";
            case HOE -> "HOE";
            case SWORD -> "SWORD";
            default -> throw new IllegalStateException("Unexpected value: " + toolType);
        };
        return Material.valueOf(prefix + suffix);
    }

    /**
     * Get the tool type from a material
     *
     * @param material the material
     * @return the tool type
     * @throws IllegalArgumentException if the material is not a tool
     */
    public static ToolType fromMaterial(Material material) {
        if (material.name().endsWith("PICKAXE")) {
            return PICKAXE;
        } else if (material.name().endsWith("AXE")) {
            return AXE;
        } else if (material.name().endsWith("SHOVEL")) {
            return SHOVEL;
        } else if (material.name().endsWith("HOE")) {
            return HOE;
        } else if (material.name().endsWith("SWORD")) {
            return SWORD;
        } else if (material == Material.SHEARS) {
            return SHEARS;
        } else {
            throw new IllegalArgumentException("Unknown tool type for material " + material);
        }
    }

    public static boolean check(ToolType toolType, Material material) {
        return material.name().endsWith(toolType.name());
    }

    public boolean check(Material material) {
        return check(this, material);
    }
}

