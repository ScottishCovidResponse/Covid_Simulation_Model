package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.householdtypes.SmallFamily;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ShopTest extends SimulationTest {

    Shop shop;
    Person p1;
    Person p2;

    @Before
    public void initialise() throws JsonParseException {
        //Setup a shop with 2 people
        shop = new Shop(CommunalPlace.Size.MED);
        p1 = new Adult(25, Person.Sex.MALE);
        p2 = new Pensioner(67, Person.Sex.FEMALE);
        Household h1 = new SmallFamily();
        Household h2 = new SmallFamily();
        p1.setHome(h1);
        p2.setHome(h2);
        shop.addPerson(p1);
        shop.addPerson(p2);
    }

    @Test
    public void testShopTransProb() {
        double expProb = PopulationParameters.get().buildingProperties.baseTransmissionConstant;
        double delta = 0.01;
        assertEquals("Unexpected shop TransProb", expProb, shop.transConstant, delta);
    }

    @Test
    public void testSendHome() {
        PopulationParameters.get().buildingProperties.pLeaveShopHour = new Probability(1.0);
        int time = shop.getOpeningTimes().getClose() - 1;
        shop.determineMovement(new Time(time), new DailyStats(new Time(time)), null);
        shop.commitMovement();
        int expPeople = 0;
        assertEquals("Unexpected people left in shop", expPeople, shop.getNumPeople());
    }

    @Test
    public void testShopWorkers() throws ImpossibleWorkerDistributionException {
        int populationSize = 12000;
        Population p = PopulationGenerator.genValidPopulation(populationSize);
        p.allocatePeople();
        
        p.setPostHourHook((pop, time) -> {
            for (Shop place : pop.getPlaces().getShops()) {
                List<Person> staff = place.getStaff(time);
                int open = place.getOpeningTimes().getOpen();
                int close = place.getOpeningTimes().getClose();
                if (time.getHour() < open || time.getHour() >= close) {
                    assertEquals("Day "+ time.getDay() +" time "+ time.getHour() + " Unexpected staff at shop",
                            0, staff.size());
                } else {
                    assertTrue("Day "+ time.getDay() +" time "+ time.getHour() + " No staff at shop",
                            staff.size() > 0);
                }
            }
        });
        
        p.simulate(7);
    }



}
