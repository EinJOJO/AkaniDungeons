package it.einjojo.akani.dungeon.gui.mineoretype;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.config.MineOreTypeConfig;
import it.einjojo.akani.dungeon.gui.GUIItem;
import it.einjojo.akani.dungeon.gui.GuiManager;
import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOreType;
import it.einjojo.akani.dungeon.mine.tool.ToolType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MineOreTypeSelectorGUI implements InventoryProvider {
    private static final List<Component> CLICK_ACTION = List.of(

            Component.empty(),
            Component.text("§7[§cLinksklick§7] Einstellungen"),
            Component.text("§7[§cRechtsklick§7] Spawn-Ei"),
            Component.empty()

    );
    private final MineOreTypeConfig config;
    private final GuiManager guiManager;


    public MineOreTypeSelectorGUI(MineOreTypeConfig config, GuiManager guiManager) {
        this.config = config;
        this.guiManager = guiManager;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, GUIItem.BACKGROUND.emptyClickableItem());
        contents.fillRow(5, GUIItem.BACKGROUND.emptyClickableItem());
        for (int i = 0; i < config.types().size(); i++) {
            MineOreType oreType = config.types().get(i);
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.addAll(oreType.description());
            lore.addAll(CLICK_ACTION);
            ClickableItem item = ClickableItem.of(new ItemBuilder(oreType.icon()).lore(lore).build(), (e) -> {
                if (e.isLeftClick()) {
                    guiManager.mineOreTypeGUI(oreType).open(player);
                } else {
                    player.getInventory().addItem(oreType.spawnEggItemStack());
                }
            });
            contents.add(item);
        }
        contents.set(5, 4, GUIItem.ADD_BUTTON.clickableItem(e -> {
            ItemStack cursorItem = e.getCursor();
            if (cursorItem == null || cursorItem.getType().isAir()) {
                player.sendMessage(Component.text("§aHalte den Block, der zusehen sein soll, in der Hand und gib ihm einen Namen."));
                player.sendMessage(Component.text("§aVerwende /amine create <name> um eine neue Erz-Kategorie zu erstellen."));
                player.closeInventory();
                return;
            }
            ItemStack icon = cursorItem.clone();
            icon.setAmount(1);
            config.addOreType(new MineOreType(cursorItem.getType().name().toLowerCase(), icon, new ArrayList<>(), Hardness.UNDETERMINED, 10, ToolType.PICKAXE));
            player.setItemOnCursor(null);
            init(player, contents);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
