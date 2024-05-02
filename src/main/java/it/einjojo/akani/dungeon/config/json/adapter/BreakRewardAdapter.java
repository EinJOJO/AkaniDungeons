package it.einjojo.akani.dungeon.config.json.adapter;

import com.google.gson.*;
import it.einjojo.akani.core.paper.util.Base64ItemStack;
import it.einjojo.akani.dungeon.mine.BreakReward;

import java.lang.reflect.Type;

public class BreakRewardAdapter implements JsonSerializer<BreakReward>, JsonDeserializer<BreakReward> {
    @Override
    public BreakReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String base64 = object.get("baseItem").getAsString();
        int min = object.get("min").getAsInt();
        int max = object.get("max").getAsInt();
        double chance = object.get("chance").getAsDouble();
        return new BreakReward(Base64ItemStack.decode(base64), min, max, chance);
    }

    @Override
    public JsonElement serialize(BreakReward src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        String base64 = Base64ItemStack.encode(src.baseItem());
        object.addProperty("baseItem", base64);
        object.addProperty("min", src.min());
        object.addProperty("max", src.max());
        object.addProperty("chance", src.chance());
        return object;
    }
}
