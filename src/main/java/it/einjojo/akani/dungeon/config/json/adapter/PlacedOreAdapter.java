package it.einjojo.akani.dungeon.config.json.adapter;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import it.einjojo.akani.dungeon.AkaniDungeonPlugin;
import it.einjojo.akani.dungeon.mine.MineOreType;
import it.einjojo.akani.dungeon.mine.PlacedOre;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class PlacedOreAdapter implements JsonSerializer<PlacedOre>, JsonDeserializer<PlacedOre> {

    private final AkaniDungeonPlugin akaniDungeon;

    public PlacedOreAdapter(@NotNull AkaniDungeonPlugin akaniDungeon) {
        Preconditions.checkNotNull(akaniDungeon);
        this.akaniDungeon = akaniDungeon;
    }


    @Override
    public PlacedOre deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var object = jsonElement.getAsJsonObject();
        var location = object.getAsJsonObject("location");
        var world = location.get("world").getAsString();
        var x = location.get("x").getAsInt();
        var y = location.get("y").getAsInt();
        var z = location.get("z").getAsInt();
        var pitch = location.get("pitch").getAsFloat();
        var yaw = location.get("yaw").getAsFloat();
        MineOreType oreType = akaniDungeon.akaniDungeon().config().mineOreTypeConfig().types().stream()
                .filter(_oreType -> _oreType.name().equals(object.get("type").getAsString()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ore type not found"));
        return akaniDungeon.akaniDungeon().placedOreFactory().createMineOre(new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch), oreType);
    }

    @Override
    public JsonElement serialize(PlacedOre placedOre, Type type, JsonSerializationContext jsonSerializationContext) {
        var object = new JsonObject();
        var locationObject = new JsonObject();
        Location loc = placedOre.location();
        locationObject.addProperty("world", loc.getWorld().getName());
        locationObject.addProperty("x", loc.getBlockX());
        locationObject.addProperty("y", loc.getBlockY());
        locationObject.addProperty("z", loc.getBlockZ());
        locationObject.addProperty("pitch", loc.getPitch());
        locationObject.addProperty("yaw", loc.getYaw());
        object.add("location", locationObject);
        object.addProperty("type", placedOre.type().name());
        return object;
    }
}
