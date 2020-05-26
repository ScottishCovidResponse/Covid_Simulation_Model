package uk.co.ramp.covid.simulation;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModelTest {

    int population;
    int nInfections;
    int nHouseholds;
    int nIter;
    int nDays;
    int RNGSeed;

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        population = 10000;
        nInfections = 10;
        nHouseholds = 3000;
        nIter = 1;
        nDays = 90;
        RNGSeed = 42;
    }

    @Test
    public void testBaseLine() {

        Model m = new Model()
                .setPopulationSize(population)
                .setnInfections(nInfections)
                .setnHouseholds(nHouseholds)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> stats = m.run();

        int lastTotalInfected = 10;
        for (DailyStats s : stats.get(0)) {
            assertEquals(10000, s.getTotalPopulation());

            // Check there are deaths, but not too many
            // Near the start of runs this is sometimes untrue, e.g. we may have 8 recoveries and 1 death,
            // so we wait for enough recoveries for this to make sense.
            if (s.getRecovered() >= 50) {
                assertTrue(s.getDead() <= s.getRecovered() * 0.1);
            }
            assertTrue(s.getDead() + nInfections >= s.getRecovered() * 0.005);
            assertTrue(s.getTotalInfected() <= lastTotalInfected * 2);
            lastTotalInfected = s.getTotalInfected();
        }

        // Check all infections occurred somewhere
        int totalDailyInfects = nInfections;
        int cummulativeI;
        for (DailyStats s : stats.get(0)) {
            cummulativeI = s.getTotalInfected() + s.getRecovered() + s.getDead();
            totalDailyInfects += s.getTotalDailyInfections();
            assertEquals(s.getHealthy(), population - cummulativeI);
            assertEquals(cummulativeI, totalDailyInfects);
        }

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

        Model run1 = new Model()
                .setPopulationSize(population)
                .setnInfections(nInfections)
                .setnHouseholds(nHouseholds)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> run1res = run1.run();

        Model run2 = new Model()
                .setPopulationSize(population)
                .setnInfections(nInfections)
                .setnHouseholds(nHouseholds)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
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

    @Test
    public void testLockdown() {

        int startLock = 30;
        int endLock = 60;

        //Run the model with no lockdown
        Model m1 = new Model()
                .setPopulationSize(population)
                .setnInfections(nInfections)
                .setnHouseholds(nHouseholds)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> stats1 = m1.run();

        //Re-run the model with partial lockdown
        Model m2 = new Model()
                .setPopulationSize(population)
                .setnInfections(nInfections)
                .setnHouseholds(nHouseholds)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput()
                .setLockdown(startLock, endLock, 2.0);

        List<List<DailyStats>> stats2 = m2.run();

        //Check that there are fewer infections in the lockdown scenario
        int inf1 = stats1.get(0).get(nDays - 1).getTotalInfected();
        int inf2 = stats2.get(0).get(nDays - 1).getTotalInfected();
        assertTrue("Unexpected more infections under lockdown", inf1 > inf2);

        //Test that the total number of infections before lockdown
        //is higher than during lockdown
        int totInfBeforeLockdown = 0;
        int totInfDuringLockdown = 0;
        int totInfAfterLockdown = 0;
        for (int i = 0; i < stats2.get(0).size(); i++) {
            if (i < startLock) {
                totInfBeforeLockdown += stats2.get(0).get(i).getTotalDailyInfections();
            } else if (i < endLock) {
                totInfDuringLockdown += stats2.get(0).get(i).getTotalDailyInfections();
            } else {
                totInfAfterLockdown += stats2.get(0).get(i).getTotalDailyInfections();
            }
        }
        assertTrue("Unexpectedly fewer infections before lockdown", totInfDuringLockdown < totInfBeforeLockdown);
        assertTrue("Unexpectedly fewer infections after lockdown", totInfDuringLockdown < totInfAfterLockdown);
    }

    @Test
    public void testMortality() throws IOException {
        //Mortality and transmission rates are set to 100%
        //Check that everyone is infected and progresses to death
        ParameterReader.readParametersFromFile("src/test/resources/Test_full_mortality.json");
        nDays = 300;
        //Run the model
        Model m1 = new Model()
                .setPopulationSize(population)
                .setnInfections(nInfections)
                .setnHouseholds(nHouseholds)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> stats1 = m1.run();
        int dead = stats1.get(0).get(nDays - 1).getDead();
        int recovered = stats1.get(0).get(nDays -1).getRecovered();
        assertEquals("Unexpected recoveries", 0, recovered);
        assertEquals("Unexpected number of deaths", population, dead);
    }
}
