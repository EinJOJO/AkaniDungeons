package it.einjojo.akani.dungeon.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import it.einjojo.akani.dungeon.mine.MineManager;
import it.einjojo.akani.dungeon.mine.MineOre;
import it.einjojo.akani.dungeon.mine.MineProgression;

public class OreAttackPacketListener extends PacketListenerAbstract {

    private final MineManager mineManager;

    public OreAttackPacketListener(MineManager mineManager) {
        super(PacketListenerPriority.NORMAL);
        this.mineManager = mineManager;
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
        progression.progress(mineOre);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            var equipment = new WrapperPlayServerEntityEquipment(event);
            return;
        }
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            var packet = new WrapperPlayServerSpawnLivingEntity(event);
            return;
        }
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
            var packet = new WrapperPlayServerSpawnEntity(event);
            return;
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            var packet = new WrapperPlayServerEntityMetadata(event);

            return;
        }

    }
}
