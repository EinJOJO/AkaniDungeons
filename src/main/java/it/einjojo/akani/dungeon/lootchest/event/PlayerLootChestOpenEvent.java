package it.einjojo.akani.dungeon.lootchest.event;

import it.einjojo.akani.dungeon.lootchest.PlacedLootChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerLootChestOpenEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final PlacedLootChest closedChest;
    private boolean cancelled = false;

    public PlayerLootChestOpenEvent(@NotNull Player who, PlacedLootChest closed) {
        super(who);
        this.closedChest = closed;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Player player() {
        return getPlayer();
    }

    public PlacedLootChest closedChest() {
        return closedChest;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
