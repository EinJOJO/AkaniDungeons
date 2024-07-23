package it.einjojo.akani.dungeon.gui.mineoretype;

import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.config.MineOreTypeConfig;
import it.einjojo.akani.dungeon.gui.GUIItem;
import it.einjojo.akani.dungeon.gui.PaginatedGui;
import it.einjojo.akani.dungeon.mine.MineOreType;
import it.einjojo.akani.dungeon.mine.factory.MineOreTypeFactory;
import it.einjojo.akani.util.inventory.Gui;
import it.einjojo.akani.util.inventory.Icon;
import it.einjojo.akani.util.inventory.pagination.PaginationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.2.0
 * This GUI is the overview of all mine ore types.
 * It allows the player to select a mine ore type to edit or to get a spawn egg.
 */
public class MineOreTypeSelectorGUI extends PaginatedGui {
    private static final List<Component> CLICK_ACTION = List.of(
            Component.empty(),
            Component.text("§7[§cLinksklick§7] Einstellungen"),
            Component.text("§7[§cRechtsklick§7] Spawn-Ei"),
            Component.empty()
    );
    private final MineOreTypeConfig config;
    private final MineOreTypeFactory factory;


    public MineOreTypeSelectorGUI(Player player, MineOreTypeConfig config, MineOreTypeFactory factory) {
        super(player, "oretype_selector", Component.text("Erze", NamedTextColor.GRAY), 6);
        this.config = config;
        this.factory = factory;
        paginationManager.registerPageSlotsBetween(9, 9 * 5 - 1);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillRow(GUIItem.BACKGROUND.icon(), 0);
        fillRow(GUIItem.BACKGROUND.icon(), 5);
        addPaginationItems();
        paginationManager.getItems().clear();
        for (int i = 0; i < config.types().size(); i++) {
            addOreType(config.types().get(i));
        }
        paginationManager.update();
        addItem(5 * 9 + 4, GUIItem.ADD_BUTTON.icon().onClick(this::handleAddClick));
    }


    private void handleAddClick(InventoryClickEvent e) {
        ItemStack cursorItem = e.getCursor();
        if (cursorItem.getType().isAir()) {
            player.sendMessage(Component.text("§aHalte den Block, der zusehen sein soll, in der Hand und gib ihm einen Namen."));
            player.sendMessage(Component.text("§aVerwende /amine create <name> um eine neue Erz-Kategorie zu erstellen."));
            player.closeInventory();
            return;
        }
        ItemStack icon = cursorItem.clone();
        icon.setAmount(1);
        MineOreType oreType = factory.createMineOreType(icon);
        config.addOreType(oreType);
        player.getInventory().addItem(cursorItem);
        player.setItemOnCursor(null);
        addOreType(oreType);
    }

    public void addOreType(MineOreType oreType) {
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.addAll(oreType.description());
        lore.addAll(CLICK_ACTION);
        Icon icon = new Icon(new ItemBuilder(oreType.icon()).lore(lore).build()).onClick(e -> {
            if (e.isLeftClick()) {
                openMineOreSettingsGui(oreType);
            } else {
                player.getInventory().addItem(oreType.spawnEggItemStack());
            }
        });
        paginationManager.addItem(icon);
    }

    private void openMineOreSettingsGui(MineOreType oreType) {
        var gui = new MineOreTypeSettingGUI(player, oreType);
        gui.setParent(this);
        gui.open();
    }

}
