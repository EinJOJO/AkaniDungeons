package it.einjojo.akani.dungeon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import it.einjojo.akani.dungeon.AkaniDungeonPlugin;
import it.einjojo.akani.dungeon.config.MobSpawnerConfig;
import it.einjojo.akani.dungeon.util.ChunkPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("chunkmobs|mobs")
public class ChunkMobsCommand extends BaseCommand {

    private final MobSpawnerConfig mobSpawnerConfig;
    private Component prefix;

    public ChunkMobsCommand(MobSpawnerConfig mobSpawnerConfig) {
        this.mobSpawnerConfig = mobSpawnerConfig;
    }


    @Subcommand("load")
    public void loadConfig(Player player) {
        mobSpawnerConfig.load();
        sendMessage(player, "§7- §cConfig loaded");
    }

    @Subcommand("save")
    public void saveConfig(Player player) {
        mobSpawnerConfig.save();
        sendMessage(player, "§7- §cConfig saved");
    }

    @Subcommand("biomeinfo")
    public void listBiomeConfig(Player player) {
        sendMessage(player, "§7- §cBiome Configs:");
        for (Biome biome : Biome.values()) {
            sendMessage(player, listBiomeSpawnablesString(biome));
        }
    }

    @Subcommand("biome info")
    public void listBiomeSpawnables(Player player) {
        Biome biome = player.getWorld().getBiome(player.getLocation());
        sendMessage(player, listBiomeSpawnablesString(biome));
    }

    public String listBiomeSpawnablesString(Biome biome) {
        List<String> mobIds = mobSpawnerConfig.mobIds(biome);
        StringBuilder mobString = new StringBuilder();
        mobString.append("§7- §c").append(biome.name()).append(": ");
        for (String mobId : mobIds) {
            mobString.append("§7").append(mobId).append("§8, ");
        }
        return mobString.toString();
    }

    @Subcommand("minMobs")
    @CommandCompletion("@range:0-10")
    public void setMinMobs(Player player, int minMobs) {
        mobSpawnerConfig.setMinMobsPerChunk(minMobs);
        sendMessage(player, "§7- §cMin Mobs set to §7" + minMobs);
    }

    @Subcommand("maxMobs")
    @CommandCompletion("@range:1-10")
    public void setMaxMobs(Player player, int maxMobs) {
        mobSpawnerConfig.setMaxMobsPerChunk(maxMobs);
        sendMessage(player, "§7- §cMax Mobs set to §7" + maxMobs);
    }




    @Subcommand("biome add")
    @CommandCompletion("@mythicmobs")
    public void addMobToBiome(Player player, String mobId) {
        Biome biome = player.getWorld().getBiome(player.getLocation());
        mobSpawnerConfig.addMobId(biome, mobId);
        sendMessage(player, "§7- §cAdded §7" + mobId + "§c to §7" + biome.name());
    }

    @Subcommand("biome remove")
    @CommandCompletion("@mythicmobs")
    public void removeMobFromBiome(Player player, String mobId) {
        Biome biome = player.getWorld().getBiome(player.getLocation());
        mobSpawnerConfig.removeMobId(biome, mobId);
        sendMessage(player, "§7- §cRemoved §7" + mobId + "§c from §7" + biome.name());
    }


    @Subcommand("chunk info")
    public void chunkInfo(Player sender) {
        Chunk chunk = sender.getChunk();
        Biome biome = ChunkPosition.of(chunk).getBiome(sender.getWorld());
        int minMobs = mobSpawnerConfig.minMobsPerChunk();
        int maxMobs = mobSpawnerConfig.maxMobsPerChunk();
        int currentEntityCount = chunk.getEntities().length;
        Double overwrite = mobSpawnerConfig.refillOverwrites().get(ChunkPosition.of(chunk));
        boolean hasOverwrite = overwrite != null;
        List<String> spawnables = mobSpawnerConfig.mobIds(biome);
        sendMessage(sender, "§7- §cChunk: §7" + chunk.getX() + "/" + chunk.getZ());
        sendMessage(sender, "§7- §cBiome: §7" + biome.name());
        sendMessage(sender, "§7- §cMin Mobs: §7" + minMobs);
        sendMessage(sender, "§7- §cMax Mobs: §7" + maxMobs);
        sendMessage(sender, "§7- §cCurrent Mobs: §7" + currentEntityCount);
        sendMessage(sender, "§7- §cPercentage-Overwrite: §7" + (hasOverwrite ? overwrite : "§cNone"));
        StringBuilder spawnableString = new StringBuilder();
        spawnableString.append("§7- §cSpawnables: ");
        for (String spawnable : spawnables) {
            spawnableString.append("§7").append(spawnable).append("§8, ");
        }
        sendMessage(sender, spawnableString.toString());
    }


    private void sendMessage(Player player, String message) {
        player.sendMessage(messagePrefix().append(Component.text(message)));
    }

    private Component messagePrefix() {
        if (prefix == null) {
            prefix = AkaniDungeonPlugin.get().akaniDungeon().core().miniMessage().deserialize("<gray>[<b><gradient:#FF512F:#F09819>Mobs</gradient></b>] ");
        }
        return prefix;
    }


    @Subcommand("chunk overwrite")
    @Description("Overwrite the spawn-percentage")
    @CommandCompletion("@range:0-100")
    public void refillOverwrite(Player player, double percentage) {
        percentage = percentage / 100;
        if (percentage < 0 || percentage > 1) {
            sendMessage(player, "§7- §cPercentage must be between §70§c and §7100");
            return;
        }
        Chunk chunk = player.getChunk();
        mobSpawnerConfig.addRefillOverwrite(ChunkPosition.of(chunk), percentage);
        sendMessage(player, "§7- §cOverwrite set to §7" + percentage + "§c for §7" + chunk.getX() + "/" + chunk.getZ());
    }

    @HelpCommand
    @CatchUnknown
    @Default
    public void help(CommandHelp help) {
        help.showHelp();
    }

}
