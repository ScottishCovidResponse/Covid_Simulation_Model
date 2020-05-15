package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class NurseryTest {

    @Test
    public void testNurseryTransProb() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("parameters/example_params.json");
        new RunModel(123);
        CommunalPlace place = new CommunalPlace(0);
        Nursery nursery = new Nursery(0);
        double expProb = place.transProb * 30d / (34000d / 50d);
        double delta = 0.01;
        assertEquals("Unexpected nursery TransProb", expProb, nursery.transProb, delta);
    }
}