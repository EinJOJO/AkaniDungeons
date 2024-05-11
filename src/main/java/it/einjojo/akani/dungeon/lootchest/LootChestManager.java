package it.einjojo.akani.dungeon.lootchest;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.xml.stream.Location;
import java.time.Duration;
import java.util.*;

public class LootChestManager implements PlacedChestObserver {
    private static final Random RANDOM = new Random();
    private final Map<Location, PlacedLootChest> placedChests = new HashMap<>();
    private final PlacedLootChestFactory factory;

    public LootChestManager() {
        this.factory = new PlacedLootChestFactory(this);
    }

    public void load() {

    }

    public void save() {

    }

    public Map<Location, PlacedLootChest> placedChests() {
        return placedChests;
    }

    public PlacedLootChestFactory factory() {
        return factory;
    }


    @Override
    public void onChestOpen(PlacedLootChest chest, Player player) {
        Inventory inv = chest.getInventory();
        int slot = 0;
        List<ItemStack> loot = chest.lootChest().generateRandomLoot();
        int maxSkip = inv.getSize() / loot.size(); // e.g. 27 / 4 items = 6
        for (ItemStack itemStack : loot) {
            inv.setItem(slot, itemStack);
            slot = (slot + RANDOM.nextInt(maxSkip)) % inv.getSize(); // overwrite if unlucky :)
        }
        player.openInventory(chest.getInventory());
    }

    @Override
    public void onChestClose(PlacedLootChest chest, Player player) {

    }

    @Override
    public void onLock(PlacedLootChest chest, UUID player, Duration duration) {

    }

    @Override
    public void onUnlock(PlacedLootChest chest, UUID player) {

    }
}
