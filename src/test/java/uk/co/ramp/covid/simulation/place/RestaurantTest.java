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

public class RestaurantTest {

    Restaurant restaurant;
    Person p1;
    Person p2;

    @Before
    public void initialise() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");

        RNG.seed(123);
        //Setup a restaurant with 2 people
        restaurant = new Restaurant(0);
        p1 = new Adult();
        p2 = new Pensioner();
        restaurant.listPeople.add(p1);
        restaurant.listPeople.add(p2);
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
        personList.add(new Child());
        restaurant.shoppingTrip(personList);
        int expPeople = 3;
        assertEquals("Unexpected number of people in restaurant", expPeople, restaurant.listPeople.size());
    }

    @Test
    public void testSendHome() {
        int time = restaurant.endTime - 1;
        ArrayList<Person> personList = restaurant.sendHome(time);
        int expPeople = 2;
        assertEquals("Unexpected number of people sent home from restaurant", expPeople, personList.size());
    }
}