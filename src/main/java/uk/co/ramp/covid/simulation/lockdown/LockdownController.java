package uk.co.ramp.covid.simulation.lockdown;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Nursery;
import uk.co.ramp.covid.simulation.place.School;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LockdownController {
    private boolean schoolLockdown = false;
    
    private double socialDist = 1.0;
    private boolean inLockdown = false;
    
    private final Population population;
    
    private List<LockdownComponent> components;

    public LockdownController(Population p) {
        population = p;
        components = new ArrayList<>();
    }
    
    public LockdownController addComponent(LockdownComponent c) {
        components.add(c);
        return this;
    }
    
    public void setLockdown(Time start, Time end, double sdist) {
        addComponent(new FullLockdownComponent(start, end, population, sdist));
    }
    
    public void setSchoolLockdown(Time start, Time end, Time schoolsReturn, double sdist) {
        addComponent(new FullLockdownComponent(start, end, population, sdist));
        addComponent(new SchoolReturnComponent(schoolsReturn, end, population));
    }

    public void implementLockdown(Time now) {
        List<LockdownComponent> removeable = new ArrayList<>();
        for (LockdownComponent c : components) {
            c.handleLockdown(now);
            
            if (c.getEndTime().equals(now)) {
                removeable.add(c);
            }
        }
        components.removeAll(removeable);
    }

    public boolean inLockdown(Time t) {
       return inLockdown;
    }
}
