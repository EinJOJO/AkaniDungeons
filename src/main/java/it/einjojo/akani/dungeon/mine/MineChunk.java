package it.einjojo.akani.dungeon.mine;

import it.einjojo.akani.dungeon.util.ChunkPosition;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public record MineChunk(ChunkPosition position, List<MineOre> ores) {


    public void renderOres(Player player) {
        ores.forEach(ore -> ore.render(player));
    }

    public void unrenderOres(Player player) {
        ores.forEach(ore -> ore.unrender(player));
    }

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
