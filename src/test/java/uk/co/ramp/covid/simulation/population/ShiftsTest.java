package uk.co.ramp.covid.simulation.population;


import com.google.gson.JsonParseException;
import org.junit.Ignore;
import org.junit.Test;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Shop;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ShiftsTest {

    @Test
    public void testGetShift() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");

       Shop shop = new Shop(CommunalPlace.Size.LARGE);
       int end = shop.getShifts().getShift(1).getEnd();
       assertEquals(15, end);
       end = shop.getShifts().getShift(1).getEnd();
       assertEquals(22, end);

    }
}