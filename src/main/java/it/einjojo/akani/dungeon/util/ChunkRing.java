package it.einjojo.akani.dungeon.util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkRing {
    private final char[][] pattern;
    private final Map<Character, List<ChunkPosition>> chunks = new HashMap<>();
    private @Nullable ChunkPosition zero;

    public ChunkRing(@Nullable ChunkPosition zero, String... patternString) {
        this.zero = zero;
        pattern = new char[patternString.length][];
        for (int i = 0; i < patternString.length; i++) {
            pattern[i] = patternString[i].replaceAll(" ", "").toCharArray();
        }
        if (zero != null) computeChunks(); // Compute the chunks if the zero is not null
    }

    public char[][] pattern() {
        return pattern;
    }

    public Map<Character, List<ChunkPosition>> chunks() {
        return chunks;
    }

    public ChunkPosition zero() {
        return zero;
    }

    public void setZero(ChunkPosition zero) {
        this.zero = zero;
        computeChunks();
    }

    public void computeChunks() {
        chunks.clear();
        int offsetX = 0, offsetZ = 0;
        int zeroX = 0, zeroZ = 0;
        if (zero != null) {
            offsetX = zero.x();
            offsetZ = zero.z();
        }

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
                int chunkX = offsetX + x - zeroX;
                int chunkZ = offsetX + z - zeroZ;
                chunks.computeIfAbsent(pattern[z][x], k -> new ArrayList<>()).add(new ChunkPosition(chunkX, chunkZ));
            }
        }
    }

    public List<ChunkPosition> getChunks(char patternId) {
        return chunks.getOrDefault(patternId, List.of());
    }
}