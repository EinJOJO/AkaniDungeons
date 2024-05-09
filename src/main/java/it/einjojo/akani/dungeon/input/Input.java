package it.einjojo.akani.dungeon.input;

import it.einjojo.akani.dungeon.listener.InputListener;

import java.util.UUID;
import java.util.function.Consumer;

public interface Input<T> {
    UUID playerUniqueId();

    Consumer<T> callback();

    default void register() {
        InputListener.register(this);
    }

    default void unregister() {
        InputListener.unregister(playerUniqueId());
    }

    void cancel();
}
