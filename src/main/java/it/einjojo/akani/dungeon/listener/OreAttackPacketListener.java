package it.einjojo.akani.dungeon.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import it.einjojo.akani.dungeon.AkaniDungeonPlugin;
import it.einjojo.akani.dungeon.event.AsyncPlayerMineEvent;
import it.einjojo.akani.dungeon.event.AsyncPlayerPreMineProgressEvent;
import it.einjojo.akani.dungeon.mine.MineManager;
import it.einjojo.akani.dungeon.mine.MineProgression;
import it.einjojo.akani.dungeon.mine.PlacedOre;
import it.einjojo.akani.dungeon.mine.tool.Tool;
import it.einjojo.akani.dungeon.mine.tool.ToolFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OreAttackPacketListener extends PacketListenerAbstract {

    private final MineManager mineManager;
    private final ToolFactory toolFactory;
    private final AkaniDungeonPlugin plugin;


    public OreAttackPacketListener(MineManager mineManager, ToolFactory toolFactory, AkaniDungeonPlugin plugin) {
        super(PacketListenerPriority.NORMAL);
        this.mineManager = mineManager;
        this.toolFactory = toolFactory;
        this.plugin = plugin;
        PacketEvents.getAPI().getEventManager().registerListener(this);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) {
            return;
        }
        WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
        int entityID = wrapper.getEntityId();
        PlacedOre placedOre = mineManager.oreByEntityId(entityID);
        if (placedOre == null) {
            return;
        }
        MineProgression progression = mineManager.progressionByPlayer(event.getUser().getUUID());
        if (progression == null) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType().equals(Material.DEBUG_STICK)) {
            printDebug(player, placedOre);
            return;
        }
        if (itemStack.getType().equals(Material.COMMAND_BLOCK)) {
            deletePlaced(player, placedOre);
            return;
        }
        if (placedOre.hasDestroyed(player.getUniqueId())) {
            denialAction(player, placedOre, Component.text("§cYou already destroyed this!"));
            return;
        }
        Tool tool = toolFactory.fromItemStack(itemStack);
        if (tool.type() == null || !placedOre.type().canBreak(itemStack)) {
            denialAction(player, placedOre, Component.text("§cYou can't break this with that tool!"));
            return;
        }
        var preMineProgressionEvent = new AsyncPlayerPreMineProgressEvent(player, placedOre, progression, tool.damage());
        if (!preMineProgressionEvent.callEvent()) {
            return;
        }
        if (!progression.progress(player, placedOre, preMineProgressionEvent.getProgress())) return;
        WrapperPlayServerEntityAnimation swingAnimation = new WrapperPlayServerEntityAnimation(player.getEntityId(), WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM);
        event.getUser().sendPacket(swingAnimation);
        if (progression.isComplete()) {
            List<ItemStack> rewards = placedOre.type().breakRewards(itemStack);
            AsyncPlayerMineEvent asyncPlayerMineEvent = new AsyncPlayerMineEvent(player, placedOre, rewards);
            if (asyncPlayerMineEvent.callEvent() && asyncPlayerMineEvent.getDrops() != null) {
                TextComponent.Builder actionbarMessage = Component.text();
                for (ItemStack reward : asyncPlayerMineEvent.getDrops()) {
                    actionbarMessage.append(Component.text("§7+§a%s §7x §a%s".formatted(reward.getAmount(), reward.getType().name())));
                    actionbarMessage.appendSpace();
                    player.getInventory().addItem(reward);
                }
                player.sendActionBar(actionbarMessage.build());
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                placedOre.destroy(player);
            }, 20);
        }
    }

    private void printDebug(Player player, PlacedOre placedOre) {
        player.sendMessage("§7Type: §c" + placedOre.type().name());
        player.sendMessage("§7Destroyed: ");
        for (Map.Entry<UUID, Long> entry : placedOre.playerDestroyMap().entrySet()) {
            player.sendMessage("§7 - " + entry.getKey() + " : §c" + entry.getValue());
        }
    }

    private void deletePlaced(Player player, PlacedOre ore) {
        mineManager.storage().deletePlacedOre(ore);
        mineManager.unregisterPlacedOre(ore);
        player.sendMessage("§cOre deleted.");
    }


    protected void denialAction(Player player, PlacedOre placedOre, Component actionBarMessage) {
        player.spawnParticle(Particle.ASH, placedOre.location().clone().add(0, 0.3f, 0), 3);
        player.sendActionBar(actionBarMessage);
        player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.3f, 1);
    }


}
