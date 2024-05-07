package it.einjojo.akani.dungeon.mine;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface UnspecificItem {
    static boolean matches(UnspecificItem unspecificItem, ItemStack itemStack) {
        if (unspecificItem.material() != itemStack.getType()) {
            return false;
        }
        if (unspecificItem.hasCustomModelData() && unspecificItem.customModelData() != itemStack.getItemMeta().getCustomModelData()) {
            return false;
        }
        return true;
    }

    default boolean matches(ItemStack itemStack) {
        return matches(this, itemStack);
    }

    Material material();

    boolean hasCustomModelData();

    int customModelData();



}
