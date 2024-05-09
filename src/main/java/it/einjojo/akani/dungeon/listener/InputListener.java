package it.einjojo.akani.dungeon.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import it.einjojo.akani.dungeon.input.BlockSelectionInput;
import it.einjojo.akani.dungeon.input.DropItemInput;
import it.einjojo.akani.dungeon.input.Input;
import it.einjojo.akani.dungeon.input.PlayerChatInput;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InputListener implements Listener {
    private static final Map<UUID, Input<?>> inputSessions = new HashMap<>();

    public InputListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void register(Input<?> input) {
        if (inputSessions.containsKey(input.playerUniqueId())) {
            throw new IllegalStateException("Player already has an active input session.");
        }
        inputSessions.put(input.playerUniqueId(), input);
    }

    public static void unregister(UUID player) {
        inputSessions.remove(player);
    }

    public static boolean hasInputSession(UUID player) {
        return inputSessions.containsKey(player);
    }

    public static Input<?> getInputSession(UUID player) {
        return inputSessions.get(player);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatMessage(AsyncChatEvent event) {
        Input<?> input = getInputSession(event.getPlayer().getUniqueId());
        if (input == null) {
            return;
        }
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        if (plainMessage.equalsIgnoreCase("cancel")) {
            event.setCancelled(true);
            input.unregister();
            input.cancel();
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 5);
            return;
        }
        if (input instanceof PlayerChatInput pci) {
            event.setCancelled(true);
            pci.unregister();
            pci.callback().accept(plainMessage);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 10);
        }
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent event) {
        Input<?> input = getInputSession(event.getPlayer().getUniqueId());
        if (input == null) {
            return;
        }
        if (!(input instanceof DropItemInput dii)) return;
        event.setCancelled(true);
        dii.unregister();
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 10);
        dii.callback().accept(event.getItemDrop().getItemStack());
    }

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent event) {
        Input<?> input = getInputSession(event.getPlayer().getUniqueId());
        if (input == null) {
            return;
        }
        if (!(input instanceof BlockSelectionInput bsi)) return;
        event.setCancelled(true);
        bsi.unregister();
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 10);
        bsi.callback().accept(event.getBlock());
    }


    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Input<?> input = getInputSession(event.getPlayer().getUniqueId());
        if (input == null) {
            return;
        }
        input.cancel();
        input.unregister();
    }

}
