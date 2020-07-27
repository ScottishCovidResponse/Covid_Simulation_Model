package uk.co.ramp.covid.simulation.util;

import com.google.gson.*;
import uk.co.ramp.covid.simulation.parameters.ParameterIO;
import uk.co.ramp.covid.simulation.population.Shifts;

public class ShiftAllocator extends RoundRobinAllocator<Shifts> {

    public static JsonDeserializer<ShiftAllocator> deserializer = (json, typeOfT, context) -> {
        JsonArray shifts = json.getAsJsonArray();
        ShiftAllocator alloc = new ShiftAllocator();
        for (JsonElement e : shifts) {
            Shifts s = ParameterIO.getGson().fromJson(e, Shifts.class);
            alloc.put(s);
        }
        return alloc;
    };

    public ShiftAllocator() {
        super();
    }

    public ShiftAllocator(ShiftAllocator shifts) {
        super(shifts);
    }
}
