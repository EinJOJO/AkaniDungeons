package it.einjojo.akani.dungeon.command;

import it.einjojo.akani.util.commands.BaseCommand;
import it.einjojo.akani.util.commands.CommandHelp;
import it.einjojo.akani.util.commands.annotation.*;
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
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;

@CommandAlias("lc|lootchest")
@CommandPermission("akani.dungeons.lootchest")
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
    public void scanRegion(Player player, String[] args) {
        if (args.length < 1) {
            sendMessage(player, "§cNutze /lc scan Material1 Material2 §7oder §c/lc scan cancel");
        }
        String arg0 = args[0];
        if (arg0 != null && arg0.equalsIgnoreCase("cancel")) {
            if (currentScanTask != null) {
                currentScanTask.cancel();
                sendMessage(player, "§aScan abgebrochen.");
            }
            return;
        }

        Set<BlockType> scanMaterials = new HashSet<>();
        for (String arg : args) {
            try {
                scanMaterials.add(BukkitAdapter.asBlockType(Material.valueOf(arg.toUpperCase())));
            } catch (IllegalArgumentException ex) {
                sendMessage(player, "§cUngültiges Material: " + arg);
                return;
            }
        }


        if (currentScanTask != null && !currentScanTask.isCancelled()) {
            sendMessage(player, "§cEin Chest-Scan läuft bereits... Brich ab /cancel");
            return;
        }

        //TODO fix
        Region selection;
        try {
            selection = BukkitAdapter.adapt(player).getSelection();
            int blockAmount = selection.getHeight() * selection.getWidth() * selection.getLength();
        } catch (IncompleteRegionException ex) {
            sendMessage(player, "§cKeine Auswahl getroffen.");
            sendMessage(player, "§cErstelle mit Worldedit eine Region.");
            return;
        }

        World world = selection.getWorld();
        org.bukkit.World bukkitWorld = BukkitAdapter.adapt(world);
        if (world == null) {
            sendMessage(player, "§cKeine Welt gefunden.");
            return;
        }


        currentScanTask = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            List<Location> lootBoxes = new ArrayList<>();
            long started = System.currentTimeMillis();
            sendMessage(player, "§eChest-Scan gestartet...");

            BlockVector3 min = selection.getMinimumPoint();
            BlockVector3 max = selection.getMaximumPoint();
            int minX = min.getBlockX();
            int minY = min.getBlockY();
            int minZ = min.getBlockZ();
            int maxX = max.getBlockX();
            int maxY = max.getBlockY();
            int maxZ = max.getBlockZ();

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    for (int y = minY; y <= maxY; y++) {
                        if (scanMaterials.contains(world.getBlock(x, y, z).getBlockType())) {
                            lootBoxes.add(new Location(bukkitWorld, x, y, z));
                        }
                    }
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

    @Subcommand("list-placed")
    @Description("Listet alle platzierten Lootboxen auf.")
    public void listPlacedChests(Player sender, @Default("0") @Single String page) {
        int pageInt;
        try {
            pageInt = Integer.parseInt(page);
        } catch (NumberFormatException ex) {
            sendMessage(sender, "§cUngültige Seitenzahl: " + page);
            return;
        }
        List<Location> locations = new LinkedList<>(lootChestManager().placedLootChestMap().keySet());
        int pageSize = 10;
        int pages = (int) Math.ceil(locations.size() / (double) pageSize);
        if (pageInt < 0 || pageInt >= pages) {
            sendMessage(sender, "§cUngültige Seitenzahl: " + page);
            return;
        }
        sendMessage(sender, "§7Platzierte Lootboxen §8(Seite §e" + (pageInt + 1) + "§8/§e" + pages + "§8)");
        for (int i = pageInt * pageSize; i < (pageInt + 1) * pageSize && i < locations.size(); i++) {
            Location location = locations.get(i);
            Component comp = Component.text("§7%d. - §e%d %d %d".formatted(i, location.getBlockX(), location.getBlockY(), location.getBlockZ())).clickEvent(ClickEvent.runCommand("/minecraft:tp %d %d %s".formatted(location.getBlockX(), location.getBlockY(), location.getBlockZ())));
            sender.sendMessage(comp);
        }
        Component prev = Component.text("§7<<").clickEvent(ClickEvent.runCommand("/lc list-placed %d".formatted(pageInt - 1)));
        Component next = Component.text("§7>>").clickEvent(ClickEvent.runCommand("/lc list-placed %d".formatted(pageInt + 1)));
        if (pageInt > 0) {
            sender.sendMessage(prev);
        }
        if (pageInt < pages - 1) {
            sender.sendMessage(next);
        }
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
        new LootChestOverviewGui(player, lootChestManager()).open();
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
