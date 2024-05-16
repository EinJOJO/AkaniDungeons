package it.einjojo.akani.dungeon.gui.lootchest;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.AkaniDungeonPlugin;
import it.einjojo.akani.dungeon.gui.GUIItem;
import it.einjojo.akani.dungeon.input.PlayerChatInput;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import it.einjojo.akani.dungeon.util.ItemReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LootChestItemsGui implements InventoryProvider {
    private final LootChest lootChest;

    public LootChestItemsGui(LootChest lootChest) {
        this.lootChest = lootChest;
    }

    public static SmartInventory inventory(LootChest lootChest) {
        var provider = new LootChestItemsGui(lootChest);
        return SmartInventory.builder()
                .id("lootChestItemsGui")
                .provider(provider)
                .size(6, 9)
                .listener(new InventoryListener<>(InventoryDragEvent.class, (e) -> {
                    e.getWhoClicked().sendMessage("DRAG");
                }))
                .title("§6Lootkiste: " + lootChest.name())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, GUIItem.BACKGROUND.emptyClickableItem());
        contents.fillRow(5, GUIItem.BACKGROUND.emptyClickableItem());
        contents.set(5, 4, GUIItem.ADD_BUTTON.clickableItem(this::onClick));
        int i = 0;
        for (ItemReward itemReward : lootChest.potentialRewards()) {
            contents.set(1 + i / 9, i, createItemReward(itemReward));
            i++;
        }
    }

    private ClickableItem createItemReward(ItemReward itemReward) {
        return ClickableItem.of(new ItemBuilder(itemReward.baseItem())
                .lore(List.of(
                        Component.text("§7min: §e" + itemReward.min()),
                        Component.text("§7max: §e" + itemReward.max()),
                        Component.text("§7Chance: §e" + itemReward.chance()),
                        Component.empty(),
                        Component.text("§7Linksklick: §eMax-Wert §7setzen"),
                        Component.text("§7Shift-Linksklick: §eMin-Wert §7setzen"),
                        Component.text("§7Rechtsklick §eChance §7setzen"),
                        Component.text("§7Shift-Rechtsklick §cLöschen")
                ))
                .build(), (e) -> {
            Player player = (Player) e.getWhoClicked();
            boolean shift = e.isShiftClick();
            boolean right = e.isRightClick();
            boolean left = e.isLeftClick();


            if (left && !shift) {
                new PlayerChatInput(player, (input) -> {
                    player.closeInventory();
                    try {
                        itemReward.setMax(Short.parseShort(input));
                    } catch (NumberFormatException ex) {
                        player.sendMessage("§cUngültige Zahl");
                    }
                    reopen(player);
                });
            } else if (left) {
                new PlayerChatInput(player, (input) -> {
                    player.closeInventory();
                    try {
                        itemReward.setMin(Short.parseShort(input));
                    } catch (NumberFormatException ex) {
                        player.sendMessage("§cUngültige Zahl");
                    }
                    reopen(player);
                });
            } else if (right && shift) {
                player.closeInventory();
                lootChest.potentialRewards().remove(itemReward);
                reopen(player);
            } else if (right) {
                player.closeInventory();
                new PlayerChatInput(player, (input) -> {
                    try {
                        itemReward.setChance(Float.parseFloat(input));
                    } catch (NumberFormatException ex) {
                        player.sendMessage("§cUngültige Zahl");
                    }
                    reopen(player);
                });
            }

        });
    }

    public void reopen(Player player) {
        Bukkit.getScheduler().runTask(AkaniDungeonPlugin.get(), () -> inventory(lootChest).open(player));
    }

    public void onClick(InventoryClickEvent event) {
        ItemStack toAdd = event.getCursor();
        if (toAdd.getType().isAir()) return;
        ItemReward newReward = new ItemReward(toAdd.clone(), (short) toAdd.getAmount(), (short) toAdd.getAmount(), 1.0f);
        lootChest.potentialRewards().add(newReward);
        event.setCancelled(true);
        inventory(lootChest).open((Player) event.getWhoClicked());
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
