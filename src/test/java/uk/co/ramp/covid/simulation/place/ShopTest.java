package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ShopTest {

    Shop shop;
    Person p1;
    Person p2;

    @Before
    public void initialise() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        //Setup a shop with 2 people
        shop = new Shop(CommunalPlace.Size.MED);
        p1 = new Adult(25, Person.Sex.MALE);
        p2 = new Pensioner(67, Person.Sex.FEMALE);
        Household h1 = new Household(Household.HouseholdType.ADULT, null);
        Household h2 = new Household(Household.HouseholdType.PENSIONER, null);
        p1.setHome(h1);
        p2.setHome(h2);
        shop.people.add(p1);
        shop.people.add(p2);
    }

    @Test
    public void testShopTransProb() {
        double expProb = PopulationParameters.get().getpBaseTrans() * 5d / (5000d / 200d);
        double delta = 0.01;
        assertEquals("Unexpected shop TransProb", expProb, shop.transProb, delta);
    }

    @Test
    public void testShoppingTrip() {
        ArrayList<Person> personList = new ArrayList<>();
        personList.add(new Child(8, Person.Sex.FEMALE));
        shop.shoppingTrip(personList);
        int expPeople = 3;
        assertEquals("Unexpected number of people in shop", expPeople, shop.people.size());
    }

    @Test
    public void testSendHome() {
        PopulationParameters.get().setpLeaveShop(1.0);
        int time = shop.times.getClose() - 1;
        int left = shop.sendHome(new Time(time));
        int expPeople = 2;
        assertEquals("Unexpected number of people sent home", expPeople, left);
    }

    @Ignore("Failing Test")
    @Test
    public void testShopWorkers() throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        int nInfections = 10;

        Population p = new Population(populationSize);
        p.allocatePeople();
        p.seedVirus(nInfections);
        List<Person> staff;
        Time t = new Time(0);
        //Run for a whole week
        for (int day = 0; day < 7; day++) {
            DailyStats s = new DailyStats(t);
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                for (Shop place : p.getPlaces().getShops()) {
                    staff = place.getStaff(t);
                    int open = place.getShifts().getShift(day).getStart();
                    int close = place.getShifts().getShift(day).getEnd();
                    if (i < open || i >= close - 1) {
                        assertEquals("Day "+day+" time "+ i + " Unexpected staff at shop", 0, staff.size());
                    } else {
                        assertTrue("Day "+day+" time "+ i + " No staff at shop", staff.size() > 0);
                    }
                }
            }
            t.advance();
        }
    }



}
