package uk.co.ramp.covid.simulation.parameters;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CovidParametersTest {

    @Test
    public void testIsValid() throws IOException {
        ParameterIO.readParametersFromFile("src/test/resources/test_params.json");

        //Test that valid parameters are handled correctly
        assertTrue(CovidParameters.get().isValid());

        //Test that invalid parameters are handled correctly
        CovidParameters params = new CovidParameters();
        assertFalse(params.isValid());
    }

}
