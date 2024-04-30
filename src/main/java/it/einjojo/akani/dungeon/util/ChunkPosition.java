package it.einjojo.akani.dungeon.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Random;

public record ChunkPosition(int x, int z) {
    private static final Random random = new Random();

    public static ChunkPosition of(Chunk chunk) {
        return new ChunkPosition(chunk.getX(), chunk.getZ());
    }

    public static ChunkPosition of(Location location) {
        return new ChunkPosition(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    public Chunk toChunk(World world) {
        return world.getChunkAt(x, z);
    }

    public Biome getBiome(World world) {
        return world.getBiome(x * 16, 80, z * 16);
    }

    public Location randomSpawnableLocation(World world) {
        int rx = random.nextInt(16) + x * 16;
        int rz = random.nextInt(16) + z * 16;
        int ry = world.getHighestBlockYAt(rx, rz) + 1;
        return new Location(world, rx, ry, rz);
    }

}
