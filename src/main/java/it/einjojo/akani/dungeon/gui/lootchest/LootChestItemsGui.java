package it.einjojo.akani.dungeon.gui.lootchest;

import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.gui.GUIItem;
import it.einjojo.akani.dungeon.gui.ParentableGui;
import it.einjojo.akani.dungeon.input.PlayerChatInput;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import it.einjojo.akani.dungeon.util.ItemReward;
import it.einjojo.akani.util.inventory.Gui;
import it.einjojo.akani.util.inventory.Icon;
import it.einjojo.akani.util.inventory.pagination.PaginationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @since 1.2.0
 */
public class LootChestItemsGui extends Gui implements ParentableGui {
    private final LootChest lootChest;
    private Gui parent;
    private final PaginationManager paginationManager = new PaginationManager(this);

    @Nullable
    @Override
    public Gui getParent() {
        return parent;
    }

    @Override
    public void setParent(Gui parent) {
        this.parent = parent;
    }

    public LootChestItemsGui(@NotNull Player player, LootChest lootChest) {
        super(player, "lootChestItems", Component.text("Items: ", NamedTextColor.GRAY)
                .append(lootChest.displayName()), 6);
        this.lootChest = lootChest;
        paginationManager.registerPageSlotsBetween(9, 9 * 5 - 1);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Icon returningBackground = GUIItem.BACKGROUND.icon().onClick(this::openParent);
        fillRow(returningBackground, 0);
        fillRow(returningBackground, 5);
        addPaginationItems();
        renderItems();
        addItem(GUIItem.PLUS_SKULL.icon().setName("§cItem hinzufügen").setLore("§7Ziehe das Item hinein.").onClick(this::handleAddClickEvent));
    }

    private void renderItems() {
        paginationManager.getItems().clear();
        for (ItemReward itemReward : lootChest.potentialRewards()) {
            paginationManager.addItem(createItemRewardIcon(itemReward));
        }
        paginationManager.update();
    }

    private void addPaginationItems() {
        addItem(GUIItem.LEFT_SKULL.icon().setName("§cVorherige Seite").onClick(e -> {
            paginationManager.goPreviousPage();
            paginationManager.update();
        }));
        addItem(GUIItem.RIGHT_SKULL.icon().setName("§cNächste Seite").onClick(e -> {
            paginationManager.goNextPage();
            paginationManager.update();
        }));
    }

    private Icon createItemRewardIcon(ItemReward itemReward) {
        return new Icon(new ItemBuilder(itemReward.baseItem())
                .lore(List.of(
                        Component.text("§7min: §e" + itemReward.min()),
                        Component.text("§7max: §e" + itemReward.max()),
                        Component.text("§7Chance: §e" + itemReward.chance()),
                        Component.empty(),
                        Component.text("§7[Linksklick]: §eMax-Wert §7setzen"),
                        Component.text("§7[Shift + Linksklick]: §eMin-Wert §7setzen"),
                        Component.text("§7[Rechtsklick] §eChance §7setzen"),
                        Component.text("§7[Shift + Rechtsklick] §cLöschen")
                ))
                .build()).onClick(e -> {
            boolean shift = e.isShiftClick();
            boolean right = e.isRightClick();
            boolean left = e.isLeftClick();
            if (left && !shift) {
                player.closeInventory();
                player.sendMessage("§7Bitte gib den neuen Max-Wert ein:");
                new PlayerChatInput(player, (input) -> {
                    try {
                        itemReward.setMax(Short.parseShort(input));
                        playSuccessSound();
                    } catch (NumberFormatException ex) {
                        player.sendMessage("§cUngültige Zahl");
                        playBadInputSound();
                    }
                    threadSafeOpen();
                });
            } else if (left) {
                player.closeInventory();
                player.sendMessage("§7Bitte gib den neuen Min-Wert ein:");
                new PlayerChatInput(player, (input) -> {
                    try {
                        itemReward.setMin(Short.parseShort(input));
                        playSuccessSound();
                    } catch (NumberFormatException ex) {
                        player.sendMessage("§cUngültige Zahl");
                        playBadInputSound();
                    }
                    threadSafeOpen();
                });
            } else if (right && shift) {
                lootChest.potentialRewards().remove(itemReward);
                renderItems();
            } else if (right) {
                player.closeInventory();
                player.sendMessage("§7Bitte gib die neue Chance ein:");
                new PlayerChatInput(player, (input) -> {
                    try {
                        itemReward.setChance(Float.parseFloat(input));
                        playSuccessSound();
                    } catch (NumberFormatException ex) {
                        player.sendMessage("§cUngültige Zahl");
                    }
                    threadSafeOpen();
                });
            }
        });
    }

    private void playBadInputSound() {
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 0.5f);
    }

    private void playSuccessSound() {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    private void threadSafeOpen() {
        runTaskLater(1, (s) -> open());
    }

    private void handleAddClickEvent(InventoryClickEvent event) {
        ItemStack toAdd = event.getCursor();
        if (toAdd.getType().isAir()) return;
        ItemReward newReward = new ItemReward(toAdd.clone(), (short) toAdd.getAmount(), (short) toAdd.getAmount(), 1.0f);
        lootChest.potentialRewards().add(newReward);
        renderItems();
    }

}
