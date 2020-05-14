package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class ConstructionSiteTest {

    @Test
    public void testConstructionSiteTransProb() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("parameters/example_params.json");
        new RunModel(123);
        CommunalPlace place = new CommunalPlace(0);
        ConstructionSite constructionSite = new ConstructionSite(0);
        double expProb = place.transProb * 10d / (5000d / 100d);
        double delta = 0.01;
        assertEquals("Unexpected construction site TransProb", expProb, constructionSite.transProb, delta);
    }
}