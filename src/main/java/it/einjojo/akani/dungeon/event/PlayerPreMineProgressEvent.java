package it.einjojo.akani.dungeon.event;

import it.einjojo.akani.dungeon.mine.MineProgression;
import it.einjojo.akani.dungeon.mine.PlacedOre;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerPreMineProgressEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final PlacedOre ore;
    private final MineProgression progression;
    private float progress;
    private boolean cancelled = false;

    public PlayerPreMineProgressEvent(@NotNull Player who, PlacedOre ore, MineProgression progression, float progress) {
        super(who);
        this.ore = ore;
        this.progression = progression;
        this.progress = progress;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PlacedOre getOre() {
        return ore;
    }

    /**
     * The added progress that the player has made on the block.
     *
     * @return The progress made on the block.
     */
    public float getProgress() {
        return progress;
    }

    /**
     * Set the additional progress that the player has made on the block.
     *
     * @param progress The progress made on the block.
     */
    public void setProgress(float progress) {
        this.progress = progress;
    }

    public MineProgression getProgression() {
        return progression;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
