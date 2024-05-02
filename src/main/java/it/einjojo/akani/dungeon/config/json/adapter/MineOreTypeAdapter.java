package it.einjojo.akani.dungeon.config.json.adapter;

import com.google.gson.*;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.mine.BreakReward;
import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MineOreTypeAdapter implements JsonSerializer<MineOreType>, JsonDeserializer<MineOreType> {
    @Override
    public MineOreType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        //hardness
        Hardness hardness = Hardness.valueOf(jsonObject.get("hardness").getAsString());
        //icon
        JsonObject icon = jsonObject.getAsJsonObject("icon");
        Optional<JsonElement> optionalTexture = Optional.ofNullable(jsonObject.get("texture"));
        String iconMaterial = icon.get("material").getAsString();
        int customModelData = icon.get("customModelData").getAsInt();
        ItemBuilder itemBuilder = new ItemBuilder(Material.valueOf(iconMaterial));
        optionalTexture.ifPresent(jsonElement -> itemBuilder.skullTexture(jsonElement.getAsString()));

        // break rewards
        List<BreakReward> breakReward = new ArrayList<>();
        return new MineOreType(itemBuilder.build(), breakReward, );
    }

    @Override
    public JsonElement serialize(MineOreType src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
