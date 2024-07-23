package it.einjojo.akani.dungeon.gui.lootchest;

import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.gui.GUIItem;
import it.einjojo.akani.dungeon.input.PlayerChatInput;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import it.einjojo.akani.dungeon.lootchest.LootChestManager;
import it.einjojo.akani.util.inventory.Gui;
import it.einjojo.akani.util.inventory.Icon;
import it.einjojo.akani.util.inventory.pagination.PaginationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LootChestOverviewGui extends Gui {
    private final LootChestManager lootChestManager;
    private final PaginationManager paginationManager = new PaginationManager(this);

    public LootChestOverviewGui(Player player, LootChestManager lootChestManager) {
        super(player, "lootChestOverview", Component.text("Lootkisten", NamedTextColor.GOLD), 6);
        this.lootChestManager = lootChestManager;
        paginationManager.registerPageSlotsBetween(9, 9 * 5 - 1);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillRow(GUIItem.BACKGROUND.icon(), 0);
        fillRow(GUIItem.BACKGROUND.icon(), 5);
        addItem(5 * 9 + 4, GUIItem.PLUS_SKULL.icon().setName("§aLootchest erstellen").onClick(this::handleAddClick));
        paginationManager.getItems().clear();
        for (LootChest chestType : lootChestManager.lootChests()) {
            paginationManager.addItem(createLootChestIcon(chestType));
        }
        paginationManager.update();
    }


    private Icon createLootChestIcon(LootChest lootChest) {
        return new Icon(new ItemBuilder(lootChest.guiIconMaterial())
                .displayName(lootChest.displayName())
                .lore(List.of(
                        loreField("Name", lootChest.name()),
                        loreField("Respawn:", lootChest.lockDuration().toMinutes() + " Minuten"),
                        loreField("Reihen", String.valueOf(lootChest.slotSize() / 9)),
                        loreField("Belohnungen", lootChest.potentialRewards().size() + " Items"),
                        loreField("ParticleSpawner", lootChest.particleSpawner().getClass().getSimpleName()),
                        Component.empty(),
                        Component.text("§7[Linksklick] §5Einstellungen §7öffnen.").color(NamedTextColor.GRAY),
                        Component.text("§7[Rechtsklick] §ePlacerchest §7erhalten.").color(NamedTextColor.GRAY)
                ))
                .build())
                .onClick(e -> handleLootChestIconClick(e, lootChest));
    }

    private void handleLootChestIconClick(InventoryClickEvent e, LootChest lootChest) {
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1.5f);
        ItemStack cursor = e.getCursor();
        if (!cursor.getType().isAir()) {
            lootChest.setGuiIconMaterial(cursor.getType());
            e.getWhoClicked().setItemOnCursor(null);
            e.setCurrentItem(createLootChestIcon(lootChest).getItem());
            return;
        } else if (e.isRightClick()) {
            player.getInventory().addItem(lootChest.chestPlacerItem());
            return;
        }
        var gui = new LootChestSettingsGui(player, lootChest);
        gui.setParent(this);
        gui.open();
    }

    private Component loreField(String key, String value) {
        return Component.text("§8• §7" + key + "§8: ").append(Component.text(value).color(NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, false);
    }


    private void handleAddClick(InventoryClickEvent e) {
        e.getWhoClicked().closeInventory();
        player.sendMessage("§7Bitte gib den Namen der neuen Lootkiste ein.");
        new PlayerChatInput((Player) e.getWhoClicked(), (input) -> {
            lootChestManager.persistLootChest(lootChestManager.createLootChest(input));
            runTaskLater(1, (s) -> open());
        });
    }
}
