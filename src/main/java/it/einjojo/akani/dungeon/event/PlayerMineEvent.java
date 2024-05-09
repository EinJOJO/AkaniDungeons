package it.einjojo.akani.dungeon.event;

import it.einjojo.akani.dungeon.mine.PlacedOre;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Event that is called when a player mines a block successfully.
 */
public class PlayerMineEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final PlacedOre ore;
    private final List<ItemStack> drops;
    private boolean cancelled = false;

    public PlayerMineEvent(@NotNull Player who, PlacedOre ore, List<ItemStack> drops) {
        super(who);
        this.ore = ore;
        this.drops = drops;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public PlacedOre getOre() {
        return ore;
    }
}
