package it.einjojo.akani.dungeon.config.json.adapter;

import com.google.gson.*;
import it.einjojo.akani.dungeon.mine.BreakReward;

import java.lang.reflect.Type;

public class BreakRewardAdapter implements JsonSerializer<BreakReward>, JsonDeserializer<BreakReward> {
    @Override
    public BreakReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }

    @Override
    public JsonElement serialize(BreakReward src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
