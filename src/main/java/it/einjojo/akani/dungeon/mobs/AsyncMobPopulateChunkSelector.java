package it.einjojo.akani.dungeon.mobs;

import it.einjojo.akani.dungeon.AkaniDungeon;
import it.einjojo.akani.dungeon.util.ChunkRing;
import it.einjojo.akani.dungeon.util.RepeatingTask;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AsyncMobPopulateChunkSelector implements RepeatingTask {
    public final static double ZERO_REFILL = 0.05;
    public final static double RING1_REFILL = 0.15;
    public final static double RING2_REFILL = 0.3;
    private static final Random RANDOM = new Random();
    private static final String[] SPAWN_PATTERN = new String[]{
            "22222",
            "21112",
            "21012",
            "21112",
            "22222"
    };
    private static final Logger logger = LoggerFactory.getLogger(AsyncMobPopulateChunkSelector.class);
    private final AkaniDungeon akaniDungeon;
    private BukkitTask task;

    public AsyncMobPopulateChunkSelector(AkaniDungeon akaniDungeon) {
        this.akaniDungeon = akaniDungeon;
    }


    @Override
    public void run() {
        World world = Bukkit.getWorld(akaniDungeon.config().mobSpawnerConfig().worldName());
        if (world == null) {
            return;
        }
        if (world.getPlayerCount() == 0) {
            return;
        }
        Set<ChunkPosition> specialChunks = akaniDungeon.config().mobSpawnerConfig().refillOverwrites().keySet();
        Set<ChunkPosition> zeros = new HashSet<>();
        Set<ChunkPosition> ring1 = new HashSet<>();
        Set<ChunkPosition> ring2 = new HashSet<>();
        for (Player player : world.getPlayers()) {
            ChunkRing ring = new ChunkRing(player.getChunk(), SPAWN_PATTERN);
            zeros.add(ChunkPosition.of(player.getChunk()));
            ring1.addAll(ring.getChunks('1'));
            ring2.addAll(ring.getChunks('2'));
        }
        // subtract them
        ring1.removeAll(zeros);
        ring2.removeAll(zeros);
        ring2.removeAll(ring1);
        zeros.removeAll(specialChunks);
        ring1.removeAll(specialChunks);
        ring2.removeAll(specialChunks);
        logger.info("Zero: {}, Ring1: {}, Ring2: {}, Special: {}", zeros.size(), ring1.size(), ring2.size(), specialChunks.size());
        // -> zero > ring1 > ring2
        refill(world, zeros, ZERO_REFILL, true);
        refill(world, ring1, RING1_REFILL, true);
        refill(world, ring2, RING2_REFILL, true);
        for (ChunkPosition chunk : specialChunks) {
            refill(world, Set.of(chunk), akaniDungeon.config().mobSpawnerConfig().refillOverwrites().get(chunk), false);
        }
    }

    private void refill(World world, Set<ChunkPosition> chunks, double refill, boolean randomlySkip) {
        int maxMobs = akaniDungeon.config().mobSpawnerConfig().maxMobsPerChunk();
        int skipped = 0;
        for (ChunkPosition chunkPos : chunks) {
            if (randomlySkip && RANDOM.nextInt(3) == 0) { // 1/3 chance to skip
                skipped++;
                continue;
            }
            Biome biome = chunkPos.getBiome(world);
            List<String> mobIds = akaniDungeon.config().mobBiomesConfig().mobIds(biome);
            if (mobIds.isEmpty()) {
                continue;
            }
            Chunk chunk = chunkPos.toChunk(world);
            int entityAmount = chunk.getEntities().length;
            if (entityAmount >= maxMobs) {
                continue;
            }
            int newMobs = (int) Math.ceil((maxMobs - entityAmount) * refill); // example: refill 30% of the missing mobs
            Iterator<String> mobIdIterator = mobIds.iterator();
            for (int i = 0; i < newMobs; i++) {
                if (!mobIdIterator.hasNext()) {
                    mobIdIterator = mobIds.iterator();
                }
                akaniDungeon.syncMobSpawner().add(akaniDungeon.spawnableFactory().create(mobIdIterator.next(), chunkPos.randomSpawnableLocation(world)));
            }
        }
        logger.info("Skipped {} chunks and refilled {} with {} refill-rate ", skipped, chunks.size() - skipped, refill);
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
