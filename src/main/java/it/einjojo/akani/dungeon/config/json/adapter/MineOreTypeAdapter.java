package it.einjojo.akani.dungeon.config.json.adapter;

import com.google.gson.*;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.mine.BreakReward;
import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MineOreTypeAdapter implements JsonSerializer<MineOreType>, JsonDeserializer<MineOreType> {
    @Override
    public MineOreType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
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
        ItemBuilder itemBuilder = new ItemBuilder(Material.valueOf(icon.get("material").getAsString())).
                customModelData(icon.get("customModelData").getAsInt());
        optionalTexture.ifPresent(jsonElement -> itemBuilder.skullTexture(jsonElement.getAsString()));

        // break rewards
        List<BreakReward> breakReward = new ArrayList<>();
        jsonObject.getAsJsonArray("breakRewards").forEach(jsonElement -> {
            breakReward.add(context.deserialize(jsonElement, BreakReward.class));
        });
        return new MineOreType(itemBuilder.build(), breakReward, hardness);
    }

    @Override
    public JsonElement serialize(MineOreType src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        //hardness
        jsonObject.addProperty("hardness", src.hardness().name());
        //icon
        JsonObject icon = new JsonObject();
        icon.addProperty("material", src.icon().getType().name());
        icon.addProperty("customModelData", src.icon().getItemMeta().getCustomModelData());
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
            breakRewards.add(context.serialize(breakReward, BreakReward.class));
        });
        jsonObject.add("breakRewards", breakRewards);
        return jsonObject;
    }
}
