package it.einjojo.akani.dungeon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockType;
import it.einjojo.akani.dungeon.AkaniDungeonPlugin;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import it.einjojo.akani.dungeon.lootchest.PlacedLootChestFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

@CommandAlias("lc|lootchest")
public class LootChestCommand extends BaseCommand {
    private static final LootChest TEST_CHEST = new LootChest("test", Duration.ofSeconds(30), 3, Component.text("test"), new LinkedList<>());
    private BukkitTask currentScanTask;
    private List<Location> lastScanResult;
    @Dependency
    private AkaniDungeonPlugin plugin;

    @Subcommand("scan-region")
    @CommandCompletion("[material]|cancel")
    public void scanRegion(Player player, @Single @Optional String arg) {
        if (arg != null && arg.equalsIgnoreCase("cancel")) {
            if (currentScanTask != null) {
                currentScanTask.cancel();
                player.sendMessage("Scan abgebrochen.");
            }
            return;
        }
        Material scanMaterial = Material.BEACON;
        if (arg != null) {
            try {
                scanMaterial = Material.valueOf(arg.toUpperCase());
            } catch (IllegalArgumentException ex) {
                player.sendMessage("Ungültiges Material: " + arg);
                return;
            }
        }

        if (currentScanTask != null && !currentScanTask.isCancelled()) {
            player.sendMessage("Ein Scan läuft bereits.");
            return;
        }
        Region selection;
        try {
            selection = BukkitAdapter.adapt(player).getSelection();
        } catch (IncompleteRegionException ex) {
            player.sendMessage("Keine Auswahl gefunden. Mach mit Worldedit.");
            return;
        }
        World world = selection.getWorld();
        BlockType searching = BukkitAdapter.asBlockType(scanMaterial);
        currentScanTask = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (world == null) {
                player.sendMessage("Keine Welt gefunden.");
                return;
            }
            List<Location> lootBoxes = new LinkedList<>();
            long started = System.currentTimeMillis();
            player.sendMessage("Scan gestartet...");
            for (BlockVector3 blockVector3 : selection) {
                if (world.getBlock(blockVector3).getBlockType() == searching) {
                    lootBoxes.add(BukkitAdapter.adapt(player.getWorld(), blockVector3));
                }
            }
            lastScanResult = lootBoxes;
            Duration elapsed = Duration.ofMillis(System.currentTimeMillis() - started);
            player.sendMessage("Scan abgeschlossen in %dm %ds".formatted(elapsed.toMinutesPart(), elapsed.toSecondsPart()));
            player.sendMessage("Gefundene Lootboxen: " + lootBoxes.size());
            currentScanTask = null;
        });
    }

    @Subcommand("setup-scan")
    public void onCreateChestsByScan(Player player) {
        if (lastScanResult == null) {
            scanRegion(player, null);
            return;
        }
        var plcFactory = new PlacedLootChestFactory();
        for (Location location : lastScanResult) {
            plugin.akaniDungeon().lootChestManager().persist(plcFactory.createSimplePlacedLootChest(TEST_CHEST, location));
        }
        player.sendMessage("Lootboxen erstellt.");
    }


}
