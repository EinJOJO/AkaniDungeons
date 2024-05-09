package it.einjojo.akani.dungeon;

import it.einjojo.akani.core.api.message.MessageProvider;
import it.einjojo.akani.core.api.message.MessageStorage;

public class DungeonMessageProvider implements MessageProvider {
    @Override
    public String providerName() {
        return "dungeons";
    }

    @Override
    public boolean shouldInsert(MessageStorage messageStorage) {
        return messageStorage.isRegistered("de", "");
    }

    @Override
    public void insertMessages(MessageStorage messageStorage) {

    }
}
