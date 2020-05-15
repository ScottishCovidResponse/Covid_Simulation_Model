package uk.co.ramp.covid.simulation.place;

import org.junit.Test;
import uk.co.ramp.covid.simulation.RunModel;

import static org.junit.Assert.assertEquals;

public class SchoolTest {

    @Test
    public void testSchoolTransProb() {
        RunModel runModel = new RunModel(123);
        CommunalPlace place = new CommunalPlace(0);
        School school = new School(0);
        double expProb = place.transProb * 30d / (34000d / 50d);
        double delta = 0.01;
        assertEquals("Unexpected school TransProb", expProb, school.transProb, delta);
    }

}