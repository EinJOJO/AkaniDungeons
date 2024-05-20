package it.einjojo.akani.dungeon.lootchest;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Collection;
import java.util.UUID;

public interface PlacedLootChest extends InventoryHolder {

    LootChest lootChest();

    void tick(Collection<? extends Player> affectedPlayers);

    /**
     * For disconnecting players
     *
     * @param player
     */
    void tryDespawn(Player player);

    void trySpawn(Player player);

    Location location();

    /**
     * @param player The player to check
     * @return true if the player can open the chest
     * @throws IllegalStateException if thread is not main
     */
    boolean open(Player player);

    boolean canOpen(UUID player);

    void onClose(InventoryCloseEvent event);

}
