package it.einjojo.akani.dungeon.gui.mineoretype;

import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.gui.GUIItem;
import it.einjojo.akani.dungeon.gui.ParentableGui;
import it.einjojo.akani.dungeon.input.PlayerChatInput;
import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOreType;
import it.einjojo.akani.dungeon.mine.tool.ToolType;
import it.einjojo.akani.dungeon.util.ItemReward;
import it.einjojo.akani.util.inventory.Gui;
import it.einjojo.akani.util.inventory.Icon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MineOreTypeSettingGUI extends Gui implements ParentableGui {
    private final MineOreType oreType;
    private Gui parent;

    public MineOreTypeSettingGUI(Player player, MineOreType oreType) {
        super(player, "oretype_setting", Component.text("Erz Einstellungen", NamedTextColor.GRAY), 6);
        this.oreType = oreType;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Icon background = GUIItem.BACKGROUND.icon().onClick(this::openParent);
        fillRow(background, 0);
        fillRow(background, 5);
        addMaxHP();
        addOreIcon();
        addToolType();
        addItemList();
        addHardnessSelector();
    }


    protected void addMaxHP() {
        Icon icon = new Icon(new ItemBuilder(Material.REDSTONE)
                .displayName(Component.text("§cMax HP: §7" + oreType.maxHealth() + " ❤"))
                .lore(List.of(
                        Component.empty(),
                        Component.text("§7Stelle die maximale Lebenspunkte ein."),
                        Component.empty(),
                        Component.text("§7▶ Aktuelle HP: §c" + oreType.maxHealth() + " ❤"),
                        Component.empty()
                )).build()).onClick(e -> {
            e.getWhoClicked().closeInventory();
            new PlayerChatInput(player, (input) -> {
                try {
                    float maxHpValue = Float.parseFloat(input);
                    oreType.setMaxHealth(maxHpValue);
                    player.sendMessage(Component.text("§aMax HP wurde auf §c" + maxHpValue + " ❤ §agesetzt."));
                    runTaskLater(1, (s) -> open());
                } catch (NumberFormatException ex) {
                    player.sendMessage(Component.text("§cBitte gib eine gültige Zahl ein."));
                }
            });
        });
        addItem(9 + 7, icon);
    }

    protected void addOreIcon() {
        addItem(9 + 1, new ItemBuilder(oreType.icon()).lore(oreType.description()).build());
    }

    protected void addToolType() {
        addItem(9 + 5, new Icon(new ItemBuilder(ToolType.material(oreType().toolType(), oreType.hardness()))
                .displayName(Component.text("§c" + oreType.toolType().name()))
                .lore(List.of(
                        Component.empty(),
                        Component.text("§7Wähle das Werkzeug, das benötigt wird,"),
                        Component.text("§7um dieses Erz abzubauen."),
                        Component.empty(),
                        Component.text("§7▶ Aktuelles Werkzeug: §c" + oreType.toolType().name()),
                        Component.empty()
                )).addItemFlag(ItemFlag.HIDE_ENCHANTS).build()).onClick(e -> {
            int index = oreType.toolType().ordinal();
            index++;
            if (index >= ToolType.values().length) {
                index = 0;
            }
            oreType.setToolType(ToolType.values()[index]);
            addToolType();
        }));

    }


    protected void addItemList() {
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("§7Ziehe ein Item auf dieses Feld, um es hinzuzufügen."));
        lore.add(Component.text("§7Die Parameter(min,max, chance) können nur in der Config eingestellt werden"));
        lore.add(Component.empty());
        lore.add(Component.text("§7▶ Aktuelle Items:"));
        oreType.breakRewards().forEach(item -> lore.add(Component.text("§c" + item.baseItem().getType().name() + "§7: min(" + item.min() + ") max(" + item.max() + ")  :  §c" + item.chance() * 100 + "%")));
        addItem(9 + 3, new Icon(new ItemBuilder(Material.COMPOSTER).displayName(Component.text("§cItems")).lore(lore).build()).onClick(event -> {
            ItemStack cursor = event.getCursor();
            if (cursor.getType().equals(Material.AIR)) return;
            oreType.breakRewards().add(new ItemReward(cursor.clone(), (short) 1, (short) 1, 1f));
            Player clicker = (Player) event.getWhoClicked();
            clicker.playSound(clicker, Sound.ENTITY_CHICKEN_EGG, 1, 3);
            event.getWhoClicked().setItemOnCursor(null);
            addItemList();
        }));
    } // TODO make it configurable.


    protected void addHardnessSelector() {
        int offset = 0;
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

            addItem(9 * 3 + 1 + (offset++), new Icon(itemBuilder.build()).onClick(e -> {
                oreType.setHardness(Hardness.values()[index]);
                addHardnessSelector();
            }));
        }
    }

    public MineOreType oreType() {
        return oreType;
    }

    @Override
    public @Nullable Gui getParent() {
        return parent;
    }

    @Override
    public void setParent(@Nullable Gui parent) {
        this.parent = parent;
    }
}
