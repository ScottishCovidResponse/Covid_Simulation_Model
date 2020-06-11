package uk.co.ramp.covid.simulation;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ModelTest extends SimulationTest {

    int population;
    int nInfections;
    int nIter;
    int nDays;
    int RNGSeed;

    @Before
    public void setupParams() {
        population = 10000;
        nInfections = 10;
        nIter = 1;
        nDays = 60;
        RNGSeed = 42;
    }

    @Test
    public void testBaseLine() {

        Model m = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> stats = m.run(0);

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

        if (CovidParameters.get().diseaseParameters.adultProgressionPhase2 < (double) CovidParameters.get().diseaseParameters.pensionerProgressionPhase2) {
            assertTrue(adultDeaths <= pensionerDeaths);
        }
        if (CovidParameters.get().diseaseParameters.childProgressionPhase2 < (double) CovidParameters.get().diseaseParameters.adultProgressionPhase2) {
            assertTrue(childDeaths <= pensionerDeaths);
        }

    }

    @Test
    public void modelsWithSameRNGSeedGiveSameResult() {

        Model run1 = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> run1res = run1.run(0);

        Model run2 = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> run2res = run2.run(0);

        assertEquals(run1res.size(), run2res.size());
        assertEquals(run1res.get(0).size(), run2res.get(0).size());

        List<DailyStats> r1 = run1res.get(0);
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
        m.run(0);
    }
    
    @Test
    public void testRNGSeedGeneration() throws JsonParseException, IOException {
        Model m  = Model.readModelFromFile("src/test/resources/test_model_params.json");
        m.optionallyGenerateRNGSeed();
        assertEquals(42, (int)m.getRNGSeed());

        Model m2  = Model.readModelFromFile("src/test/resources/test_model_params_with_no_seed.json");
        assertNull(m2.getRNGSeed());
        assertFalse(m2.isValid());

        // This also should write the new seed to the log
        m2.optionallyGenerateRNGSeed();
        assertTrue(m2.isValid());
    }

    @Test
    public void testLockdown() {

        int startLock = 30;
        int endLock = 60;
        nInfections = 100;

        //Run the model with no lockdown
        Model m1 = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections * 10)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> stats1 = m1.run(0);

        //Re-run the model with partial lockdown
        Model m2 = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections * 10)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput()
                .setLockdown(startLock, endLock, 2.0);

        List<List<DailyStats>> stats2 = m2.run(0);

        //Check that there are the same number of infections in both scenarios before lockdown starts
        int inf1 = stats1.get(0).get(29).getTotalInfected();
        int inf2 = stats2.get(0).get(29).getTotalInfected();
        assertEquals("infection numbers don't match before lockdown", inf1, inf2);

        //Check that there are fewer infections in the lockdown scenario
        inf1 = stats1.get(0).get(nDays - 1).getTotalInfected();
        inf2 = stats2.get(0).get(nDays - 1).getTotalInfected();
        assertTrue("Unexpected more infections under lockdown." + inf1 + " > " + inf2, inf1 > inf2);

        //Test that the total number of infections before lockdown
        //is higher than during lockdown (ignoring the first 10 days after start of lockdown)
        int totInfBeforeLockdown = 0;
        int totInfDuringLockdown = 0;
        for (int i = 0; i < stats2.get(0).size(); i++) {
            if (i < startLock && i>=10) {
                totInfBeforeLockdown += stats2.get(0).get(i).getTotalDailyInfections();
            } else if (i < endLock && i>= startLock + 10) {
                totInfDuringLockdown += stats2.get(0).get(i).getTotalDailyInfections();
            }
        }
        assertTrue("Unexpectedly fewer infections before lockdown", totInfDuringLockdown < totInfBeforeLockdown);
     }

    @Test
    public void testMortality() {
        //Mortality and transmission rates are set to 100%
        //Check that everyone is infected and progresses to death
        CovidParameters.get().diseaseParameters.pSymptomaticCase = new Probability(1.0);
        CovidParameters.get().diseaseParameters.meanSymptomDelay = -5.0;
        CovidParameters.get().diseaseParameters.meanLatentPeriod = 50.0;
        CovidParameters.get().diseaseParameters.meanInfectiousDuration = 100.0;
        CovidParameters.get().diseaseParameters.mortalityRate = 100.0;
        CovidParameters.get().diseaseParameters.childProgressionPhase2 = 100.0;
        CovidParameters.get().diseaseParameters.adultProgressionPhase2 = 100.0;
        CovidParameters.get().diseaseParameters.pensionerProgressionPhase2 = 100.0;
        CovidParameters.get().diseaseParameters.symptomaticTransAdjustment = 100.0;
        CovidParameters.get().diseaseParameters.aSymptomaticTransAdjustment = 100.0;
        PopulationParameters.get().personProperties.pTransmission = new Probability(1.0);
        PopulationParameters.get().personProperties.pQuarantinesIfSymptomatic = new Probability(0.0);
        nDays = 100;
        //Run the model
        Model m1 = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections * 30)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> stats1 = m1.run(0);
        int dead = stats1.get(0).get(nDays - 1).getDead();
        int recovered = stats1.get(0).get(nDays -1).getRecovered();
        int latent = stats1.get(0).get(nDays -1).getExposed();
        int phase1 = stats1.get(0).get(nDays -1).getPhase1();
        int phase2 = stats1.get(0).get(nDays -1).getPhase2();
        assertTrue("Too many latent remain", latent < 3);
        assertTrue("Too many P1 remain", phase1 < 3);
        assertTrue("Too many P2 remain", phase2 < 3);
        assertEquals("Unexpected recoveries", 0, recovered);
        assertEquals("Unexpected number of deaths", population, dead, latent + phase1 + phase2);
    }
}
