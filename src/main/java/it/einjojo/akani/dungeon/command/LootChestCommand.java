package it.einjojo.akani.dungeon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockType;
import it.einjojo.akani.dungeon.AkaniDungeonPlugin;
import it.einjojo.akani.dungeon.gui.lootchest.LootChestOverviewGui;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import it.einjojo.akani.dungeon.lootchest.LootChestManager;
import it.einjojo.akani.dungeon.lootchest.PlacedLootChestFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

@CommandAlias("lc|lootchest")
public class LootChestCommand extends BaseCommand {
    private BukkitTask currentScanTask;
    private List<Location> lastScanResult;
    @Dependency
    private AkaniDungeonPlugin plugin;
    private Component lcPrefix;

    @Subcommand("scan")
    @Description("Scannt eine World-Region nach Lootboxen.")
    @CommandCompletion("[material]|cancel")
    @Syntax("[material]|cancel")
    public void scanRegion(Player player, @Single @Optional String arg) {
        if (arg != null && arg.equalsIgnoreCase("cancel")) {
            if (currentScanTask != null) {
                currentScanTask.cancel();
                sendMessage(player, "§aScan abgebrochen.");
            }
            return;
        }
        Material scanMaterial = Material.BEACON;
        if (arg != null) {
            try {
                scanMaterial = Material.valueOf(arg.toUpperCase());
            } catch (IllegalArgumentException ex) {
                sendMessage(player, "§cUngültiges Material: " + arg);
                return;
            }
        }

        if (currentScanTask != null && !currentScanTask.isCancelled()) {
            sendMessage(player, "§cEin Chest-Scan läuft bereits...");
            return;
        }
        Region selection;
        try {
            selection = BukkitAdapter.adapt(player).getSelection();
        } catch (IncompleteRegionException ex) {
            sendMessage(player, "§cKeine Auswahl getroffen.");
            sendMessage(player, "§cErstelle mit Worldedit eine Region.");
            return;
        }
        World world = selection.getWorld();
        BlockType searching = BukkitAdapter.asBlockType(scanMaterial);
        currentScanTask = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (world == null) {
                sendMessage(player, "§cKeine Welt gefunden.");
                return;
            }
            List<Location> lootBoxes = new LinkedList<>();
            long started = System.currentTimeMillis();
            sendMessage(player, "§eChest-Scan gestartet...");
            for (BlockVector3 blockVector3 : selection) {
                if (world.getBlock(blockVector3).getBlockType() == searching) {
                    lootBoxes.add(BukkitAdapter.adapt(player.getWorld(), blockVector3));
                }
            }
            lastScanResult = lootBoxes;
            Duration elapsed = Duration.ofMillis(System.currentTimeMillis() - started);
            sendMessage(player, "§aScan abgeschlossen §7in %dm %ds".formatted(elapsed.toMinutesPart(), elapsed.toSecondsPart()));
            sendMessage(player, "§7Gefundene Lootboxen: §a" + lootBoxes.size());
            currentScanTask = null;
        });
    }

    @Subcommand("setup-scan")
    @CommandCompletion("@lootChests")
    @Syntax("<chestName>")
    @Description("Erstellt Lootboxen an den gescannten Positionen.")
    public void onCreateChestsByScan(Player player, @Single String chestName) {
        if (lastScanResult == null || lastScanResult.isEmpty()) {
            scanRegion(player, null);
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                while (currentScanTask != null && !currentScanTask.isCancelled()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignore) {
                    }
                }
                // scan finished
                if (lastScanResult == null || lastScanResult.isEmpty()) {
                    sendMessage(player, "§cKeine Lootboxen gefunden.");
                    return;
                }
                plugin.getServer().getScheduler().runTask(plugin, () -> onCreateChestsByScan(player, chestName)); // run sync
            });
            return;
        }
        LootChest type = plugin.akaniDungeon().lootChestManager().lootChestByName(chestName);
        if (type == null) {
            sendMessage(player, "§cLootChest-Typ §e" + chestName + " §cnicht gefunden.");
            return;
        }
        var plcFactory = new PlacedLootChestFactory();
        for (Location location : lastScanResult) {
            lootChestManager().persistPlacedChest(plcFactory.createSimplePlacedLootChest(type, location));
        }
        sendMessage(player, "§aStandard-Lootboxen erstellt.");
    }

    @Subcommand("create-type")
    @Syntax("<name>")
    @CommandCompletion("<name>")
    public void createType(Player sender, @Single String name) {
        if (lootChestManager().lootChestByName(name) != null) {
            sendMessage(sender, "§cLootChest-Typ §e" + name + " §cexistiert bereits.");
            return;
        }
        lootChestManager().persistLootChest(lootChestManager().createLootChest(name));
        sendMessage(sender, "§aLootChest-Typ §e" + name + " §aerstellt.");
        sendMessage(sender, "§7Nutze §e/lc gui §7um die Lootboxen zu verwalten.");
        sendMessage(sender, "§7Nutze §e/lc setup-scan §7um die Lootboxen in der Welt zu erstellen.");
    }

    @Subcommand("gui")
    public void openGui(Player player) {
        LootChestOverviewGui.inventory(lootChestManager()).open(player);
    }

    private LootChestManager lootChestManager() {
        return plugin.akaniDungeon().lootChestManager();
    }

    private void sendMessage(CommandSender receiver, String message) {
        receiver.sendMessage(prefix().append(Component.text(message)));
    }

    @Subcommand("reload")
    @Description("Lädt die Lootboxen neu.")
    public void reload(CommandSender sender) {
        lootChestManager().load();
        sendMessage(sender, "§aLootboxen neu geladen.");
    }

    @Subcommand("save")
    @Description("Speichert die Lootboxen manuell.")
    public void save(CommandSender sender) {
        lootChestManager().save();
        sendMessage(sender, "§aLootboxen gespeichert.");
    }

    @HelpCommand
    @CatchUnknown
    @Default
    public void help(CommandHelp help) {
        help.showHelp();
    }

    private Component prefix() {
        if (lcPrefix == null) {
            lcPrefix = plugin.akaniDungeon().core().miniMessage().deserialize("<gray>[<b><gradient:#FF512F:#F09819>LootChest</gradient></b>] ");
        }
        return lcPrefix;
    }

}
