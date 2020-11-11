package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.RNG;

import static org.junit.Assert.assertEquals;

public class PlaceTest extends SimulationTest {

    @Test
    public void testNextBinomial() {
        double p = 0.5;
        long numberOfTrials = (long)Integer.MAX_VALUE + 1;
        long expected = numberOfTrials / 2;
        int delta = 100000;

        int result = Place.nextBinomial(RNG.get(), numberOfTrials, p);
        assertEquals(expected, result, delta);
    }

}
