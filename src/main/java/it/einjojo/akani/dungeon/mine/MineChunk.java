package it.einjojo.akani.dungeon.mine;

import it.einjojo.akani.dungeon.util.ChunkPosition;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public record MineChunk(ChunkPosition position, List<PlacedOre> ores) {


    public void renderOres(Player player) {
        boolean inDebugMode = player.getInventory().getItemInMainHand().getType().equals(Material.DEBUG_STICK);
        ores.forEach(ore -> ore.render(player, inDebugMode));
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
