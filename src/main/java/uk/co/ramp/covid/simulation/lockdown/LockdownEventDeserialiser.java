package uk.co.ramp.covid.simulation.lockdown;

import com.google.gson.*;
import uk.co.ramp.covid.simulation.lockdown.easingevents.*;
import uk.co.ramp.covid.simulation.place.Shop;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LockdownEventDeserialiser implements JsonDeserializer<LockdownEvent> {
    
    private Map<String, Class<?>> components;
    private Gson gson; // to handle recursive parse
    
    public LockdownEventDeserialiser() {
        components = new HashMap<>();
        components.put("FullLockdown", FullLockdownEvent.class);
        components.put("SchoolEasing", SchoolEasingEvent.class);
        components.put("ConstructionSiteEasing", ConstructionSiteEasingEvent.class);
        components.put("OfficeEasing", OfficeEasingEvent.class);
        components.put("ShopEasing", ShopEasingEvent.class);
        components.put("RestaurantEasing", RestaurantEasingEvent.class);
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public LockdownEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject comp = json.getAsJsonObject();
        JsonElement compTypeE = comp.get("type");
        String compType = compTypeE.getAsString();

        // TODO: Error handling if the component doesn't exist
        Class<?> type = components.get(compType);
        
        return gson.fromJson(comp, (Type) type);
    }
}
