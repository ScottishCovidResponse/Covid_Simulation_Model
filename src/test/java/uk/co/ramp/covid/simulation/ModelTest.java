package uk.co.ramp.covid.simulation;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import uk.co.ramp.covid.simulation.io.ParameterReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModelTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testBaseLine() {
        int population = 10000;
        int nInfections = 10;

        Model m = new Model()
                .setPopulationSize(population)
                .setnInfections(nInfections)
                .setnHouseholds(3000)
                .setIters(1)
                .setnDays(90)
                .setRNGSeed(42)
                .setNoOutput();

        List<List<DailyStats>> stats = m.run();

        int lastTotalInfected = 10;
        for (DailyStats s : stats.get(0)) {
            assertEquals(10000, s.getTotalPopulation());

            // Check there are deaths, but not too many
            // Near the start of runs this is sometimes untrue, e.g. we may have 8 recoveries and 1 death,
            // so we wait for enough recoveries for this to make sense.
            if (s.getRecovered() >= 10) {
                assertTrue(s.getDead() <= s.getRecovered() * 0.1);
            }
            assertTrue(s.getDead() + nInfections >= s.getRecovered() * 0.005);
            assertTrue(s.getTotalInfected() <= lastTotalInfected * 2);
            lastTotalInfected = s.getTotalInfected();
        }

        // Check all infections occurred somewhere
        int totalDailyInfects = nInfections;
        int cummulativeI = 0;
        for (DailyStats s : stats.get(0)) {
            cummulativeI = s.getTotalInfected() + s.getRecovered() + s.getDead();
            totalDailyInfects += s.getTotalDailyInfections();
        }
        assertEquals(cummulativeI, totalDailyInfects);

        // Deaths should be proportional to phase2 progression
        int adultDeaths = 0;
        int pensionerDeaths = 0;
        int childDeaths = 0;
        for (DailyStats s : stats.get(0)) {
            adultDeaths = s.getAdultDeaths();
            pensionerDeaths += s.getPensionerDeaths();
            childDeaths += s.getChildDeaths();
        }

        if (CovidParameters.get().getAdultProgressionPhase2() < CovidParameters.get().getPensionerProgressionPhase2()) {
            assertTrue(adultDeaths <= pensionerDeaths);
        }
        if (CovidParameters.get().getChildProgressionPhase2() < CovidParameters.get().getAdultProgressionPhase2()) {
            assertTrue(childDeaths <= pensionerDeaths);
        }

    }

    @Test
    public void modelsWithSameRNGSeedGiveSameResult() {
        int population = 10000;
        int nInfections = 10;
        int seed = 42;

        Model run1 = new Model()
                .setPopulationSize(population)
                .setnInfections(nInfections)
                .setnHouseholds(3000)
                .setIters(1)
                .setnDays(90)
                .setRNGSeed(seed)
                .setNoOutput();

        List<List<DailyStats>> run1res = run1.run();

        Model run2 = new Model()
                .setPopulationSize(population)
                .setnInfections(nInfections)
                .setnHouseholds(3000)
                .setIters(1)
                .setnDays(90)
                .setRNGSeed(seed)
                .setNoOutput();

        List<List<DailyStats>> run2res = run2.run();

        assertEquals(run1res.size(), run2res.size());
        assertEquals(run1res.get(0).size(), run2res.get(0).size());

        List<DailyStats> r1 = run1res.get(0);
        List<DailyStats> r2 = run2res.get(0);
        for (int i = 0; i < r1.size(); i++) {
            assertEquals(r1.get(i), r2.get(i));
        }
    }
}
