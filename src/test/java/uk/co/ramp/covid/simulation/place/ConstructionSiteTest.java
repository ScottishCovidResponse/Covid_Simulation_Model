package uk.co.ramp.covid.simulation.place;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class ConstructionSiteTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testConstructionSiteTransProb() throws JsonParseException {
        RNG.seed(123);
        ConstructionSite constructionSite = new ConstructionSite();
        double expProb = PopulationParameters.get().getpBaseTrans() * 10d / (5000d / 100d);
        double delta = 0.01;
        assertEquals("Unexpected construction site TransProb", expProb, constructionSite.transProb, delta);
    }

    @Test
    public void testNoConstructionSites() throws JsonParseException {
        //The input ConstructionSites ratio is set very high so that there are no construction sites.
        //Check that each person's primary place is never set to construction site
        PopulationParameters.get().setConstructionSiteRatio(100000);
        Population p = new Population(10000,1000);
        try {
            p.populateHouseholds();
        } catch (ImpossibleAllocationException e) {
            Assert.fail("Could not populate households in test");
        }
        p.createMixing();

        Person[] allPeople = p.getAllPeople();
        for (Person allPerson : allPeople) {
            allPerson.allocateCommunalPlace(p.getPlaces());
            assertFalse("Primary communal place cannot be a construction site", allPerson.getPrimaryCommunalPlace() instanceof ConstructionSite);
        }
    }

    @Test
    public void testNoCSInfections() throws JsonParseException {
        //The input ConstructionSites ratio is set very high so that there are no construction sites.
        //Check that there are no infections on construction sites
        PopulationParameters.get().setConstructionSiteRatio(100000);
        int population = 10000;
        int nInfections = 100;

        Model m = new Model()
                .setPopulationSize(population)
                .setnInfections(nInfections)
                .setnHouseholds(3000)
                .setIters(1)
                .setnDays(90)
                .setRNGSeed(42)
                .setNoOutput();

        List<List<DailyStats>> stats = m.run();

        for (DailyStats s : stats.get(0)) {
            assertEquals("Unexpected construction site infections", 0, s.getConstructionSiteInfections());
        }
    }

}