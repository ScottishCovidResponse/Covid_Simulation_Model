package uk.co.ramp.covid.simulation;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.lockdown.FullLockdownEvent;
import uk.co.ramp.covid.simulation.output.CsvOutput;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.ParameterIO;
import uk.co.ramp.covid.simulation.util.Time;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class ModelTest {

    private static final int population = 10000;
    private static final int nInfections = 150;
    private static final int nIter = 1;
    private static final int nDays = 60;
    private static final int testSeed = 402;

    // To avoid recomputes
    private static List<List<DailyStats>> cachedStats = null;

    @BeforeClass
    public static void singleRun() throws IOException {
        ParameterIO.readParametersFromFile("parameters/example_population_params.json");

        double startTime = System.currentTimeMillis();
        
        Model cachedRun = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(testSeed)
                .setNoOutput();

        cachedStats = cachedRun.run(0);

        //Output model performance
        double runLength = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.printf("Total run time (secs): %.3f\n", runLength);
        System.out.printf("Mean run time per day: %.3f\n", runLength/nDays);
    }

    @Test
    public void testBaseLine() {
        List<List<DailyStats>> stats = cachedStats;

        int lastTotalInfected = nInfections;
        for (DailyStats s : stats.get(0)) {
            assertEquals(10000, s.getTotalPopulation());

            // Check there are deaths, but not too many
            // Near the start of runs this is sometimes untrue, e.g. we may have 8 recoveries and 1 death,
            // so we wait for enough recoveries for this to make sense.
            if (s.recovered.get() >= 50) {
                assertTrue(s.dead.get() <= s.recovered.get() * 0.1);
            }
            assertTrue(s.dead.get() + nInfections >= s.recovered.get() * 0.005);
            assertTrue(s.getTotalInfected() <= lastTotalInfected * 2);
            lastTotalInfected = s.getTotalInfected();
        }

        // Check all infections occurred somewhere
        int totalDailyInfects = nInfections;
        int cummulativeI;
        for (DailyStats s : stats.get(0)) {
            cummulativeI = s.getTotalInfected() + s.recovered.get() + s.dead.get();
            totalDailyInfects += s.getTotalDailyInfections();
            assertEquals(s.healthy.get(), population - cummulativeI);
            assertEquals(cummulativeI, totalDailyInfects);
        }

        // Deaths should be proportional to phase2 progression
        int adultDeaths = 0;
        int pensionerDeaths = 0;
        int childDeaths = 0;
        
        int totalDead = 0;
        for (DailyStats s : stats.get(0)) {
            adultDeaths = s.adultDeaths.get();
            pensionerDeaths += s.pensionerDeaths.get();
            childDeaths += s.childDeaths.get();
            totalDead += s.homeDeaths.get() + s.hospitalDeaths.get() + s.careHomeDeaths.get() + s.additionalDeaths.get();
        }
        
        List<DailyStats> s = stats.get(0);
        assertEquals(s.get(s.size() - 1).dead.get(), totalDead);

        if (CovidParameters.get().diseaseParameters.adultProgressionPhase2 < (double) CovidParameters.get().diseaseParameters.pensionerProgressionPhase2) {
            assertTrue(adultDeaths <= pensionerDeaths);
        }
        if (CovidParameters.get().diseaseParameters.childProgressionPhase2 < (double) CovidParameters.get().diseaseParameters.adultProgressionPhase2) {
            assertTrue(childDeaths <= pensionerDeaths);
        }
    }

    // The following "regression tests" are not being kept up-to-date, because
    //   we want to keep our life simple, and avoid git merge conflicts.
    // Please avoid committing changes to the expected results, and enable 
    //   these tests temporarily when making any changes which should not
    //   affect results.

    @Ignore("this regression test is not kept up-to-date")
    @Test
    public void quickSimpleRegressionTest() throws IOException {

        List<DailyStats> s = cachedStats.get(0);
        assertEquals("Day = 59 Healthy = 1338 Latent = 379 Asymptomatic = 606 Phase 1 = 373 Phase 2 = 266 Dead = 81 Recovered = 6957 Hospitalised = 76",
                s.get(s.size() - 1).logString());
    }

    @Ignore("this regression test is not kept up-to-date")
    @Test
    public void csvOutputRegressionTest() throws IOException {
        CsvOutput output = new CsvOutput(0, cachedStats);
        String csv = output.dailyStatsAsCSVString();

        Path expectedCsvPath = Paths.get("src/test/resources/regression_test_out.csv");
        // To update test data: Files.writeString(expectedCsvPath, sw.toString());
        String expectedCsvContents = new String(
                Files.readAllBytes(expectedCsvPath), StandardCharsets.UTF_8);

        assertEquals(expectedCsvContents, csv);
    }

    @Test
    public void modelsWithSameRNGSeedGiveSameResult() {

        Model repeatRun = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(testSeed)
                .setNoOutput();

        List<List<DailyStats>> run2res = repeatRun.run(0);

        assertEquals(cachedStats.size(), run2res.size());
        assertEquals(cachedStats.get(0).size(), run2res.get(0).size());

        List<DailyStats> r1 = cachedStats.get(0);
        List<DailyStats> r2 = run2res.get(0);
        for (int i = 0; i < r1.size(); i++) {
            assertEquals(r1.get(i), r2.get(i));
        }
    }

    @Test
    public void testReadModelFromFile() throws JsonParseException, IOException {
        Model m  = Model.readModelFromFile("src/test/resources/test_model_params.json");
        m.setNoOutput();
        assertTrue(m.isValid());
    }
    
    @Test
    public void testRNGSeedGeneration() throws JsonParseException, IOException {
        Model m  = Model.readModelFromFile("src/test/resources/test_model_params.json");
        m.optionallyGenerateRNGSeed();
        assertEquals(42, (int)m.getRNGSeed());

        Model m2  = Model.readModelFromFile("src/test/resources/test_model_params_with_no_seed.json");
        assertNull(m2.getRNGSeed());

        // This also should write the new seed to the log
        m2.optionallyGenerateRNGSeed();
        assertNotNull(m2.getRNGSeed());
    }

    @Test
    public void testLockdown() {

        int startLock = 30;

        //Re-run the model with partial lockdown
        Model m2 = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setNoOutput()
                .setRNGSeed(testSeed)
                .addLockdownEvent(new FullLockdownEvent(Time.timeFromDay(startLock), null,2.0));

        List<List<DailyStats>> stats2 = m2.run(0);

        //Check that there are the same number of infections in both scenarios before lockdown starts
        int inf1 = cachedStats.get(0).get(startLock - 1).getTotalInfected();
        int inf2 = stats2.get(0).get(startLock - 1).getTotalInfected();
        assertEquals("infection numbers don't match before lockdown", inf1, inf2);

        //Check that there are fewer infections in the lockdown scenario
        inf1 = cachedStats.get(0).get(nDays - 1).getTotalInfected();
        inf2 = stats2.get(0).get(nDays - 1).getTotalInfected();
        assertTrue("Unexpected more infections under lockdown." + inf1 + " > " + inf2, inf1 > inf2);

        //Test that the total number of infections before lockdown
        //is higher than during lockdown (ignoring the first 10 days after start of lockdown)
        int totInfBeforeLockdown = 0;
        int totInfDuringLockdown = 0;
        for (int i = 0; i < stats2.get(0).size(); i++) {
            if (i < startLock && i>=10) {
                totInfBeforeLockdown += stats2.get(0).get(i).getTotalDailyInfections();
            } else if (i < nDays && i>= startLock + 10) {
                totInfDuringLockdown += stats2.get(0).get(i).getTotalDailyInfections();
            }
        }
        assertTrue("Unexpectedly fewer infections before lockdown", totInfDuringLockdown < totInfBeforeLockdown);
     }

}
