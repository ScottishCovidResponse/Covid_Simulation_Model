package uk.co.ramp.covid.simulation.integrationTests;

import org.junit.Ignore;
import org.junit.Test;
import uk.co.ramp.covid.simulation.lockdown.FullLockdownEvent;
import uk.co.ramp.covid.simulation.lockdown.easingevents.HouseholdEasingEvent;
import uk.co.ramp.covid.simulation.lockdown.easingevents.RestaurantEasingEvent;
import uk.co.ramp.covid.simulation.lockdown.easingevents.SchoolEasingEvent;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.population.Child;
import uk.co.ramp.covid.simulation.place.Hospital;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

import static org.junit.Assert.*;

public class LockdownTests extends SimulationTest  {
    
    @Test
    public void furloughedStaffDontGoToWork() {
        final int simDays = 7;
        final int populationSize = 20000;
       
        Population pop = PopulationGenerator.genValidPopulation(populationSize);
        pop.getLockdownController().addComponent(new FullLockdownEvent(Time.timeFromDay(2), pop, 0.5));

        pop.setPostHourHook((population, time) -> {
            // Skip the first hour lockdown starts since people might still be at
            // work as they get the message they need to leave
            if (time.getDay() == 2 && time.getHour() == 1) {
                return;
            }

            for (Hospital plc : population.getPlaces().getNonCovidHospitals()) {
                for (Person p : plc.getPeople()) {
                    if (p.getPrimaryCommunalPlace() == plc
                            && !p.isHospitalised()
                            && !(p.hasHospitalAppt() && p.getHospitalAppt().isOccurring(time))) {
                            assertFalse(p.isFurloughed());
                    }
                }
            }
        });

        pop.simulate(simDays);
    }

    @Test
    public void schoolEasingReopensSchools() {
        int populationSize = 20000;
        Population pop = PopulationGenerator.genValidPopulation(populationSize);
        pop.getLockdownController().addComponent(new FullLockdownEvent(Time.timeFromDay(0), pop, 0.5));
        pop.getLockdownController().addComponent(new SchoolEasingEvent(
                Time.timeFromDay(2), pop, new Probability(1.0), 1.0, new Probability(0.5)
        ));

        pop.setPostHourHook((population, time) -> {
            if (time.getDay() < 2) {
                for (School s : population.getPlaces().getSchools()) {
                    assertTrue(s.isClosed());
                }
            } else {
                // Since this happens after time is advanced we need to
                // skip the first check of day 2 to let lockdown easing happen first
                if (time.getDay() == 2 && time.getHour() == 0) {
                    return;
                }

                int children = 0;
                for (School s : population.getPlaces().getSchools()) {
                    assertFalse(s.isClosed());
                    for (Person p : s.getPeople()) {
                        if (p instanceof Child) {
                            assertFalse(p.isFurloughed());
                            children++;
                        }
                    }
                }
                
                long totalChildren = population.getAllPeople().stream().filter(p -> p instanceof Child).count();

                // 0 children if the school isn't open this hour
                if (children > 0) {
                    assertEquals(totalChildren / 2, children, totalChildren * 0.1);
                }
            }
        });

        pop.simulate(5);

    }

    @Test
    public void restaurantEasingReopensRestaurants() {
        // Make it likely we would go to a restaurant in normal circumstances
        PopulationParameters.get().householdProperties.pGoRestaurant = new Probability(1.0);

        int populationSize = 20000;
        Population pop = PopulationGenerator.genValidPopulation(populationSize);
        pop.getLockdownController().addComponent(new FullLockdownEvent(Time.timeFromDay(0), pop, 0.5));
        pop.getLockdownController().addComponent(new RestaurantEasingEvent(Time.timeFromDay(2), pop, 
                new Probability(1.0), 1.0, 1.0));

        final int[] visitors = {0};
        pop.setPostHourHook((population, time) -> {
            if (time.getDay() < 2) {
                for (Restaurant r : population.getPlaces().getRestaurants()) {
                    assertTrue(r.isClosed());
                }
            } else {
                // Since this happens after time is advanced we need to
                // skip the first check of day 2 to let lockdown easing happen first
                if (time.getDay() == 2 && time.getHour() == 0) {
                    return;
                }
                
                for (Restaurant r : population.getPlaces().getRestaurants()) {
                    assertFalse(r.isClosed());
                    for (Person p : r.getPeople()) {
                        if (!p.isWorking(r, time)) {
                            visitors[0]++;
                        }
                    }
                }
            }
        });
        pop.simulate(5);

        assertNotEquals(0, visitors[0]);
    }

    @Test
    public void householdEasingAllowsNeighbourVisits() {
        // Everyone respects the lockdown
        PopulationParameters.get().householdProperties.pLockCompliance = new Probability(1.0);

        int populationSize = 20000;
        Population pop = PopulationGenerator.genValidPopulation(populationSize);
        pop.getLockdownController().addComponent(new FullLockdownEvent(Time.timeFromDay(0), pop, 0.5));
        pop.getLockdownController().addComponent(new HouseholdEasingEvent(Time.timeFromDay(2), pop, 1.0));

        final int[] visitorsDuringLockdown = {0};
        final int[] visitorsAfterLockdown = {0};
        pop.setPostHourHook((population, time) -> {
            if (time.getDay() < 2) {
                for (Household h : population.getHouseholds()) {
                    visitorsDuringLockdown[0] += h.getVisitors().size();
                }
            } else {
                // Since this happens after time is advanced we need to
                // skip the first check of day 2 to let lockdown easing happen first
                if (time.getDay() == 2 && time.getHour() == 0) {
                    return;
                }

                for (Household h : population.getHouseholds()) {
                    visitorsAfterLockdown[0] += h.getVisitors().size();
                }
            }
        });
        pop.simulate(5);

        assertEquals(0, visitorsDuringLockdown[0]);
        assertNotEquals(0, visitorsAfterLockdown[0]);
    }
    
}
