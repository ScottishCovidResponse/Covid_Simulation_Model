package uk.co.ramp.covid.simulation.parameters;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.ParameterIO;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;

public class ExampleParametersTest {

    @Test
    public void testExampleParameterFiles() throws Exception {
        // This test is designed to ensure that if the list of input
        // parameters changes, then the example files are updated accordingly
       
        ParameterIO.readParametersFromFile("parameters/example_population_params.json");
        assertTrue(PopulationParameters.get().isValid());
        assertTrue(CovidParameters.get().isValid());

        Model m  = Model.readModelFromFile("parameters/example_model_params.json");
        assertTrue(m.isValid());
    }
}
