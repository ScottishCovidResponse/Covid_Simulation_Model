package uk.co.ramp.covid.simulation.place;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConstructionSiteTest {

    @Test
    public void testConstructionSiteTransProb() {
        CommunalPlace place = new CommunalPlace(0);
        ConstructionSite constructionSite = new ConstructionSite(0);
        double expProb = place.transProb * 10d / (5000d / 100d);
        double delta = 0.01;
        assertEquals("Unexpected construction site TransProb", expProb, constructionSite.transProb, delta);
    }
}