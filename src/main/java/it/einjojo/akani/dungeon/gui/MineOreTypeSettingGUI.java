package it.einjojo.akani.dungeon.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.mine.BreakReward;
import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOreType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MineOreTypeSettingGUI implements InventoryProvider {

    private final MineOreType oreType;
    private final GuiManager guiManager;

    public MineOreTypeSettingGUI(MineOreType oreType, GuiManager guiManager) {
        this.oreType = oreType;
        this.guiManager = guiManager;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ClickableItem close = ClickableItem.of(new ItemBuilder(Material.BARRIER).displayName(Component.text("§cSchließen")).build(), e -> guiManager.mineOreTypeSelectorGUI().open(player));
        ClickableItem icon = ClickableItem.empty(oreType.icon());
        ClickableItem toolType = ClickableItem.empty(oreType.icon());
        ClickableItem maxHp = ClickableItem.empty(oreType.icon());
        contents.set(5, 4, icon);
        contents.set(5, 0, close);
        addItemList(contents);
        addHardnessSelector(player, contents);
    }

    protected void addItemList(InventoryContents contents) {
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("§7Ziehe ein Item auf dieses Feld, um es hinzuzufügen."));
        lore.add(Component.text("§7Die Parameter(min,max, chance) können nur in der Config eingestellt werden"));
        lore.add(Component.empty());
        lore.add(Component.text("§7▶ Aktuelle Items:"));
        oreType.breakRewards().forEach(item -> lore.add(Component.text("§c" + item.baseItem().getType().name() + "§7: min(" + item.min() + ") max(" + item.max() + ")  :  §c" + item.chance() * 100 + "%")));
        contents.set(SlotPos.of(1, 2), ClickableItem.of(new ItemBuilder(Material.COMPOSTER).displayName(Component.text("§cItems")).lore(lore).build(), (event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor != null) {
                if (cursor.getType().equals(Material.AIR)) return;
                oreType.breakRewards().add(new BreakReward(cursor.clone(), (short) 1, (short) 1, 1f));
                Player clicker = (Player) event.getWhoClicked();
                clicker.playSound(clicker, Sound.ENTITY_CHICKEN_EGG, 1, 3);
                event.getWhoClicked().setItemOnCursor(null);
                init(clicker, contents);
            }
        }));
    }

    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    protected void addHardnessSelector(Player player, InventoryContents contents) {
        SlotPos hardnessSelector = SlotPos.of(3, 1);
        for (int i = 0; i < Hardness.values().length; i++) {
            final int index = i;
            Hardness hardness = Hardness.values()[i];
            ItemBuilder itemBuilder = new ItemBuilder(hardness.icon()).displayName(hardness.displayName()).lore(List.of(
                    Component.empty(),
                    Component.text("§7Wähle die erforderliche Härte, die es benötigt,"),
                    Component.text("§7um dieses Erz abzubauen."),
                    Component.empty(),
                    Component.text("§7▶ Aktuelle Härte: §c" + oreType.hardness().name()),
                    Component.empty()
            )).addItemFlag(ItemFlag.HIDE_ENCHANTS);
            if (oreType.hardness() == hardness) {
                itemBuilder.addEnchantment(Enchantment.DURABILITY);
            }

            contents.set(hardnessSelector, ClickableItem.of(itemBuilder.build(), e -> {
                oreType.setHardness(Hardness.values()[index]);
                init(player, contents);
            }));
            hardnessSelector = SlotPos.of(hardnessSelector.getRow(), hardnessSelector.getColumn() + 1);
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }

    public MineOreType oreType() {
        return oreType;
    }
}
