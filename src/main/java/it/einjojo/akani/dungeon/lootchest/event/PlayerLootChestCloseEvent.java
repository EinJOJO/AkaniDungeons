package it.einjojo.akani.dungeon.lootchest.event;

import it.einjojo.akani.dungeon.lootchest.PlacedLootChest;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerLootChestCloseEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final PlacedLootChest openedChest;

    public PlayerLootChestCloseEvent(@NotNull Player who, PlacedLootChest openedChest) {
        super(who);
        this.openedChest = openedChest;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player player() {
        return getPlayer();
    }

    public PlacedLootChest openedChest() {
        return openedChest;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
