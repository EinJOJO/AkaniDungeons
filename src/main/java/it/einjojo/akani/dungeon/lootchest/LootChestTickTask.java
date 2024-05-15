package it.einjojo.akani.dungeon.lootchest;

import it.einjojo.akani.dungeon.util.ChunkPosition;
import it.einjojo.akani.dungeon.util.ChunkRing;
import it.einjojo.akani.dungeon.util.RepeatingTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class LootChestTickTask implements RepeatingTask {
    private static final ChunkRing RING = new ChunkRing(null, "1 1 1 1 1",
            "1 1 1 1 1",
            "1 1 0 1 1",
            "1 1 1 1 1",
            "1 1 1 1 1");
    private final Map<ChunkPosition, List<PlacedLootChest>> tickingChests = new HashMap<>();
    private BukkitTask task;

    public boolean add(PlacedLootChest chest) {
        ChunkPosition position = ChunkPosition.of(chest.location());
        tickingChests.putIfAbsent(position, new LinkedList<>());
        return tickingChests.get(position).add(chest);
    }

    public boolean remove(PlacedLootChest chest) {
        ChunkPosition position = ChunkPosition.of(chest.location());
        return tickingChests.get(position).remove(chest);
    }


    @Override
    public BukkitTask task() {
        return task;
    }

    @Override
    public void setTask(BukkitTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        Collection<? extends Player> affectedPlayers = Bukkit.getOnlinePlayers();
        if (affectedPlayers.isEmpty()) return;
        for (ChunkPosition chunk : loadedChunks()) {
            for (PlacedLootChest chest : tickingChests.getOrDefault(chunk, List.of())) {
                chest.tick(affectedPlayers);
            }
        }
    }

    private List<ChunkPosition> loadedChunks() {
        List<ChunkPosition> loadedChunks = new LinkedList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            RING.setZero(ChunkPosition.of(player.getLocation()));
            for (List<ChunkPosition> chunks : RING.chunks().values()) {
                loadedChunks.addAll(chunks);
            }
        }

        return loadedChunks;
    }
}
