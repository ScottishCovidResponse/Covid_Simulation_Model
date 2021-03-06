package uk.co.ramp.covid.simulation.output;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.CareHome;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //Test that all statistics are collected and counted correctly
    @Test
    public void testDailyStats() {
        // Place infection statistics
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
        int careHomeInfectionsWorker = 0;
        int careHomeInfectionsResident = 0;
        int transportInfections = 0;

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

        // Hospitalisation Stats
        int hospitalised = 0;
        int newlyHospitalised = 0;
        int totalPhase2 = 0;

        nDays = 50;

        Model run1 = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(10)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> stats = run1.run(0);
        int healthy = stats.get(0).get(nDays - 1).healthy.get();
        int exposed = stats.get(0).get(nDays - 1).exposed.get();
        int asymptomatic = stats.get(0).get(nDays - 1).asymptomatic.get();
        int phase1 = stats.get(0).get(nDays - 1).phase1.get();
        int phase2 = stats.get(0).get(nDays - 1).phase2.get();
        int dead = stats.get(0).get(nDays - 1).dead.get();
        int recovered = stats.get(0).get(nDays - 1).recovered.get();
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
            adultDeaths += stats.get(0).get(i).adultDeaths.get();
            childDeaths += stats.get(0).get(i).childDeaths.get();
            pensionerDeaths += stats.get(0).get(i).pensionerDeaths.get();
            infantDeaths += stats.get(0).get(i).infantDeaths.get();
            adultInfected += stats.get(0).get(i).adultInfected.get();
            childInfected += stats.get(0).get(i).childInfected.get();
            pensionerInfected += stats.get(0).get(i).pensionerInfected.get();
            infantInfected += stats.get(0).get(i).infantInfected.get();
            dailyInfected += stats.get(0).get(i).getTotalDailyInfections();
            seedInfections += stats.get(0).get(i).seedInfections.get();

            homeInfectionsInhabitant += stats.get(0).get(i).homeInfectionsInhabitant.get();
            homeInfectionsVisitor += stats.get(0).get(i).homeInfectionsVisitor.get();
            constructionSiteInfectionsWorker += stats.get(0).get(i).constructionSiteInfectionsWorker.get();
            constructionSiteInfectionsVisitor += stats.get(0).get(i).constructionSiteInfectionsVisitor.get();
            hospitalInfectionsWorker += stats.get(0).get(i).hospitalInfectionsWorker.get();
            hospitalInfectionsVisitor += stats.get(0).get(i).hospitalInfectionsVisitor.get();
            nurseryInfectionsWorker += stats.get(0).get(i).nurseryInfectionsWorker.get();
            nurseryInfectionsVisitor += stats.get(0).get(i).nurseryInfectionsVisitor.get();
            officeInfectionsWorker += stats.get(0).get(i).officeInfectionsWorker.get();
            officeInfectionsVisitor += stats.get(0).get(i).officeInfectionsVisitor.get();
            restaurantInfectionsWorker += stats.get(0).get(i).restaurantInfectionsWorker.get();
            restaurantInfectionsVisitor += stats.get(0).get(i).restaurantInfectionsVisitor.get();
            schoolInfectionsWorker += stats.get(0).get(i).schoolInfectionsWorker.get();
            schoolInfectionsVisitor += stats.get(0).get(i).schoolInfectionsVisitor.get();
            shopInfectionsWorker += stats.get(0).get(i).shopInfectionsWorker.get();
            shopInfectionsVisitor += stats.get(0).get(i).shopInfectionsVisitor.get();
            careHomeInfectionsWorker += stats.get(0).get(i).careHomeInfectionsWorker.get();
            careHomeInfectionsResident += stats.get(0).get(i).careHomeInfectionsResident.get();
            transportInfections += stats.get(0).get(i).transportInfections.get();

            // Hospitalised will double count people (since you might in in hospital multiple days),
            // so we need to account for this in the test cases
            hospitalised += stats.get(0).get(i).inHospital.get();
            newlyHospitalised += stats.get(0).get(i).newlyHospitalised.get();
            totalPhase2 +=  stats.get(0).get(i).phase2.get();
        }
        int expDeaths = adultDeaths + childDeaths + pensionerDeaths + infantDeaths;
        int expInfected = adultInfected + childInfected + pensionerInfected + infantInfected + seedInfections;
        int expPlaceInfections = homeInfectionsInhabitant + homeInfectionsVisitor +
                constructionSiteInfectionsWorker + constructionSiteInfectionsVisitor +
                hospitalInfectionsWorker + hospitalInfectionsVisitor +
                nurseryInfectionsWorker + nurseryInfectionsVisitor +
                officeInfectionsWorker + officeInfectionsVisitor +
                restaurantInfectionsWorker + restaurantInfectionsVisitor +
                schoolInfectionsWorker + schoolInfectionsVisitor +
                shopInfectionsWorker + shopInfectionsVisitor + transportInfections +
                seedInfections + careHomeInfectionsResident + careHomeInfectionsWorker;
        assertEquals("Inconsistent number of deaths", expDeaths, stats.get(0).get(nDays - 1).dead.get());
        assertEquals("Inconsistent number of infected", expInfected, dailyInfected);
        assertEquals("Inconsistent number of place infections", expPlaceInfections, dailyInfected);
        
        assertNotEquals("Some people are hospitalised", 0, hospitalised);
        assertNotEquals("Some people are hospitalised (new cases)", 0, newlyHospitalised);
        assertTrue("Hospitalised should be > newlyHospitalised", hospitalised > newlyHospitalised);
        assertTrue("Hospitalised <= phase2", newlyHospitalised <= totalPhase2);

        // Test consistency of deaths by infection day
        int totalInfectionsByDay = 0;
        for (DailyStats s : stats.get(0)) {
            totalInfectionsByDay += s.deathsAfterInfectionToday.get();
        }
        assertEquals(expDeaths, totalInfectionsByDay);
    }

    @Test
    public void testConsistentDeaths() {
        int population = 10000;
        int nInfections = 300;
        int nIter = 1;
        int nDays = 50;
        int RNGSeed = 42;
        
        // Try to force at least 1 care home death for test purposes
        // Allow care homes to have any number of residents
        PopulationParameters.get().buildingDistribution.careHomeResidentRanges =
                Collections.singletonList(new CareHome.CareHomeResidentRange(1, population, new Probability(1.0)));

        CovidParameters.get().diseaseParameters.pensionerProgressionPhase2 = 100.0;
        PopulationParameters.get().pensionerProperties.pEntersCareHome = new Probability(0.33);

        Model m = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> stats = m.run(0);

        int totalHospitalDeaths = 0;
        int totalCareHomeDeaths = 0;
        int totalHomeDeaths = 0;
        int totalAdditionalDeaths = 0;
        int totalDeaths = 0;
        for (DailyStats s : stats.get(0)) {
            totalHospitalDeaths += s.hospitalDeaths.get();
            totalCareHomeDeaths += s.careHomeDeaths.get();
            totalHomeDeaths += s.homeDeaths.get();
            totalAdditionalDeaths += s.additionalDeaths.get();
            totalDeaths += s.getTotalDeaths();
        }
        int actualDeaths = totalHospitalDeaths + totalCareHomeDeaths + totalHomeDeaths + totalAdditionalDeaths;
        assertTrue("No hospital deaths recorded", totalHospitalDeaths > 0);
        assertTrue("No care home deaths recorded", totalCareHomeDeaths > 0);
        assertTrue("No home deaths recorded", totalHomeDeaths > 0);
        
        assertTrue("Most people go home/hospital before dying",
                totalAdditionalDeaths < totalHomeDeaths && totalAdditionalDeaths < totalHospitalDeaths);
        
        assertEquals("Inconsistent number of deaths", totalDeaths, actualDeaths);

        // Test consistency in deathsByAge
        Map<Integer, Integer> combinedDeathsByAge = new HashMap<>();
        for (DailyStats s : stats.get(0)) {
           s.deathsByAge.forEach((k, v) -> combinedDeathsByAge.merge(k, v, Integer::sum));
        }

        final int[] deathsByAge = {0};
        combinedDeathsByAge.forEach((k, v) -> deathsByAge[0] += v);
        assertEquals(totalDeaths, deathsByAge[0]);
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
