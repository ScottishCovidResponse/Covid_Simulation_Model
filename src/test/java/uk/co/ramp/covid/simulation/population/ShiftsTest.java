package uk.co.ramp.covid.simulation.population;


import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Hospital;
import uk.co.ramp.covid.simulation.place.Shop;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import static org.junit.Assert.assertEquals;

public class ShiftsTest extends SimulationTest {

    @Test
    public void testGetEndShift() throws JsonParseException {
       Shop shop = new Shop(CommunalPlace.Size.LARGE);
       int end = shop.getShifts().getShift(1).getEnd();
       assertEquals(15, end);
       end = shop.getShifts().getShift(1).getEnd();
       assertEquals(22, end);
    }

    @Test
    public void testGetStartShift() throws JsonParseException {
        Hospital hospital = new Hospital(CommunalPlace.Size.LARGE);
        int start = hospital.getShifts().getShift(2).getStart();
        assertEquals(0, start);
        start = hospital.getShifts().getShift(2).getStart();
        assertEquals(12, start);
    }
}