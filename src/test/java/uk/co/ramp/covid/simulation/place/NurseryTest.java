package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.util.RNG;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class NurseryTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testNurseryTransProb() throws JsonParseException, IOException {
        RNG.seed(123);
        Nursery nursery = new Nursery(0);
        double expProb = PopulationParameters.get().getpBaseTrans() * 30d / (34000d / 50d);
        double delta = 0.01;
        assertEquals("Unexpected nursery TransProb", expProb, nursery.transProb, delta);
    }
}