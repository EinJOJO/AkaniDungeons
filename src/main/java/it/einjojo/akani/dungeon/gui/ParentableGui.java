package it.einjojo.akani.dungeon.gui;

import it.einjojo.akani.util.inventory.Gui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

public interface ParentableGui {

    @Nullable
    Gui getParent();

    void setParent(@Nullable Gui parent);

    /**
     * Opens the parent gui if it is not null.
     */
    default void openParent() {
        Gui parent = getParent();
        if (parent != null) {
            parent.open();
        }
    }

    default void openParent(InventoryClickEvent ignored) {
        openParent();
    }

}
