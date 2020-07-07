package uk.co.ramp.covid.simulation.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

public class PolymorphicTypeDeserialiser<T> implements JsonDeserializer<T> {

    private Map<String, Class<?>> components;
    private Gson gson; // to handle recursive parse

    public PolymorphicTypeDeserialiser(Map<String, Class<?>> buildable) {
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

}
