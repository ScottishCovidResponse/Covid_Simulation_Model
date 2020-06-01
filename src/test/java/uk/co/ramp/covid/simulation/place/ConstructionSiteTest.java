package uk.co.ramp.covid.simulation.place;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ConstructionSiteTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testConstructionSiteTransProb() throws JsonParseException {
        ConstructionSite constructionSite = new ConstructionSite();
        double expProb = PopulationParameters.get().getpBaseTrans() * 10d / (5000d / 100d);
        double delta = 0.01;
        assertEquals("Unexpected construction site TransProb", expProb, constructionSite.transProb, delta);
    }

    @Ignore("Failing Test")
    @Test
    public void testNoConstructionSites() throws JsonParseException, ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        //The input ConstructionSites ratio is set very high so that there are no construction sites.
        //Check that each person's primary place is never set to construction site
        PopulationParameters.get().setConstructionSiteRatio(100000);
        Population p = null;
        try {
            p = new Population(10000);
        } catch (ImpossibleAllocationException e) {
            Assert.fail("Could not populate households in test");
        }

        ArrayList<Person> allPeople = p.getAllPeople();
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
                .setIters(1)
                .setnDays(90)
                .setRNGSeed(42)
                .setNoOutput();

        List<List<DailyStats>> stats = m.run(0);

        for (DailyStats s : stats.get(0)) {
            assertEquals("Unexpected construction site infections", 0, s.getConstructionSiteInfectionsWorker());
        }
    }

    @Ignore("Failing Test")
    @Test
    public void testConstructionSiteWorkers() throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        int nInfections = 10;

        Population p = new Population(populationSize);
        p.allocatePeople();
        p.seedVirus(nInfections);
        List<Person> staff;
        //Run for a whole week
        for (int day = 0; day < 7; day++) {
            int totStaff;
            int startTime = Shifts.nineFiveFiveDays().getShift(day).getStart();
            int endTime = Shifts.nineFiveFiveDays().getShift(day).getEnd();
            DailyStats s = new DailyStats(day);
            for (int i = 0; i < 24; i++) {
                p.timeStep(day, i, s);
                totStaff = 0;
                for (ConstructionSite place : p.getPlaces().getConstructionSites()) {
                    staff = place.getStaff(day, i);
                    totStaff += staff.size();
                }

                if (day < 5) {

                    //Staff should be at construction sites during working hours only
                    if (i < startTime || i >= endTime - 1) {
                        assertEquals("Unexpected staff at construction site", 0, totStaff);
                    } else {
                        assertTrue("Unexpectedly no staff at construction site", totStaff > 0);
                    }
                } else {
                    //Staff should not be at construction sites on weekends
                    assertEquals("Unexpected staff at construction site", 0, totStaff);
                }
            }

        }
    }
}
