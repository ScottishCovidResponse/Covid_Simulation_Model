package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.util.RNG;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class ConstructionSiteTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testConstructionSiteTransProb() throws JsonParseException, IOException {
        ConstructionSite constructionSite = new ConstructionSite();
        double expProb = PopulationParameters.get().getpBaseTrans() * 10d / (5000d / 100d);
        double delta = 0.01;
    //    assertEquals("Unexpected construction site TransProb", expProb, constructionSite.transProb, delta);
    }
}