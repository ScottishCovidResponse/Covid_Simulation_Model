package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.population.Adult;
import uk.co.ramp.covid.simulation.population.Child;
import uk.co.ramp.covid.simulation.population.Pensioner;
import uk.co.ramp.covid.simulation.population.Person;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class RestaurantTest {

    Restaurant restaurant;
    Person p1;
    Person p2;

    @Before
    public void initialise() {
        RunModel runModel = new RunModel(123);
        //Setup a restaurant with 2 people
        restaurant = new Restaurant(0);
        p1 = new Adult();
        p2 = new Pensioner();
        restaurant.listPeople.add(p1);
        restaurant.listPeople.add(p2);
    }

    @Test
    public void testRestaurantTransProb() {
        CommunalPlace place = new CommunalPlace(0);
        double expProb = place.transProb * 5d / (5000d / 1000d);
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