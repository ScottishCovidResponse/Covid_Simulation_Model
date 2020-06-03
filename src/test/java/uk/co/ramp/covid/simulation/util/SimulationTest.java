package uk.co.ramp.covid.simulation.util;

import java.io.IOException;

import org.junit.Before;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.io.ParameterReader;

/**
 * SimulationTest is a base class for setting up tests
 */
public class SimulationTest {
    @Before
    public void readDefaultParams() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Before
    public void seedRNG() {
        RNG.seed(0);
    }
}
