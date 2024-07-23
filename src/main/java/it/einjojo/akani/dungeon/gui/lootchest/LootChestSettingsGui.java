package it.einjojo.akani.dungeon.gui.lootchest;

import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.gui.GUIItem;
import it.einjojo.akani.dungeon.gui.ParentableGui;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import it.einjojo.akani.dungeon.lootchest.particle.ParticleSpawnerFactory;
import it.einjojo.akani.util.inventory.Gui;
import it.einjojo.akani.util.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        addItem(new Icon(Material.YELLOW_STAINED_GLASS_PANE).setName("§6Legendär").onClick(e -> {
            lootChest.setParticleSpawner(new ParticleSpawnerFactory().createLegendaryParticleSpawner());
        }), 9 * 2 + 2);

        addItem(new Icon(Material.PURPLE_STAINED_GLASS_PANE).setName("§5Episch").onClick(e -> {
            lootChest.setParticleSpawner(new ParticleSpawnerFactory().createEpicParticleSpawner());
        }), 9 * 2 + 3);

        addItem(new Icon(Material.WHITE_STAINED_GLASS_PANE).setName("§fNormal").onClick(e -> {
            lootChest.setParticleSpawner(new ParticleSpawnerFactory().createNormalParticleSpawner());
        }), 9 * 2 + 4);


    }

    public void setParent(Gui parent) {
        this.parent = parent;
    }

    public Gui getParent() {
        return parent;
    }
}
