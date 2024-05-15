package it.einjojo.akani.dungeon.lootchest;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Collection;
import java.util.UUID;

public interface PlacedLootChest extends InventoryHolder {

    void tick(Collection<? extends Player> affectedPlayers);

    /**
     * For disconnecting players
     * @param player
     */
    void unrender(Player player);

    Location location();

    boolean open(Player player);

    boolean canOpen(UUID player);

    void onClose(InventoryCloseEvent event);

}
