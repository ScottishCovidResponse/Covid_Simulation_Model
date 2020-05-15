package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class OfficeTest {

    @Test
    public void testOfficeTransProb() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("parameters/example_params.json");
        RunModel runModel = new RunModel(123);
        CommunalPlace place = new CommunalPlace(0);
        Office office = new Office(0);
        double expProb = place.transProb * 10d / (10000d / 400d);
        double delta = 0.01;
        assertEquals("Unexpected office TransProb", expProb, office.transProb, delta);
    }
}