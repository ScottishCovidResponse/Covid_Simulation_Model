package uk.co.ramp.covid.simulation.place;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NurseryTest {

    @Test
    public void testNurseryTransProb() {
        CommunalPlace place = new CommunalPlace(0);
        Nursery nursery = new Nursery(0);
        double expProb = place.transProb * 30d / (34000d / 50d);
        double delta = 0.01;
        assertEquals("Unexpected nursery TransProb", expProb, nursery.transProb, delta);
    }
}