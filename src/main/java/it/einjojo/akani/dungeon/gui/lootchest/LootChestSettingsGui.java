package it.einjojo.akani.dungeon.gui.lootchest;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.gui.GUIItem;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import it.einjojo.akani.dungeon.lootchest.particle.ParticleSpawnerFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LootChestSettingsGui implements InventoryProvider {
    private final LootChest lootChest;

    public LootChestSettingsGui(LootChest lootChest) {
        this.lootChest = lootChest;
    }

    public static SmartInventory inventory(LootChest lootChest) {
        return SmartInventory.builder()
                .id("lootChestSettingsGui")
                .provider(new LootChestSettingsGui(lootChest))
                .size(6, 9)
                .title("§6Lootkiste: " + lootChest.name())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ClickableItem i = (GUIItem.BACKGROUND.clickableItem(e -> {
            LootChestOverviewGui.inventory(null).open(player);
        }));
        contents.fillRow(0, i);
        contents.set(1, 2, ClickableItem.of(new ItemBuilder(Material.CHEST).build(), e -> {
            LootChestItemsGui.inventory(lootChest).open(player);
        }));
        placeParticleSettings(contents);

        contents.fillRow(5, i);

    }


    private void placeParticleSettings(InventoryContents contents) {
        contents.set(2, 2, ClickableItem.of(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).build(), (e) -> {
            lootChest.setParticleSpawner(new ParticleSpawnerFactory().createLegendaryParticleSpawner());
        }));
        contents.set(2, 3, ClickableItem.of(new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).build(), (e) -> {
            lootChest.setParticleSpawner(new ParticleSpawnerFactory().createEpicParticleSpawner());
        }));
        contents.set(2, 4, ClickableItem.of(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).build(), (e) -> {
            lootChest.setParticleSpawner(new ParticleSpawnerFactory().createNormalParticleSpawner());
        }));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
