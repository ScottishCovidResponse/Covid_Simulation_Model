package uk.co.ramp.covid.simulation.lockdown;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import uk.co.ramp.covid.simulation.lockdown.easingevents.*;

public class LockdownTypeMaps {

    public static BiMap<String, Class<?>> getLockdownEventMap() {
        BiMap<String, Class<?>> components = HashBiMap.create();
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

    public static BiMap<String, Class<?>> getLockdownEventGeneratorMap() {
        BiMap<String, Class<?>> components = HashBiMap.create();
        components.put("LocalLockdownEventGenerator", LocalLockdownEventGenerator.class);
        return components;
    }
}
