package it.einjojo.akani.dungeon.gui.lootchest;

import com.google.common.base.Preconditions;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import it.einjojo.akani.dungeon.lootchest.LootChestManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

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
                        Component.text("§7Klicke um die Einstellungen zu öffnen.").color(NamedTextColor.GRAY)
                ))
                .build(), e -> {
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
