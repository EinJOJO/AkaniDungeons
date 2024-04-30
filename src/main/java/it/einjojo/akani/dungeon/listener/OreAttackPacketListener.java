package it.einjojo.akani.dungeon.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
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
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
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
}
