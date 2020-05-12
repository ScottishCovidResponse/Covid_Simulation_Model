package uk.co.ramp.covid.simulation.place;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.population.Adult;
import uk.co.ramp.covid.simulation.population.Child;
import uk.co.ramp.covid.simulation.population.Pensioner;
import uk.co.ramp.covid.simulation.population.Person;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ShopTest {

    Shop shop;
    Person p1;
    Person p2;

    @Before
    public void initialise() {
        //Setup a shop with 2 people
        shop = new Shop(0);
        p1 = new Adult();
        p2 = new Pensioner();
        shop.listPeople.add(p1);
        shop.listPeople.add(p2);
    }

    @Test
    public void testShopTransProb() {
        CommunalPlace place = new CommunalPlace(0);
        double expProb = place.transProb * 5d / (5000d / 200d);
        double delta = 0.01;
        assertEquals("Unexpected shop TransProb", expProb, shop.transProb, delta);
    }

    @Test
    public void testShoppingTrip() {

        ArrayList<Person> personList = new ArrayList<>();
        personList.add(new Child());
        shop.shoppingTrip(personList);
        int expPeople = 3;

        assertEquals("Unexpected number of people in shop", expPeople, shop.listPeople.size());
    }

    @Test
    public void testSendHome() {
        int time = shop.endTime - 1;
        ArrayList<Person> personList = shop.sendHome(time);
        int expPeople = 2;

        assertEquals("Unexpected number of people sent home", expPeople, personList.size());
    }
}