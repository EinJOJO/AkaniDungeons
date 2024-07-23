package it.einjojo.akani.dungeon.storage;

import java.text.MessageFormat;

public class StorageException extends RuntimeException {

    private final String failedAction;

    public StorageException(String failedAction) {
        super(MessageFormat.format("Failed {0}", failedAction));
        this.failedAction = failedAction;
    }



    public StorageException(String failedAction, Throwable cause) {
        super(MessageFormat.format("Failed {0}", failedAction), cause);
        this.failedAction = failedAction;
    }
}
