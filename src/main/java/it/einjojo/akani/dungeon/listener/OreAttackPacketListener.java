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
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class OreAttackPacketListener extends PacketListenerAbstract {

    private final MineManager mineManager;
    private final JavaPlugin plugin;


    public OreAttackPacketListener(MineManager mineManager, JavaPlugin plugin) {
        super(PacketListenerPriority.NORMAL);
        this.mineManager = mineManager;
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
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!mineOre.type().canBreak(tool)) {
            player.spawnParticle(Particle.ASH, mineOre.location().clone().add(0, 0.3f, 0), 3);
            player.sendActionBar(Component.text("§cDu benötigst ein besseres Werkzeug!"));
            player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.3f, 1);
            return;
        }
        if (!progression.progress(player, mineOre)) return;
        WrapperPlayServerEntityAnimation swingAnimation = new WrapperPlayServerEntityAnimation(player.getEntityId(), WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM);
        event.getUser().sendPacket(swingAnimation);
        if (progression.isComplete()) {
            List<ItemStack> rewards = mineOre.type().breakRewards(tool);
            for (ItemStack reward : rewards) {
                player.getInventory().addItem(reward);
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                mineOre.destroy(player);
            }, 20);
        }

    }

}
