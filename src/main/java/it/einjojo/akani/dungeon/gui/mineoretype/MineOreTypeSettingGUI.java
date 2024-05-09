package it.einjojo.akani.dungeon.gui.mineoretype;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.gui.GUIItem;
import it.einjojo.akani.dungeon.gui.GuiManager;
import it.einjojo.akani.dungeon.input.PlayerChatInput;
import it.einjojo.akani.dungeon.mine.BreakReward;
import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOreType;
import it.einjojo.akani.dungeon.mine.tool.ToolType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class MineOreTypeSettingGUI implements InventoryProvider {

    private final MineOreType oreType;
    private final GuiManager guiManager;
    private final JavaPlugin plugin;

    public MineOreTypeSettingGUI(MineOreType oreType, GuiManager guiManager, JavaPlugin plugin) {
        this.oreType = oreType;
        this.guiManager = guiManager;
        this.plugin = plugin;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, GUIItem.BACKGROUND.emptyClickableItem());
        contents.fillRow(5, GUIItem.BACKGROUND.emptyClickableItem());
        ClickableItem close = ClickableItem.of(new ItemBuilder(Material.BARRIER)
                .displayName(Component.text("§cSchließen")).build(), e -> guiManager.mineOreTypeSelectorGUI().open(player));
        contents.set(5, 0, close);
        addMaxHP(player, contents);
        addIcon(contents);
        addToolType(player, contents);
        addItemList(contents);
        addHardnessSelector(player, contents);
    }

    protected void addMaxHP(Player player, InventoryContents contents) {
        ClickableItem maxHp = ClickableItem.of(new ItemBuilder(Material.REDSTONE)
                .displayName(Component.text("§cMax HP: §7" + oreType.maxHealth() + " ❤"))
                .lore(List.of(
                        Component.empty(),
                        Component.text("§7Stelle die maximale Lebenspunkte ein."),
                        Component.empty(),
                        Component.text("§7▶ Aktuelle HP: §c" + oreType.maxHealth() + " ❤"),
                        Component.empty()
                )).build(), (e) -> {
            e.getWhoClicked().closeInventory();
            new PlayerChatInput(player, (input) -> {
                try {
                    float maxHpValue = Float.parseFloat(input);
                    oreType.setMaxHealth(maxHpValue);
                    player.sendMessage(Component.text("§aMax HP wurde auf §c" + maxHpValue + " ❤ §agesetzt."));
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        guiManager.mineOreTypeSelectorGUI().open(player);
                    });
                } catch (NumberFormatException ex) {
                    player.sendMessage(Component.text("§cBitte gib eine gültige Zahl ein."));
                }
            });
        });
        contents.set(1, 6, maxHp);
    }

    protected void addIcon(InventoryContents contents) {
        ClickableItem icon = ClickableItem.empty(new ItemBuilder(oreType.icon()).lore(oreType.description()).build());
        contents.set(5, 4, icon);
    }

    protected void addToolType(Player player, InventoryContents contents) {
        SlotPos toolTypeSelector = SlotPos.of(1, 4);
        contents.set(toolTypeSelector, ClickableItem.of(new ItemBuilder(ToolType.material(oreType().toolType(), oreType.hardness()))
                .displayName(Component.text("§c" + oreType.toolType().name()))
                .lore(List.of(
                        Component.empty(),
                        Component.text("§7Wähle das Werkzeug, das benötigt wird,"),
                        Component.text("§7um dieses Erz abzubauen."),
                        Component.empty(),
                        Component.text("§7▶ Aktuelles Werkzeug: §c" + oreType.toolType().name()),
                        Component.empty()
                )).addItemFlag(ItemFlag.HIDE_ENCHANTS).build(), (e) -> {
            int index = oreType.toolType().ordinal();
            index++;
            if (index >= ToolType.values().length) {
                index = 0;
            }
            oreType.setToolType(ToolType.values()[index]);
            init(player, contents);
        }));

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
