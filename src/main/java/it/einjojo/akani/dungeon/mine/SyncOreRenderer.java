package it.einjojo.akani.dungeon.mine;

import it.einjojo.akani.dungeon.util.ChunkPosition;
import it.einjojo.akani.dungeon.util.ChunkRing;
import it.einjojo.akani.dungeon.util.RepeatingTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class SyncOreRenderer implements RepeatingTask {
    private static final ChunkRing RING = new ChunkRing(null,  //  9x9 pattern
            "2 2 2 2 2 2 2 2 2",
            "2 1 1 1 1 1 2",
            "2 1 1 1 1 1 2",
            "2 1 1 0 1 1 2",
            "2 1 1 1 1 1 2",
            "2 1 1 1 1 1 2",
            "2 2 2 2 2 2 2");
    private final MineManager mineManager;
    private final Map<UUID, List<MineChunk>> renderedChunks = new HashMap<>();
    private final Map<UUID, ChunkPosition> lastChunks = new HashMap<>();
    private BukkitTask task;

    public SyncOreRenderer(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    /**
     * Called in the given interval
     * This method will render the ores in the chunks around the player
     * It will also unrender the ores that are not in the chunks around the player
     * It will only do calculations if the player has changed a chunk.
     */
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            resetProgressions(player);
            // Check Chunk Change
            ChunkPosition currentChunk = ChunkPosition.of(player.getLocation());
            ChunkPosition last = lastChunks.get(player.getUniqueId());
            if (last != null && last.equals(currentChunk)) {
                continue;
            }
            // Player has changed chunk
            lastChunks.put(player.getUniqueId(), currentChunk);
            RING.setZero(currentChunk);
            List<MineChunk> newRendered = new ArrayList<>();
            for (Map.Entry<Character, List<ChunkPosition>> entry : RING.chunks().entrySet()) {
                for (ChunkPosition chunkPosition : entry.getValue()) {
                    // render everything
                    MineChunk mineChunk = mineManager.chunkByPosition(chunkPosition);
                    if (mineChunk == null) {
                        continue;
                    }
                    newRendered.add(mineChunk); // Add to rendered chunks
                    mineChunk.renderOres(player); // Try render ores
                }
            }

            List<MineChunk> oldRendered = renderedChunks.get(player.getUniqueId());
            if (oldRendered != null) {
                // Get all chunks that are not rendered anymore
                oldRendered.removeAll(newRendered);
                for (MineChunk mineChunk : oldRendered) {
                    mineChunk.unrenderOres(player);
                }
            }
            renderedChunks.put(player.getUniqueId(), newRendered);
        }
    }


    private void resetProgressions(Player player) {
        MineProgression progression = mineManager.progressionByPlayer(player.getUniqueId());
        if (progression == null) return;
        PlacedOre lastOre = progression.lastOre();
        if (progression.isMining()) {
            return;
        }
        if (lastOre != null) {
            progression.reset(player);
        }

    }

    public MineManager mineManager() {
        return mineManager;
    }

    public Map<UUID, List<MineChunk>> renderedChunks() {
        return renderedChunks;
    }

    public Map<UUID, ChunkPosition> lastChunk() {
        return lastChunks;
    }


    @Override
    public BukkitTask task() {
        return task;
    }

    @Override
    public void setTask(BukkitTask task) {
        this.task = task;
    }
}
