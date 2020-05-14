package uk.co.ramp.covid.simulation.place;

import org.junit.Test;
import uk.co.ramp.covid.simulation.RunModel;

import static org.junit.Assert.assertEquals;

public class ConstructionSiteTest {

    @Test
    public void testConstructionSiteTransProb() {
        RunModel runModel = new RunModel(123);
        CommunalPlace place = new CommunalPlace(0);
        ConstructionSite constructionSite = new ConstructionSite(0);
        double expProb = place.transProb * 10d / (5000d / 100d);
        double delta = 0.01;
        assertEquals("Unexpected construction site TransProb", expProb, constructionSite.transProb, delta);
    }
}