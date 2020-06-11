package uk.co.ramp.covid.simulation.output;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.List;

import static org.junit.Assert.*;

public class DailyStatsTest extends SimulationTest {

    int population;
    int nInfections;
    int nIter;
    int nDays;
    int RNGSeed;

    @Before
    public void setupParams() {
        population = 10000;
        nInfections = 100;
        nIter = 1;
        nDays = 5;
        RNGSeed = 42;
    }

    //Test that all statistics are collected and self consistent
    @Test
    public void testDailyStats() {
        // Daily only statistics
        int homeInfectionsInhabitant = 0;
        int homeInfectionsVisitor = 0;
        int constructionSiteInfectionsWorker = 0;
        int constructionSiteInfectionsVisitor = 0;
        int hospitalInfectionsWorker = 0;
        int hospitalInfectionsVisitor = 0;
        int nurseryInfectionsWorker = 0;
        int nurseryInfectionsVisitor = 0;
        int officeInfectionsWorker = 0;
        int officeInfectionsVisitor = 0;
        int restaurantInfectionsWorker = 0;
        int restaurantInfectionsVisitor  = 0;
        int schoolInfectionsWorker = 0;
        int schoolInfectionsVisitor = 0;
        int shopInfectionsWorker = 0;
        int shopInfectionsVisitor = 0;
        int seedInfections = 0;

        // Age Statistics
        int adultInfected = 0;
        int pensionerInfected = 0;
        int childInfected  = 0;
        int infantInfected = 0;
        int dailyInfected = 0;

        // Fatality Statistics
        int adultDeaths = 0;
        int pensionerDeaths = 0;
        int childDeaths = 0;
        int infantDeaths = 0;

        nDays = 100;
        Model run1 = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(10)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> stats = run1.run(0);
        int healthy = stats.get(0).get(nDays - 1).getHealthy();
        int exposed = stats.get(0).get(nDays - 1).getExposed();
        int asymptomatic = stats.get(0).get(nDays - 1).getAsymptomatic();
        int phase1 = stats.get(0).get(nDays - 1).getPhase1();
        int phase2 = stats.get(0).get(nDays - 1).getPhase2();
        int dead = stats.get(0).get(nDays - 1).getDead();
        int recovered = stats.get(0).get(nDays - 1).getRecovered();
        int expInfections = exposed + asymptomatic + phase1 + phase2;

        assertTrue("Number of healthy = 0", healthy > 0);
        assertTrue("Number of exposed = 0",exposed > 0);
        assertTrue("Number of asymptomatic = 0",asymptomatic > 0);
        assertTrue("Number of phase1 = 0",phase1 > 0);
        assertTrue("Number of phase2 = 0",phase2 > 0);
        assertTrue("Number of dead = 0",dead > 0);
        assertTrue("Number of recovered = 0",recovered > 0);
        assertEquals("Inconsistent number of infections", expInfections, stats.get(0).get(nDays - 1).getTotalInfected());

        for (int i = 0; i < stats.get(0).size(); i++) {
            adultDeaths += stats.get(0).get(i).getAdultDeaths();
            childDeaths += stats.get(0).get(i).getChildDeaths();
            pensionerDeaths += stats.get(0).get(i).getPensionerDeaths();
            infantDeaths += stats.get(0).get(i).getInfantDeaths();
            adultInfected += stats.get(0).get(i).getAdultInfected();
            childInfected += stats.get(0).get(i).getChildInfected();
            pensionerInfected += stats.get(0).get(i).getPensionerInfected();
            infantInfected += stats.get(0).get(i).getInfantInfected();
            dailyInfected += stats.get(0).get(i).getTotalDailyInfections();
            seedInfections += stats.get(0).get(i).getSeedInfections();

            constructionSiteInfectionsWorker = stats.get(0).get(i).getConstructionSiteInfectionsWorker();
            constructionSiteInfectionsVisitor = 0;
            hospitalInfectionsWorker = 0;
            hospitalInfectionsVisitor = 0;
            nurseryInfectionsWorker = 0;
            nurseryInfectionsVisitor = 0;
            officeInfectionsWorker = 0;
            officeInfectionsVisitor = 0;
            restaurantInfectionsWorker = 0;
            restaurantInfectionsVisitor  = 0;
            schoolInfectionsWorker = 0;
            schoolInfectionsVisitor = 0;
            shopInfectionsWorker = 0;
            shopInfectionsVisitor = 0;
        }
        int expDeaths = adultDeaths + childDeaths + pensionerDeaths + infantDeaths;
        int expInfected = adultInfected + childInfected + pensionerInfected + infantInfected + seedInfections;
        assertEquals("Inconsistent number of deaths", expDeaths, stats.get(0).get(nDays - 1).getDead());
        assertEquals("Inconsistent number of infected", expInfected, dailyInfected);

    }

    //Test the equals() method in DailyStats
    @Test
    public void testEquals() {
        nIter = 2;
        Model run1 = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> run1res = run1.run(0);

        List<DailyStats> r1 = run1res.get(0);
        List<DailyStats> r2 = run1res.get(0);
        List<DailyStats> r3 = run1res.get(1);
        assertTrue("DailyStats equals method unexpectedly returns false", r1.equals(r2));
        assertFalse("DailyStats equals method unexpectedly returns true", r2.equals(r3));
    }
}
