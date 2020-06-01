package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestaurantTest {

    Restaurant restaurant;
    Person p1;
    Person p2;

    @Before
    public void initialise() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");

        //Setup a restaurant with 2 people
        restaurant = new Restaurant();
        p1 = new Adult(30, Person.Sex.MALE);
        p2 = new Pensioner(67, Person.Sex.FEMALE);
        Household h1 = new Household(Household.HouseholdType.ADULT, null);
        Household h2 = new Household(Household.HouseholdType.PENSIONER, null);
        p1.setHome(h1);
        p2.setHome(h2);
        restaurant.people.add(p1);
        restaurant.people.add(p2);
    }

    @Test
    public void testRestaurantTransProb() {
        double expProb = PopulationParameters.get().getpBaseTrans() * 5d / (5000d / 1000d);
        double delta = 0.01;
        assertEquals("Unexpected restaurant TransProb", expProb, restaurant.transProb, delta);
    }

    @Test
    public void testShoppingTrip() {
        ArrayList<Person> personList = new ArrayList<>();
        personList.add(new Child(6, Person.Sex.MALE));
        restaurant.shoppingTrip(personList);
        int expPeople = 3;
        assertEquals("Unexpected number of people in restaurant", expPeople, restaurant.people.size());
    }

    @Test
    public void testSendHome() {
        PopulationParameters.get().setpLeaveRestaurant(1.0);
        int time = restaurant.times.getClose() - 1;
        int left = restaurant.sendHome(time, 0);
        int expPeople = 2;
        assertEquals("Unexpected number of people sent home from restaurant", expPeople, left);
    }

    @Ignore("Failing Test")
    @Test
    public void testRestaurantWorkers() throws ImpossibleAllocationException {
        int populationSize = 10000;
        int nHouseholds = 2000;
        int nInfections = 10;

        Population p = new Population(populationSize, nHouseholds);
        p.populateHouseholds();
        p.createMixing();
        p.allocatePeople();
        p.seedVirus(nInfections);
        List<Person> staff;
        //Run for a whole week
        for (int day = 0; day < 7; day++) {
            DailyStats s = new DailyStats(day);
            for (int i = 0; i < 24; i++) {
                p.timeStep(day, i, s);
                for (Restaurant place : p.getPlaces().getRestaurants()) {
                    staff = place.getStaff(day, i);
                    int open = place.getShifts().getShift(day).getStart();
                    int close = place.getShifts().getShift(day).getEnd();
                    if (i < open || i >= close - 1) {
                        assertEquals("Day "+day+" time "+ i + " Unexpected staff at restaurant", 0, staff.size());
                    } else {
                        assertTrue("Day "+day+" time "+ i + " Unexpectedly no staff at restaurant", staff.size() > 0);
                    }
                }
            }
        }
    }

}