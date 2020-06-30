package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LockdownComponentFactory {
    
    public class LockdownComponentFactoryInfo {
        String type;
        Integer start;
        Integer end;
        List<Object> args;
    }
    
    public LockdownComponent buildComponent(LockdownComponentFactoryInfo component, Population p)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {

        // Create the constructor params
        Time start = Time.timeFromDay(component.start);
        Time end = Time.timeFromDay(component.end);

        List<Object> vargs = new ArrayList<>();
        vargs.add(start);
        vargs.add(end);
        vargs.add(p);
        vargs.addAll(component.args);

        // TODO: support other lockdown types
        switch (component.type) {
            case "FullLockdown" : {
                Constructor<?> constructor = FullLockdownComponent.class.getDeclaredConstructors()[0];
                return (LockdownComponent) constructor.newInstance(vargs.toArray());
            }
            case "SocialDistancing": {
                Constructor<?> constructor = SocialDistancingComponent.class.getDeclaredConstructors()[0];
                return (LockdownComponent) constructor.newInstance(vargs.toArray());
            }
        }
        return null;
    }

    public List<LockdownComponent> buildComponents(List<LockdownComponentFactoryInfo> componentsToBuild, Population p) {
        List<LockdownComponent> res = new ArrayList<>();
        for (LockdownComponentFactoryInfo info : componentsToBuild) {
            LockdownComponent comp = null;
            try {
                comp = buildComponent(info, p);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
                // TODO: Throw an exception so we don't continue the run on errors
            }

            // TODO: Throw an exception if it's null since it means we didn't initialise components correctly
            if (comp != null) {
                res.add(comp);
            }
        }
        return res;
    }
    
}
