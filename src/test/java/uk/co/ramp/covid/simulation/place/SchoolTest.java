package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
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
        RNG.seed(123);
        CommunalPlace place = new CommunalPlace(0);
        School school = new School(0);
        double expProb = place.transProb * 30d / (34000d / 50d);
        double delta = 0.01;
        assertEquals("Unexpected school TransProb", expProb, school.transProb, delta);
    }

}