package uk.co.ramp.covid.simulation.place;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SchoolTest {

    @Test
    public void testSchoolTransProb() {
        CommunalPlace place = new CommunalPlace(0);
        School school = new School(0);
        double expProb = place.transProb * 30d / (34000d / 50d);
        double delta = 0.01;
        assertEquals("Unexpected school TransProb", expProb, school.transProb, delta);
    }

}