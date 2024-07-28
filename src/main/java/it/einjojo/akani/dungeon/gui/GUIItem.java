package it.einjojo.akani.dungeon.gui;

import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.util.item.SkullCreator;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import it.einjojo.akani.util.inventory.*;

import java.util.List;

public enum GUIItem {
    BACKGROUND(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName(Component.empty()).build()),
    PLUS_SKULL(new ItemBuilder(Material.PLAYER_HEAD).skullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19").build()),
    LEFT_SKULL(new ItemBuilder(Material.PLAYER_HEAD).skullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2EyYzEyY2IyMjkxODM4NGUwYTgxYzgyYTFlZDk5YWViZGNlOTRiMmVjMjc1NDgwMDk3MjMxOWI1NzkwMGFmYiJ9fX0=").build()),
    RIGHT_SKULL(new ItemBuilder(Material.PLAYER_HEAD).skullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjkxYWM0MzJhYTQwZDdlN2E2ODdhYTg1MDQxZGU2MzY3MTJkNGYwMjI2MzJkZDUzNTZjODgwNTIxYWYyNzIzYSJ9fX0=").build()),
    ADD_BUTTON(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).displayName(Component.text("§aErz hinzufügen")).lore(List.of(Component.empty(), Component.text("§7Klicke hier, um ein neues Erz hinzuzufügen."), Component.text("§7Unterstützt Drag & Drop!"), Component.empty())).build());

    private final ItemStack itemStack;

    GUIItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }


    public Icon icon() {
        return new Icon(itemStack());
    }

    public ItemStack itemStack() {
        return itemStack.clone();
    }
}
