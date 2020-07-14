package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.lockdown.easingevents.*;

import java.util.HashMap;
import java.util.Map;

public class LockdownTypeMaps {

    public static Map<String, Class<?>> getLockdownEventMap() {
        Map<String, Class<?>> components = new HashMap<>();
        components.put("FullLockdown", FullLockdownEvent.class);
        components.put("FullLockdownEasing", FullLockdownEasingEvent.class);
        components.put("SchoolEasing", SchoolEasingEvent.class);
        components.put("ConstructionSiteEasing", ConstructionSiteEasingEvent.class);
        components.put("OfficeEasing", OfficeEasingEvent.class);
        components.put("ShopEasing", ShopEasingEvent.class);
        components.put("RestaurantEasing", RestaurantEasingEvent.class);
        components.put("HouseholdEasing", HouseholdEasingEvent.class);
        components.put("TravelEasing", TravelEasingEvent.class);
        components.put("ShieldingEasing", ShieldingEasingEvent.class);
        return components;
    }

    public static Map<String, Class<?>> getLockdownEventGeneratorMap() {
        Map<String, Class<?>> components = new HashMap<>();
        components.put("LocalLockdownEventGenerator", LocalLockdownEventGenerator.class);
        return components;
    }
}
