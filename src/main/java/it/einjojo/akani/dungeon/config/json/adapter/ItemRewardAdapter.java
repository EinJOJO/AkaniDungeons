package it.einjojo.akani.dungeon.config.json.adapter;

import com.google.gson.*;
import it.einjojo.akani.core.paper.util.Base64ItemStack;
import it.einjojo.akani.dungeon.util.ItemReward;

import java.lang.reflect.Type;

public class ItemRewardAdapter implements JsonSerializer<ItemReward>, JsonDeserializer<ItemReward> {
    @Override
    public ItemReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String base64 = object.get("baseItem").getAsString();
        short min = object.get("min").getAsShort();
        short max = object.get("max").getAsShort();
        float chance = object.get("chance").getAsFloat();
        return new ItemReward(Base64ItemStack.decode(base64), min, max, chance);
    }

    @Override
    public JsonElement serialize(ItemReward src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        String base64 = Base64ItemStack.encode(src.baseItem());
        object.addProperty("baseItem", base64);
        object.addProperty("min", src.min());
        object.addProperty("max", src.max());
        object.addProperty("chance", src.chance());
        return object;
    }
}
