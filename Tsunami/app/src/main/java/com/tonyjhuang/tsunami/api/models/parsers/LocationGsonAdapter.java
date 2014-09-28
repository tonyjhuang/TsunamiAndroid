package com.tonyjhuang.tsunami.api.models.parsers;

import android.location.Location;
import android.os.Bundle;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.tonyjhuang.tsunami.logging.Timber;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Parses a JSON array of two doubles into a Location object.
 * Created by tonyjhuang on 8/19/14.
 */
public class LocationGsonAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {

    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        if (jsonElement == null) {
            return null;
        }

        Type collectionType = new TypeToken<List<Double>>(){}.getType();
        List<Double> coords = context.deserialize(jsonElement, collectionType);
        if (coords == null) {
            throw new JsonParseException("Couldn't deserialize location from JSON: " + jsonElement);
        }

        Location location = new Location("TsunamiAPI");
        location.setLatitude(coords.get(0));
        location.setLongitude(coords.get(1));
        return location;
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(Arrays.asList(location.getLongitude(), location.getLatitude()));
    }
}
