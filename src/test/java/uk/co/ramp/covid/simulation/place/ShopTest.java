package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ShopTest {

    Shop shop;
    Person p1;
    Person p2;

    @Before
    public void initialise() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        RNG.seed(123);
        //Setup a shop with 2 people
        shop = new Shop();
        p1 = new Adult();
        p2 = new Pensioner();
        Household h1 = new Household(Household.HouseholdType.ADULT);
        Household h2 = new Household(Household.HouseholdType.PENSIONER);
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
        personList.add(new Child());
        shop.shoppingTrip(personList);
        int expPeople = 3;
        assertEquals("Unexpected number of people in shop", expPeople, shop.people.size());
    }

    @Test
    public void testSendHome() {
        int time = shop.times.getClose() - 1;
        int left = shop.sendHome(time, 0);
        int expPeople = 2;
        assertEquals("Unexpected number of people sent home", expPeople, left);
    }
}