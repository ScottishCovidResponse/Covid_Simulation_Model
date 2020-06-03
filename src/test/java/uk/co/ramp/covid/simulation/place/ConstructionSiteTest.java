package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.util.SimulationTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ConstructionSiteTest extends SimulationTest {

    @Test
    public void testConstructionSiteTransProb() throws JsonParseException {
        ConstructionSite constructionSite = new ConstructionSite(CommunalPlace.Size.MED);
        double expProb = PopulationParameters.get().getpBaseTrans();
        double delta = 0.01;
        assertEquals("Unexpected construction site TransProb", expProb, constructionSite.transProb, delta);
    }

    @Test
    public void testNoConstructionSites() throws JsonParseException, ImpossibleWorkerDistributionException {
        //The input ConstructionSites ratio is set very high so that there are no construction sites.
        //Check that each person's primary place is never set to construction site
        PopulationParameters.get().setConstructionSiteRatio(100000);
        Population p = PopulationGenerator.genValidPopulation(10000);

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

    @Test
    public void testConstructionSiteWorkers() throws ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        int nInfections = 10;

        Population p = PopulationGenerator.genValidPopulation(populationSize);
        p.allocatePeople();
        p.seedVirus(nInfections);
        List<Person> staff;
        Time t = new Time(0);
        //Run for a whole week
        for (int day = 0; day < 7; day++) {
            int totStaff;
            int startTime = Shifts.nineFiveFiveDays().getShift(day).getStart();
            int endTime = Shifts.nineFiveFiveDays().getShift(day).getEnd();
            DailyStats s = new DailyStats(t);
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                t = t.advance();
                totStaff = 0;
                for (ConstructionSite place : p.getPlaces().getConstructionSites()) {
                    staff = place.getStaff(t);
                    totStaff += staff.size();
                }

                if (day < 5) {

                    //Staff should be at construction sites during working hours only
                    if (i + 1 < startTime || i + 1 >= endTime) {
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
