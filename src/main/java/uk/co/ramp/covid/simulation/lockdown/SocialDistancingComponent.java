package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.place.Place;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

/** Lockdown component that only increases the social distancing in places */
public class SocialDistancingComponent extends LockdownComponent {
    private double lockdownSocialDistance;
    private double previousSocialDistance;
    
    public SocialDistancingComponent(Time start, Time end, Population p,
                                     double lockdownSocialDistance, double previousSocialDistance) {
        super(start, end, p);
        this.lockdownSocialDistance = lockdownSocialDistance;
        this.previousSocialDistance = previousSocialDistance;
    }

    @Override
    protected void start() {
        for (Place p : population.getPlaces().getAllPlaces()) {
            p.setSocialDistancing(lockdownSocialDistance);
        }
    }

    @Override
    protected void end() {
        for (Place p : population.getPlaces().getAllPlaces()) {
            p.setSocialDistancing(previousSocialDistance);
        }
    }

    @Override
    protected void tick(Time t) {

    }

    @Override
    protected String getName() {
        return "Social Distancing";
    }

}
