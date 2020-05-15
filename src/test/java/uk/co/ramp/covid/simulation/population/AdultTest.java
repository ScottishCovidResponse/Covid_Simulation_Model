package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class AdultTest {

    @Test
    public void testSetProfession() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("parameters/example_params.json");
        new RunModel(123);
        Adult adult = new Adult();
        adult.setProfession();
        assertTrue("Unexpected adult profession", adult.isShopWorker());
    }
}