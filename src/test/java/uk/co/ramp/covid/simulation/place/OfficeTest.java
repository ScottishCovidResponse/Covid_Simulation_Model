package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OfficeTest {

    @Test
    public void testOfficeTransProb() {
        CommunalPlace place = new CommunalPlace(0);
        Office office = new Office(0);
        double expProb = place.transProb * 10d / (10000d / 400d);
        double delta = 0.01;
        assertEquals("Unexpected office TransProb", expProb, office.transProb, delta);
    }
}