package uk.co.ramp.covid.simulation.testutil;

import java.io.IOException;

import org.junit.Before;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.util.RNG;
import uk.co.ramp.covid.simulation.parameters.ParameterIO;

/**
 * SimulationTest is a base class for setting up tests
 */
public class SimulationTest {
    @Before
    public void readDefaultParams() throws JsonParseException, IOException {
        ParameterIO.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Before
    public void seedRNG() {
        RNG.seed(0);
    }
}
