package uk.co.ramp.covid.simulation.parameters;

import org.junit.After;
import org.junit.Test;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;

import java.io.IOException;

import static org.junit.Assert.*;

public class ParametersTest {

    @Test
    public void testIsValid() throws IOException {
        //Test that valid parameters are handled correctly
        ParameterIO.readParametersFromFile("parameters/example_population_params.json");
        assertTrue(PopulationParameters.get().isValid());
    }

    @Test
    public void testIsInValid() throws IOException {
        //Test that invalid parameters are handled correctly
        ParameterIO.readParametersFromFile("src/test/resources/test_invalid_params.json");
        assertFalse(PopulationParameters.get().isValid());
    }

    @Test (expected = InvalidParametersException.class)
    public void testNullParameters() {
        //Test that null parameters throw an InvalidParametersException
        PopulationParameters.get().isValid();
    }

    @Test
    public void serialisationAndDeSerialisationAreInverse() throws IOException {
        ParameterIO.readParametersFromFile("parameters/example_population_params.json");
        PopulationParameters pop = PopulationParameters.get();
        CovidParameters covid = CovidParameters.get();

        String s = ParameterIO.writeParametersToString();

        PopulationParameters.clearParameters();
        CovidParameters.clearParameters();

        ParameterIO.readParametersFromString(s);

        assertTrue(pop.equals(PopulationParameters.get()));
        assertTrue(covid.equals(CovidParameters.get()));

        // Model checks
        Model m = Model.readModelFromFile("parameters/example_model_params_lockdown.json");
        s = m.modelToJsonString();
        Model n = Model.readModelFromString(s);

        assertTrue(m.equals(n));
    }

    @After
    public void clearParams() {
        PopulationParameters.clearParameters();
    }

}