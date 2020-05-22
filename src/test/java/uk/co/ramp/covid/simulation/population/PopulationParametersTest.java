package uk.co.ramp.covid.simulation.population;

import org.junit.After;
import org.junit.Test;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PopulationParametersTest {

    @Test
    public void testIsValid() throws IOException {
        //Test that valid parameters are handled correctly
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        assertTrue(PopulationParameters.get().isValid());
    }

    @Test
    public void testIsInValid() throws IOException {
        //Test that invalid parameters are handled correctly
        ParameterReader.readParametersFromFile("src/test/resources/test_invalid_params.json");
        assertFalse(PopulationParameters.get().isValid());
    }

    @Test (expected = InvalidParametersException.class)
    public void testNullParameters() {
        //Test that null parameters throw an InvalidParametersException
        PopulationParameters.get().isValid();
    }

    @After
    public void clearParams() {
        PopulationParameters.clearParameters();
    }

}