package com.tonyjhuang.tsunami.api.models.parsers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by tonyjhuang on 8/17/14.
 */
public class EnumGsonAdapter <E extends Enum> implements JsonDeserializer<E>, JsonSerializer<E> {
    private Class<E> enumClass;

    public static <E extends Enum> EnumGsonAdapter<E> getInstance(Class<E> enumClass) {
        return new EnumGsonAdapter<E>(enumClass);
    }

    public EnumGsonAdapter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public E deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        return (E) Enum.valueOf(enumClass, jsonElement.getAsString().toUpperCase());
    }

    @Override
    public JsonElement serialize(E e, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(e.toString().toLowerCase());
    }
}