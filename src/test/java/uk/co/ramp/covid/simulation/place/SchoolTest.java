package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SchoolTest {


    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testSchoolTransProb() {
        School school = new School();
        double expProb = PopulationParameters.get().getpBaseTrans() * 30d / (34000d / 50d);
        double delta = 0.01;
        assertEquals("Unexpected school TransProb", expProb, school.transProb, delta);
    }

}