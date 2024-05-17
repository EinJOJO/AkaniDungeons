package it.einjojo.akani.dungeon.gui.lootchest;

import com.google.common.base.Preconditions;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.gui.GUIItem;
import it.einjojo.akani.dungeon.input.PlayerChatInput;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import it.einjojo.akani.dungeon.lootchest.LootChestManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LootChestOverviewGui implements InventoryProvider {
    private static SmartInventory instance;
    private final LootChestManager lootChestManager;

    public LootChestOverviewGui(LootChestManager lootChestManager) {
        this.lootChestManager = lootChestManager;
    }

    public static void reset() {
        instance = null;
    }

    public static SmartInventory inventory(LootChestManager lootChestManager) {
        if (instance == null) {
            Preconditions.checkNotNull(lootChestManager, "lootChestManager cannot be null");
            instance = SmartInventory.builder()
                    .id("lootChestOverviewGui")
                    .size(6, 9)
                    .provider(new LootChestOverviewGui(lootChestManager))
                    .title("§6Lootkisten")
                    .build();
        }
        return instance;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1, 1);
        contents.fillRow(5, GUIItem.BACKGROUND.emptyClickableItem());
        contents.set(5, 4, GUIItem.ADD_BUTTON.clickableItem((e) -> {
            e.getWhoClicked().closeInventory();
            new PlayerChatInput(player, (input) -> {

                lootChestManager.persistLootChest(lootChestManager.createLootChest(input));
                Bukkit.getScheduler().runTask(lootChestManager.plugin(), () -> {
                    inventory(lootChestManager).open(player);
                });
            });
        }));
        for (LootChest chestType : lootChestManager.lootChests()) {
            contents.add(chestItem(chestType));
        }
    }

    private ClickableItem chestItem(LootChest lootChest) {
        return ClickableItem.of(new ItemBuilder(lootChest.guiIconMaterial())
                .displayName(lootChest.displayName())
                .lore(List.of(
                        loreField("Name", lootChest.name()),
                        loreField("Respawn:", lootChest.lockDuration().toMinutes() + " Minuten"),
                        loreField("Reihen", String.valueOf(lootChest.slotSize() / 9)),
                        loreField("Belohnungen", lootChest.potentialRewards().size() + " Items"),
                        loreField("ParticleSpawner", lootChest.particleSpawner().getClass().getSimpleName()),
                        Component.empty(),
                        Component.text("§7Linksklicke um die Einstellungen zu öffnen.").color(NamedTextColor.GRAY),
                        Component.text("§7Rechtsklicke um eine Placerchest zu erhalten.").color(NamedTextColor.GRAY)
                ))
                .build(), e -> {

            Player clicker = (Player) e.getWhoClicked();
            clicker.playSound(clicker, Sound.UI_BUTTON_CLICK, 1, 1.5f);
            ItemStack cursor = e.getCursor();
            if (!cursor.getType().isAir()) {
                lootChest.setGuiIconMaterial(cursor.getType());
                e.getWhoClicked().setItemOnCursor(null);
                e.setCurrentItem(chestItem(lootChest).getItem());
                return;
            }
            if (e.isRightClick()) {
                clicker.getInventory().addItem(lootChest.chestPlacerItem());
                return;
            }
            LootChestSettingsGui.inventory(lootChest).open((Player) e.getWhoClicked());
        });
    }

    Component loreField(String key, String value) {
        return Component.text("§8• §7" + key + "§8: ").append(Component.text(value).color(NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, false);
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
