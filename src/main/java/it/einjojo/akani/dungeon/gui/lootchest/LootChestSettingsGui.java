package it.einjojo.akani.dungeon.gui.lootchest;

import it.einjojo.akani.dungeon.gui.GUIItem;
import it.einjojo.akani.dungeon.gui.ParentableGui;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import it.einjojo.akani.dungeon.lootchest.particle.ParticleSpawnerFactory;
import it.einjojo.akani.util.inventory.Gui;
import it.einjojo.akani.util.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class LootChestSettingsGui extends Gui implements ParentableGui {
    private final LootChest lootChest;
    private Gui parent;

    public LootChestSettingsGui(Player player, LootChest lootChest) {
        super(player, "lootchest_config", lootChest.displayName(), 6);
        this.lootChest = lootChest;
    }


    @Override
    public void onOpen(InventoryOpenEvent event) {
        Icon background = GUIItem.BACKGROUND.icon().onClick(this::openParent);
        fillRow(background, 0);
        fillRow(background, 5);
        addItem(11, new Icon(Material.CHEST).setName("§7Items verwalten").onClick(e -> {
            var gui = new LootChestItemsGui(player, lootChest);
            gui.setParent(this);
            gui.open();
        }));
        placeParticleSettings();
    }


    private void placeParticleSettings() {
        int row = 9 * 2;
        var factory = new ParticleSpawnerFactory();
        addItem(row + 2, new Icon(Material.YELLOW_STAINED_GLASS_PANE).setName("§6Legendär").onClick(e -> {
            lootChest.setParticleSpawner(factory.createLegendaryParticleSpawner());
        }));
        addItem(row + 3, new Icon(Material.PURPLE_STAINED_GLASS_PANE).setName("§5Episch").onClick(e -> {
            lootChest.setParticleSpawner(factory.createEpicParticleSpawner());
        }));

        addItem(row + 4, new Icon(Material.WHITE_STAINED_GLASS_PANE).setName("§fNormal").onClick(e -> {
            lootChest.setParticleSpawner(factory.createNormalParticleSpawner());
        }));
        addItem(row + 5, new Icon(Material.BLUE_STAINED_GLASS_PANE).setName("§9Sehr selten").onClick(e -> {
            lootChest.setParticleSpawner(factory.createVeryRareParticleSpawner());
        }));
        addItem(row + 6, new Icon(Material.GREEN_STAINED_GLASS_PANE).setName("§aSelten").onClick(e -> {
            lootChest.setParticleSpawner(factory.createRareParticleSpawner());
        }));


    }

    public void setParent(Gui parent) {
        this.parent = parent;
    }

    public Gui getParent() {
        return parent;
    }
}
