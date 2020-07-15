package uk.co.ramp.covid.simulation.util;

import com.google.gson.*;
import com.google.common.collect.BiMap;

import java.lang.reflect.Type;

public class PolymorphicTypeDeserialiser<T> implements JsonDeserializer<T>, JsonSerializer<T> {

    private final BiMap<String, Class<?>> components;
    private Gson gson; // to handle recursive parse

    public PolymorphicTypeDeserialiser(BiMap<String, Class<?>> buildable) {
        components = buildable;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject comp = json.getAsJsonObject();
        JsonElement compTypeE = comp.get("type");
        String compType = compTypeE.getAsString();

        Class<?> type = components.get(compType);
        return gson.fromJson(comp, (Type) type);
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement rest = gson.toJsonTree(src);
        rest.getAsJsonObject().addProperty("type", components.inverse().get(src.getClass()));
        return rest;
    }
}
