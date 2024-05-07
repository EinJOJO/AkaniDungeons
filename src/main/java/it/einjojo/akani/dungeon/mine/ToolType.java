package it.einjojo.akani.dungeon.mine;

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
}

