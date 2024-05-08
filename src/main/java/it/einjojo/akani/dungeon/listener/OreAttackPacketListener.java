package it.einjojo.akani.dungeon.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import it.einjojo.akani.dungeon.mine.MineManager;
import it.einjojo.akani.dungeon.mine.MineOre;
import it.einjojo.akani.dungeon.mine.MineProgression;
import it.einjojo.akani.dungeon.mine.tool.Tool;
import it.einjojo.akani.dungeon.mine.tool.ToolFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OreAttackPacketListener extends PacketListenerAbstract {

    private final MineManager mineManager;
    private final ToolFactory toolFactory;
    private final JavaPlugin plugin;


    public OreAttackPacketListener(MineManager mineManager, ToolFactory toolFactory, JavaPlugin plugin) {
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
        MineOre mineOre = mineManager.oreByEntityId(entityID);
        if (mineOre == null) {
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
            player.sendMessage("§7Type: §c" + mineOre.type().name());
            player.sendMessage("§7Destroyed: ");
            for (Map.Entry<UUID, Long> entry : mineOre.playerDestroyMap().entrySet()) {
                player.sendMessage("§7 - " + entry.getKey() + " : §c" + entry.getValue());
            }
            return;
        }
        if (mineOre.hasDestroyed(player.getUniqueId())) {
            denialAction(player, mineOre, Component.text("§cYou already destroyed this!"));
            return;
        }
        Tool tool = toolFactory.fromItemStack(itemStack);
        if (tool.type() == null || !mineOre.type().canBreak(itemStack)) {
            denialAction(player, mineOre, Component.text("§cYou can't break this with that tool!"));
            return;
        }

        if (!progression.progress(player, mineOre, tool.damage())) return;
        WrapperPlayServerEntityAnimation swingAnimation = new WrapperPlayServerEntityAnimation(player.getEntityId(), WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM);
        event.getUser().sendPacket(swingAnimation);
        if (progression.isComplete()) {
            List<ItemStack> rewards = mineOre.type().breakRewards(itemStack);
            TextComponent.Builder actionbarMessage = Component.text();
            for (ItemStack reward : rewards) {
                actionbarMessage.append(Component.text("§7+§a%s §7x §a%s".formatted(reward.getAmount(), reward.getType().name())));
                actionbarMessage.appendSpace();
                player.getInventory().addItem(reward);
            }
            player.sendActionBar(actionbarMessage.build());
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                mineOre.destroy(player);
            }, 20);
        }
    }

    protected void denialAction(Player player, MineOre mineOre, Component actionBarMessage) {
        player.spawnParticle(Particle.ASH, mineOre.location().clone().add(0, 0.3f, 0), 3);
        player.sendActionBar(actionBarMessage);
        player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.3f, 1);
    }

}
