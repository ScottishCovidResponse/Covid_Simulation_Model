package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.util.RNG;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class HospitalTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testHospitalTransProb() throws JsonParseException, IOException {
        RNG.seed(123);
        CommunalPlace place = new CommunalPlace(0);
        Hospital hospital = new Hospital(0);
        double expProb = place.transProb * 15d / (5000d / 10d);
        double delta = 0.01;
        assertEquals("Unexpected hospital TransProb", expProb, hospital.transProb, delta);
    }
}