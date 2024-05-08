package it.einjojo.akani.dungeon.mine.tool;

import it.einjojo.akani.dungeon.config.ToolConfig;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ToolFactory {
    private final ToolConfig config;
    private final Map<ToolIdentifier, Tool> customLinkedTools = new HashMap<>();
    private final Map<ItemStack, Tool> cachedTools = new HashMap<>();

    public ToolFactory(ToolConfig config) {
        this.config = config;
    }

    public void linkTool(ToolIdentifier condition, Tool tool) {
        customLinkedTools.put(condition, tool);
    }

    public Tool fromItemStack(ItemStack itemStack) {
        if (!customLinkedTools.isEmpty()) {
            for (Map.Entry<ToolIdentifier, Tool> entry : customLinkedTools.entrySet()) {
                if (entry.getKey().matches(itemStack)) {
                    return entry.getValue();
                }
            }
        }
        Tool tool;
        if ((tool = cachedTools.get(itemStack)) != null) {
            return tool;
        }
        tool = createTool(itemStack);
        cachedTools.put(itemStack, tool);
        return tool;
    }

    protected Tool createTool(ItemStack itemStack) {
        try {
            ToolType toolType = ToolType.fromMaterial(itemStack.getType()); // throws exception if not a tool
            float damage = config.materialBaseDamage(itemStack.getType());
            float lootMultiplier = 1.0f;
            ItemMeta is = itemStack.getItemMeta();
            if (is.hasEnchant(Enchantment.DIG_SPEED)) {
                damage += is.getEnchantLevel(Enchantment.DIG_SPEED) * config.efficiencyDamageBonus();
            }
            if (is.hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
                lootMultiplier = (float) Math.pow(config.luckLootMultiplier(), is.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS));
            }
            return new Tool.Impl(damage, toolType, lootMultiplier, false);
        } catch (Exception e) {
            return new NullTool();
        }
    }


}
