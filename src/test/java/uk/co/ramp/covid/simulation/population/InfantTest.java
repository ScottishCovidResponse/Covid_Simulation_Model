package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InfantTest {

    @Test
    public void testInfant() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        RNG.seed(100);
        int nNursery = 0;
        //Test 50% of infants go to nursery
        for (int i = 0; i < 1000; i++) {
            Infant infant = new Infant();
            if (infant.goesToNursery) nNursery++;
        }
        assertEquals("Unexpected number of infants at nursery", 500, nNursery, 10);
    }
}