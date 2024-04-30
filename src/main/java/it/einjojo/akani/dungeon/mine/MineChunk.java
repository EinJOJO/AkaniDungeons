package it.einjojo.akani.dungeon.mine;

import it.einjojo.akani.dungeon.util.ChunkPosition;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.List;

public record MineChunk(ChunkPosition position, List<MineOre> ores) {


    public int x() {
        return position.x();
    }

    public int z() {
        return position.z();
    }

    public Chunk toChunk(World world) {
        return position.toChunk(world);
    }
}
