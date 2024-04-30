package it.einjojo.akani.dungeon.mine;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record MineOre(int entityId, Location location, MineOreType type, Set<UUID> viewers,
                      Map<UUID, Long> playerDestroyMap) {


    public void render(Player player) {
        if (!viewers.contains(player.getUniqueId())) {
            return;
        }
        //Spawn entity
        viewers.add(player.getUniqueId());
        if (hasDestroyed(player)) {
            return;
        } else {
            spawnOreArmorstand(player);
        }

    }

    public boolean hasDestroyed(Player player) {
        return playerDestroyMap.containsKey(player.getUniqueId());
    }

    private void spawnOreArmorstand(Player player) {
        WrapperPlayServerSpawnLivingEntity spawnPacket = new WrapperPlayServerSpawnLivingEntity(
                entityId,
                UUID.randomUUID(),
                EntityTypes.ARMOR_STAND,
                SpigotConversionUtil.fromBukkitLocation(location).getPosition(),
                0f,
                0f,
                0f,
                Vector3d.zero(),
                List.of(
                        new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20), // Invisible
                        new EntityData(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, Component.text("Erz").color(NamedTextColor.GOLD)), // Custom name
                        new EntityData(3, EntityDataTypes.BOOLEAN, true), // Custom name visible
                        new EntityData(5, EntityDataTypes.BOOLEAN, true) // No gravity
                )
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnPacket);
        List<Equipment> equipmentList = List.of(
                new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(type.icon()))
        );
        WrapperPlayServerEntityEquipment entityEquipmentPacket = new WrapperPlayServerEntityEquipment(entityId, equipmentList);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityEquipmentPacket);
    }

    public void unrender(Player player) {
        viewers.remove(player.getUniqueId());
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDestroyEntities(entityId));
    }

    public void destroy(Player player) {
        unrender(player);
        playerDestroyMap.put(player.getUniqueId(), System.currentTimeMillis());
    }

}
