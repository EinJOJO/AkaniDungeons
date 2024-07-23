package it.einjojo.akani.dungeon.gui;

import it.einjojo.akani.util.item.SkullCreator;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import it.einjojo.akani.util.item.ItemBuilder;
import it.einjojo.akani.util.inventory.*;

import java.util.List;

public enum GUIItem {
    BACKGROUND(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName(Component.empty()).build()),
    PLUS_SKULL(SkullCreator.itemFromBase64("")),
    LEFT_SKULL(SkullCreator.itemFromBase64("")),
    RIGHT_SKULL(SkullCreator.itemFromBase64("")),
    ADD_BUTTON(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).displayName(Component.text("§aErz hinzufügen")).lore(List.of(Component.empty(), Component.text("§7Klicke hier, um ein neues Erz hinzuzufügen."), Component.text("§7Unterstützt Drag & Drop!"), Component.empty())).build());

    private final ItemStack itemStack;

    GUIItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }


    public Icon icon() {
        return new it.einjojo.akani.util.inventory.Icon(itemStack());
    }

    public ItemStack itemStack() {
        return itemStack.clone();
    }
}
