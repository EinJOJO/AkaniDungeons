package it.einjojo.akani.dungeon;

import it.einjojo.akani.core.api.message.MessageProvider;
import it.einjojo.akani.core.api.message.MessageStorage;

public class DungeonMessageProvider implements MessageProvider {
    private static final int VERSION = 0;

    @Override
    public String providerName() {
        return "dungeons";
    }

    @Override
    public boolean shouldInsert(MessageStorage messageStorage) {
        return messageStorage.isRegistered("de", "dungeons.version." + VERSION);
    }

    @Override
    public void insertMessages(MessageStorage ms) {
        ms.registerMessage("de", "dungeons.version." + VERSION, String.valueOf(System.currentTimeMillis()));
        // mine
        ms.registerMessage("de", "dungeons.mine.invalid-tool", "<red>Du kannst diesen Block nicht mit diesem Werkzeug abbauen.");
        ms.registerMessage("de", "dungeons.mine.invalid-block", "<red>Du kannst diesen Block nicht abbauen.");


    }
}
