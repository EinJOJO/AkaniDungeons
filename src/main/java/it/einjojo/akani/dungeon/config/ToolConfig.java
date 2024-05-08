package it.einjojo.akani.dungeon.config;

import org.bukkit.Material;

public interface ToolConfig {
    float efficiencyDamageBonus();

    float luckLootMultiplier();

    float materialBaseDamage(Material material);

    class Dummy implements ToolConfig {
        @Override
        public float efficiencyDamageBonus() {
            return 0.5f;
        }

        @Override
        public float luckLootMultiplier() {
            return 1.25f;
        }

        @Override
        public float materialBaseDamage(Material material) {
            return switch (material) {
                case WOODEN_PICKAXE, WOODEN_AXE, WOODEN_SHOVEL, WOODEN_HOE, WOODEN_SWORD -> 1.0f;
                case STONE_PICKAXE, STONE_AXE, STONE_SHOVEL, STONE_HOE, STONE_SWORD -> 1.5f;
                case IRON_PICKAXE, IRON_AXE, IRON_SHOVEL, IRON_HOE, IRON_SWORD -> 2.0f;
                case GOLDEN_PICKAXE, GOLDEN_AXE, GOLDEN_SHOVEL, GOLDEN_HOE, GOLDEN_SWORD -> 2.5f;
                case DIAMOND_PICKAXE, DIAMOND_AXE, DIAMOND_SHOVEL, DIAMOND_HOE, DIAMOND_SWORD -> 3.0f;
                case NETHERITE_PICKAXE, NETHERITE_AXE, NETHERITE_SHOVEL, NETHERITE_HOE, NETHERITE_SWORD -> 3.5f;
                default -> 0.0f;
            };

        }
    }


}
