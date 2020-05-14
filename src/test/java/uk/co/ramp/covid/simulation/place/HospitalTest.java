package uk.co.ramp.covid.simulation.place;

import org.junit.Test;
import uk.co.ramp.covid.simulation.RunModel;

import static org.junit.Assert.assertEquals;

public class HospitalTest {

    @Test
    public void testHospitalTransProb() {
        RunModel runModel = new RunModel(123);
        CommunalPlace place = new CommunalPlace(0);
        Hospital hospital = new Hospital(0);
        double expProb = place.transProb * 15d / (5000d / 10d);
        double delta = 0.01;
        assertEquals("Unexpected hospital TransProb", expProb, hospital.transProb, delta);
    }
}