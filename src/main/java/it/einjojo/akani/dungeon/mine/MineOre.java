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
import net.kyori.adventure.text.format.NamedTextColor;
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
        spawnOreArmorstand(player);
        if (hasDestroyed(player.getUniqueId())) {
            setName(player, Component.text("§c" + type.name()).color(NamedTextColor.RED));
        } else {
            setName(player, Component.text("§a" + type.name()).color(NamedTextColor.GREEN));
            setEquipment(player, true);
        }
    }

    public boolean isViewing(UUID playerUuid) {
        return viewers.contains(playerUuid);
    }

    public boolean hasDestroyed(UUID playerUuid) {
        return playerDestroyMap.containsKey(playerUuid);
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
                    new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(type.icon())),
                    new Equipment(EquipmentSlot.MAIN_HAND, SpigotConversionUtil.fromBukkitItemStack(type.icon())),
                    new Equipment(EquipmentSlot.OFF_HAND, SpigotConversionUtil.fromBukkitItemStack(type.icon()))
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

    public void unrender(Player player) {
        if (!viewers.contains(player.getUniqueId())) {
            return;
        }
        viewers.remove(player.getUniqueId());
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDestroyEntities(entityId));
    }

    public void destroy(Player player) {
        unrender(player);
        player.getInventory().addItem(type.breakRewards(player.getInventory().getItemInMainHand()).toArray(new org.bukkit.inventory.ItemStack[0]));
        playerDestroyMap.put(player.getUniqueId(), System.currentTimeMillis());
    }

}
