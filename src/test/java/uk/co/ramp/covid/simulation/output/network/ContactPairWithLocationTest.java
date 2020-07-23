package uk.co.ramp.covid.simulation.output.network;


import org.junit.Test;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Shop;
import uk.co.ramp.covid.simulation.population.Adult;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import static org.junit.Assert.assertEquals;
import static uk.co.ramp.covid.simulation.population.Person.Sex.FEMALE;
import static uk.co.ramp.covid.simulation.population.Person.Sex.MALE;

public class ContactPairWithLocationTest extends SimulationTest {

    @Test
    public void testCompare() {

        Adult p1 = new Adult(30, FEMALE);
        Adult p2 = new Adult(40, MALE);
        Shop s1 = new Shop(CommunalPlace.Size.SMALL);
        ContactPairWithLocation cp1 = new ContactPairWithLocation(p1, p2, s1);

        Adult p3 = new Adult(30, FEMALE);
        Adult p4 = new Adult(40, MALE);
        Shop s2 = new Shop(CommunalPlace.Size.SMALL);
        ContactPairWithLocation cp2 = new ContactPairWithLocation(p3, p4, s2);

        ContactPairWithLocation cp3 = cp1;

        //These contact pairs are not comparable
        assertEquals(-1, cp1.compareTo(cp2));

        //These contact pairs are comparable
        assertEquals(0, cp3.compareTo(cp1));
    }
}