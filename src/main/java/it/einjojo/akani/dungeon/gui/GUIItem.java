package it.einjojo.akani.dungeon.gui;

import fr.minuskube.inv.ClickableItem;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public enum GUIItem {
    BACKGROUND(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName(Component.empty()));
    private final ItemBuilder itemBuilder;

    GUIItem(ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
    }

    public ItemBuilder itemBuilder() {
        return itemBuilder;
    }

    public ClickableItem emptyClickableItem() {
        return ClickableItem.empty(itemStack());
    }

    public ClickableItem clickableItem(Consumer<InventoryClickEvent> clickAction) {
        return ClickableItem.of(itemStack(), clickAction);
    }

    public ItemStack itemStack() {
        return itemBuilder.build();
    }
}
