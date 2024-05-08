package it.einjojo.akani.dungeon.mine.tool;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ToolIdentifier {
    boolean matches(ItemStack itemStack);

    /**
     * Tool identifier based on the custom model data of the item stack
     */
    class ModelDataToolIdentifier implements ToolIdentifier {
        private final int modelData;

        public ModelDataToolIdentifier(int modelData) {
            this.modelData = modelData;
        }

        @Override
        public boolean matches(ItemStack itemStack) {
            if (!itemStack.getItemMeta().hasCustomModelData()) {
                return false;
            }
            return itemStack.getItemMeta().getCustomModelData() == modelData;
        }
    }

    /**
     * Tool identifier based on the material of the item stack
     */
    class MaterialToolIdentifier implements ToolIdentifier {
        private final Material material;

        public MaterialToolIdentifier(Material material) {
            this.material = material;
        }

        @Override
        public boolean matches(ItemStack itemStack) {
            return itemStack.getType() == material;
        }
    }

}
