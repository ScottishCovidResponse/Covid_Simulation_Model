package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

public class FullLockdownEvent extends LockdownEvent {
    
    private final Double socialDistance;

    public FullLockdownEvent(Time s, Population p, double socialDistance) {
        super(s, p);
        this.socialDistance = socialDistance;
    }

    @Override
    protected void apply() {
        for (CommunalPlace cPlace : population.getPlaces().getAllPlaces()) {
            cPlace.enterLockdown(socialDistance);
        }
        
        for (Person p : population.getAllPeople()) {
            p.furlough();
            p.setLockdownHospitalApptAdjustment(
                    PopulationParameters.get().hospitalApptProperties.lockdownApptDecreasePercentage);
        }

        for (Household h : population.getHouseholds()) {
            h.setLockdownShopVisitFrequencyAdjustment(
                    PopulationParameters.get().householdProperties.lockdownShoppingProbabilityAdjustment);
            h.setLockdownRestaurantVisitFrequencyAdjustment(0.0);
            // Note: Only applies to those that are lockdown compliant
            h.setLockdownNeighbourVisitFrequencyAdjustment(0.0);
        }

        // Log initial R if needed
        population.setShouldPrintR();

        // Travel restrictions during lockdown
        population.getSeeder().stopTravelSeeding();
    }


    @Override
    protected String getName() {
        return "Full Lockdown";
    }

    @Override
    protected boolean isValid() {
        return start != null && socialDistance != null;
    }
}
