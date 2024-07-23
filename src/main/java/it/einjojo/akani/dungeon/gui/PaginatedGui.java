package it.einjojo.akani.dungeon.gui;

import it.einjojo.akani.util.inventory.Gui;
import it.einjojo.akani.util.inventory.pagination.PaginationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PaginatedGui extends Gui {

    protected final PaginationManager paginationManager = new PaginationManager(this);

    public PaginatedGui(@NotNull Player player, @NotNull String id, Component title, int rows) {
        super(player, id, title, rows);
    }

    protected void addPaginationItems() {
        addItem(9 * 5 + 3, GUIItem.LEFT_SKULL.icon().setName("§cVorherige Seite").onClick(e -> {
            paginationManager.goPreviousPage();
            paginationManager.update();
        }));
        addItem(9 * 5 + 5,GUIItem.RIGHT_SKULL.icon().setName("§cNächste Seite").onClick(e -> {
            paginationManager.goNextPage();
            paginationManager.update();
        }));
    }
}
