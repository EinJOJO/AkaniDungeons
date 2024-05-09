package it.einjojo.akani.dungeon.mine;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
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

public record PlacedOre(int entityId, Location location, MineOreType type, Set<UUID> viewers,
                        Map<UUID, Long> playerDestroyMap) {


    public void render(Player player, boolean ignoreDestroy) {
        if (viewers.contains(player.getUniqueId())) {
            return;
        }
        if (ignoreDestroy || !hasDestroyed(player.getUniqueId())) {
            viewers.add(player.getUniqueId());
            spawnOreArmorstand(player);
            setEquipment(player, true);
        }
    }

    public void render(Player player) {
        render(player, false);
    }

    public boolean isViewing(UUID playerUuid) {
        return viewers.contains(playerUuid);
    }

    public boolean hasDestroyed(UUID playerUuid) {
        Long time = playerDestroyMap.get(playerUuid);
        if (time == null) {
            return false;
        }
        boolean destroyed = System.currentTimeMillis() - time < type.respawnTime().toMillis();
        if (!destroyed) {
            playerDestroyMap.remove(playerUuid);
        }
        return destroyed;
    }

    private void spawnOreArmorstand(Player player) {
        WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(
                entityId,
                Optional.of(UUID.randomUUID()),
                EntityTypes.ARMOR_STAND,
                SpigotConversionUtil.fromBukkitLocation(location).getPosition(),
                location().getPitch(),
                location.getYaw(),
                location.getYaw(),
                0,
                Optional.of(Vector3d.zero())
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnPacket);
        List<EntityData> data = new ArrayList<>(List.of(
                new EntityData(0, EntityDataTypes.BYTE, (byte) (0x20)), // invisible
                new EntityData(5, EntityDataTypes.BOOLEAN, true), // No gravity

                new EntityData(16, EntityDataTypes.ROTATION, new Vector3f(51f, 205f, 40f)), // head
                new EntityData(18, EntityDataTypes.ROTATION, new Vector3f(116f, 40f, 25f)), //left arm
                new EntityData(19, EntityDataTypes.ROTATION, new Vector3f(112f, 209f, 87f)) // right arm

        ));
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityMetadata(entityId, data));
    }


    public void setName(Player player, @Nullable Component displayName) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityMetadata(entityId, List.of(
                new EntityData(3, EntityDataTypes.BOOLEAN, displayName != null), // render name
                new EntityData(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, Optional.ofNullable(displayName)) // name
        )));
    }

    public void setEquipment(Player player, boolean showBlocks) {
        List<Equipment> equipmentList;
        if (showBlocks) {
            equipmentList = List.of(
                    new Equipment(EquipmentSlot.HELMET, type.protocolIcon()),
                    new Equipment(EquipmentSlot.MAIN_HAND, type.protocolIcon()),
                    new Equipment(EquipmentSlot.OFF_HAND, type.protocolIcon())
            );
        } else {
            equipmentList = List.of(
                    new Equipment(EquipmentSlot.HELMET, ItemStack.EMPTY),
                    new Equipment(EquipmentSlot.MAIN_HAND, ItemStack.EMPTY),
                    new Equipment(EquipmentSlot.OFF_HAND, ItemStack.EMPTY)
            );
        }
        WrapperPlayServerEntityEquipment entityEquipmentPacket = new WrapperPlayServerEntityEquipment(entityId, equipmentList);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityEquipmentPacket);

    }

    /**
     * Unrender the ore for the player
     *
     * @param player the player
     */
    public void unrender(Player player) {
        if (viewers.contains(player.getUniqueId())) {
            viewers.remove(player.getUniqueId());
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDestroyEntities(entityId));
        }
    }

    /**
     * Destroy the ore for the player and set the time when it was destroyed
     *
     * @param player the player
     */
    public void destroy(Player player) {
        unrender(player);
        playerDestroyMap.put(player.getUniqueId(), System.currentTimeMillis());
    }

}
