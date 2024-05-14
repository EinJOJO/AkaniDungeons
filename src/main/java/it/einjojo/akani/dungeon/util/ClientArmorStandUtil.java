package it.einjojo.akani.dungeon.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientArmorStandUtil {

    public static void spawn(Player player, int entityId, org.bukkit.Location location) {
        WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(
                entityId,
                Optional.of(UUID.randomUUID()),
                EntityTypes.ARMOR_STAND,
                SpigotConversionUtil.fromBukkitLocation(location).getPosition(),
                location.getPitch(),
                location.getYaw(),
                location.getYaw(),
                0,
                Optional.of(Vector3d.zero())
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnPacket);
    }

    public static void setName(Player player, int entityId, @Nullable Component displayName) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityMetadata(entityId, List.of(
                new EntityData(3, EntityDataTypes.BOOLEAN, displayName != null), // render name
                new EntityData(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, Optional.ofNullable(displayName)) // name
        )));
    }

    public static void despawn(Player player, int entityId) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDestroyEntities(entityId));
    }

    public static void updateEntityData(Player player, int entityId) {
        List<EntityData> data = new ArrayList<>(List.of(
                new EntityData(0, EntityDataTypes.BYTE, (byte) (0x20)), // invisible
                new EntityData(5, EntityDataTypes.BOOLEAN, true) // No gravity

        ));
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityMetadata(entityId, data));
    }
}
