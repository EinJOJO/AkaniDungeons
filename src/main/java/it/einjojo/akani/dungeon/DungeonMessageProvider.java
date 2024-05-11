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
    public void insertMessages(MessageStorage ms) {
        ms.registerMessage("de", "mine.action.invalid-tool", "<red>Du kannst diesen Block nicht mit diesem Werkzeug abbauen.");
        ms.registerMessage("de", "mine.action.invalid-block", "<red>Du kannst diesen Block nicht abbauen.");

    }
}
