package it.einjojo.akani.dungeon.lootchest;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public interface PlacedLootChest extends InventoryHolder {

    void tick();

    Location location();

    boolean open(Player player);

    boolean canOpen(UUID player);

    void onClose(InventoryCloseEvent event);

}
