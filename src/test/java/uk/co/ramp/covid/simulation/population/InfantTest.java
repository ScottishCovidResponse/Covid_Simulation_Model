package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;
import static org.junit.Assert.assertTrue;

public class InfantTest {

    @Test
    public void testInfant() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
//        new RunModel(100);
        RNG.seed(100);
        Infant infant = new Infant();
        assertTrue("Infant not at nursery", infant.isNursery());
    }
}