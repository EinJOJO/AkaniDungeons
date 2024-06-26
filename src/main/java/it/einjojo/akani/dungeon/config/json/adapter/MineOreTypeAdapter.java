package it.einjojo.akani.dungeon.config.json.adapter;

import com.google.gson.*;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.util.ItemReward;
import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOreType;
import it.einjojo.akani.dungeon.mine.tool.ToolType;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MineOreTypeAdapter implements JsonSerializer<MineOreType>, JsonDeserializer<MineOreType> {
    @Override
    public MineOreType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        //hardness
        Hardness hardness = Hardness.UNDETERMINED;
        try {
            JsonElement hardnessJson = jsonObject.get("hardness");
            if (hardnessJson != null) {
                hardness = Hardness.valueOf(hardnessJson.getAsString());
            }
        } catch (IllegalArgumentException ignored) {
        }
        //icon
        JsonObject icon = jsonObject.getAsJsonObject("icon");
        Optional<JsonElement> optionalTexture = Optional.ofNullable(jsonObject.get("texture"));
        int customModelData = icon.get("customModelData").getAsInt();
        ItemBuilder itemBuilder = new ItemBuilder(Material.valueOf(icon.get("material").getAsString()));
        if (customModelData != 0) {
            itemBuilder.customModelData(customModelData);
        }
        optionalTexture.ifPresent(jsonElement -> itemBuilder.skullTexture(jsonElement.getAsString()));

        //maxhp
        float maxHP = Optional.ofNullable(jsonObject.get("maxHealth")).map(JsonElement::getAsFloat).orElse(10F);

        // break rewards
        List<ItemReward> itemReward = new ArrayList<>();
        jsonObject.getAsJsonArray("breakRewards").forEach(jsonElement -> {
            itemReward.add(context.deserialize(jsonElement, ItemReward.class));
        });

        // tool
        ToolType toolType = Optional.ofNullable(jsonObject.get("toolType")).map(jsonElement -> ToolType.valueOf(jsonElement.getAsString())).orElse(ToolType.PICKAXE);

        // respawnTime
        Duration respawnTime = Optional.ofNullable(jsonObject.get("respawnTimeSeconds")).map(jsonElement -> Duration.ofSeconds(jsonElement.getAsLong())).orElse(Duration.ofMinutes(30));

        return new MineOreType(name, itemBuilder.build(), itemReward, hardness, maxHP, toolType, respawnTime);
    }

    @Override
    public JsonElement serialize(MineOreType src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        //name
        jsonObject.addProperty("name", src.name());
        //hardness
        jsonObject.addProperty("hardness", src.hardness().name());
        //icon
        JsonObject icon = new JsonObject();
        icon.addProperty("material", src.icon().getType().name());
        int customModelData = src.icon().getItemMeta().hasCustomModelData() ? src.icon().getItemMeta().getCustomModelData() : 0;
        icon.addProperty("customModelData", customModelData);
        ItemMeta itemMeta = src.icon().getItemMeta();
        if (itemMeta instanceof SkullMeta skullMeta) {
            skullMeta.getPlayerProfile().getProperties().forEach(property -> {
                if (property.getName().equals("textures")) {
                    icon.addProperty("texture", property.getValue());
                }
            });
        }
        jsonObject.add("icon", icon);
        //break rewards
        JsonArray breakRewards = new JsonArray();
        src.breakRewards().forEach(breakReward -> {
            breakRewards.add(context.serialize(breakReward, ItemReward.class));
        });
        jsonObject.add("breakRewards", breakRewards);

        //maxhp
        jsonObject.addProperty("maxHealth", src.maxHealth());
        //tool
        jsonObject.addProperty("toolType", src.toolType().name());

        // respawnTime
        jsonObject.addProperty("respawnTimeSeconds", src.respawnTime().toSeconds());

        return jsonObject;
    }
}
