package uk.co.ramp.covid.simulation.population;


import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Shop;
import uk.co.ramp.covid.simulation.util.SimulationTest;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ShiftsTest extends SimulationTest {

    @Test
    public void testGetShift() throws JsonParseException, IOException {
       Shop shop = new Shop(CommunalPlace.Size.LARGE);
       int end = shop.getShifts().getShift(1).getEnd();
       assertEquals(15, end);
       end = shop.getShifts().getShift(1).getEnd();
       assertEquals(22, end);
    }
}