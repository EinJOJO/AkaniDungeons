package it.einjojo.akani.dungeon.event;

import it.einjojo.akani.dungeon.mine.PlacedOre;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Event that is called when a player mines a block successfully.
 */
public class AsyncPlayerMineEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final PlacedOre ore;
    private List<ItemStack> drops;
    private boolean cancelled = false;

    public AsyncPlayerMineEvent(@NotNull Player who, PlacedOre ore, List<ItemStack> drops) {
        super(who, true);
        this.ore = ore;
        this.drops = drops;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    public void setDrops(@Nullable List<ItemStack> drops) {
        this.drops = drops;
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
