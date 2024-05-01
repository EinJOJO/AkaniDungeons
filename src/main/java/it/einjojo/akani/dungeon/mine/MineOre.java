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
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record MineOre(int entityId, Location location, MineOreType type, Set<UUID> viewers,
                      Map<UUID, Long> playerDestroyMap) {


    public void render(Player player) {
        if (viewers.contains(player.getUniqueId())) {
            return;
        }
        //Spawn entity
        viewers.add(player.getUniqueId());
        if (!hasDestroyed(player)) {
            spawnOreArmorstand(player);
            return;
        }
    }

    public boolean hasDestroyed(Player player) {
        return playerDestroyMap.containsKey(player.getUniqueId());
    }

    private void spawnOreArmorstand(Player player) {
        WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(
                entityId,
                Optional.of(UUID.randomUUID()),
                EntityTypes.ARMOR_STAND,
                SpigotConversionUtil.fromBukkitLocation(location).getPosition(),
                15f,
                3f,
                0f,
                0,
                Optional.of(Vector3d.zero())
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnPacket);
        System.out.println("Spawned armor stand");
    }

    public void setMetadata(Player player, @Nullable Component displayName) {
        List<EntityData> data = new ArrayList<>(List.of(
                new EntityData(0, EntityDataTypes.BYTE, (0x20)), // invisible
                new EntityData(5, EntityDataTypes.BOOLEAN, true) // No gravity
        ));
        if (displayName == null) {
            data.add(new EntityData(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, Optional.empty())); // name
            data.add(new EntityData(3, EntityDataTypes.BOOLEAN, false)); // is name visible
        } else {
            data.add(new EntityData(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, Optional.of(displayName))); // name
            data.add(new EntityData(3, EntityDataTypes.BOOLEAN, true)); // is name visible
        }
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityMetadata(entityId, data));

    }

    public void equipArmorStandWithMineBlock(Player player) {
        List<Equipment> equipmentList = List.of(
                new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(type.icon()))
        );
        WrapperPlayServerEntityEquipment entityEquipmentPacket = new WrapperPlayServerEntityEquipment(entityId, equipmentList);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityEquipmentPacket);
    }

    public void unrender(Player player) {
        if (!viewers.contains(player.getUniqueId())) {
            return;
        }
        viewers.remove(player.getUniqueId());
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDestroyEntities(entityId));
    }

    public void destroy(Player player) {
        unrender(player);
        playerDestroyMap.put(player.getUniqueId(), System.currentTimeMillis());
    }

}
