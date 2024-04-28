package it.einjojo.akani.dungeon.util;

import it.einjojo.akani.dungeon.mobs.ChunkPosition;
import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkRing {
    private final Map<Character, List<ChunkPosition>> chunks = new HashMap<>();

    public ChunkRing(Chunk zero, String... patternString) {
        char[][] pattern = new char[patternString.length][];
        for (int i = 0; i < patternString.length; i++) {
            pattern[i] = patternString[i].toCharArray();
        }
        int zeroX = 0, zeroZ = 0;

        // Find the zero position
        for (int z = 0; z < pattern.length; z++) {
            for (int x = 0; x < pattern[z].length; x++) {
                if (pattern[z][x] == '0') {
                    zeroX = x;
                    zeroZ = z;
                    break;
                }
            }
        }

        // Find the chunks for the given patternId
        for (int z = 0; z < pattern.length; z++) {
            for (int x = 0; x < pattern[z].length; x++) {
                if (pattern[z][x] == '0') {
                    continue;
                }
                int chunkX = zero.getX() + x - zeroX;
                int chunkZ = zero.getZ() + z - zeroZ;
                chunks.computeIfAbsent(pattern[z][x], k -> new ArrayList<>()).add(new ChunkPosition(chunkX, chunkZ));
            }
        }
    }

    public List<ChunkPosition> getChunks(char patternId) {
        return chunks.getOrDefault(patternId, List.of());
    }
}