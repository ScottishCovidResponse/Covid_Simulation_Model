package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.householdtypes.SmallFamily;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestaurantTest extends SimulationTest {

    Restaurant restaurant;
    Person p1;
    Person p2;

    @Before
    public void initialise() throws JsonParseException {
        //Setup a restaurant with 2 people
        restaurant = new Restaurant(CommunalPlace.Size.MED);
        p1 = new Adult(30, Person.Sex.MALE);
        p2 = new Pensioner(67, Person.Sex.FEMALE);
        Household h1 = new SmallFamily();
        Household h2 = new SmallFamily();
        p1.setHome(h1);
        p2.setHome(h2);
        restaurant.addPerson(p1);
        restaurant.addPerson(p2);
    }

    @Test
    public void testRestaurantTransProb() throws JsonParseException {
        Restaurant restaurant = new Restaurant(CommunalPlace.Size.MED);
        double expProb = PopulationParameters.get().buildingProperties.baseTransmissionConstant;
        double delta = 0.01;
        assertEquals("Unexpected restaurant TransProb", expProb, restaurant.transConstant, delta);
    }

    @Test
    public void testSendHome() {
        PopulationParameters.get().buildingProperties.pLeaveRestaurant = new Probability(1.0);
        int time = restaurant.times.getClose() - 1;
        restaurant.determineMovement(new Time(time), new DailyStats(new Time(time)), false, null);
        restaurant.commitMovement();
        int expPeople = 0;
        assertEquals("Unexpected people left in restaurant", expPeople, restaurant.getNumPeople());
    }

    @Test
    public void testRestaurantWorkers() throws ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        Population p = PopulationGenerator.genValidPopulation(populationSize);
        p.allocatePeople();

        p.setPostHourHook((pop, time) -> {
            for (Restaurant place : p.getPlaces().getRestaurants()) {
                List<Person> staff = place.getStaff(time);
                int open = place.times.getOpen();
                int close = place.times.getClose();
                if (time.getHour() < open || time.getHour() >= close) {
                    assertEquals("Day " + time.getDay() +" time "+ time.getHour() + " Unexpected staff at restaurant",
                            0, staff.size());
                } else {
                    assertTrue("Day " + time.getDay() +" time "+ time.getHour() + " Unexpectedly no staff at restaurant",
                            staff.size() > 0);
                }
            }
        });
        
        p.simulate(7);
    }
}
