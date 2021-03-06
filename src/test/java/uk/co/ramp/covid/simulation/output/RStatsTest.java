package uk.co.ramp.covid.simulation.output;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.lockdown.FullLockdownEvent;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Time;

import static org.junit.Assert.*;

public class RStatsTest extends SimulationTest {

    private Population pop;

    @Before
    public void setupParams() {
        int populationSize = 10000;
        pop = PopulationGenerator.genValidPopulation(populationSize);
    }

    @Test
    public void meanRWithNoInfectionsIsNull() {
        pop.getSeeder().forceNInfections(0);
        pop.simulate(20);
        RStats rs = new RStats(pop);

        for (int i = 0; i < 20; i++) {
            assertNull(rs.getSecInfections(i));
            assertNull(rs.getMeanGenerationTime(i));
        }
    }

    @Test
    public void meanRPositiveWhenInfectionsOccur() {
        pop.getSeeder().forceNInfections(20);
        pop.simulate(20);
        RStats rs = new RStats(pop);

        assertTrue("Mean R unexpectedly = 0", rs.getMeanRBefore(20) > 0);
        assertTrue("Secondary Infections unexpectedly = 0", rs.getSecInfections(0) > 0);
        assertTrue("Mean generation time unexpectedly = 0", rs.getMeanGenerationTime(0) > 0);
    }

    @Test
    public void testMeanRWithLockdown() {
        int startLock = 20;
        int nDays = 40;
        // Ensure enough infections for a reasonable R value
        pop.getSeeder().forceNInfections(100);
        pop.getLockdownController().addComponent(
                new FullLockdownEvent(Time.timeFromDay(startLock), pop, 2.0));
        pop.simulate(nDays);
        RStats rs = new RStats(pop);

        // Taking the mean with 10 days before lockdown and 20 days before the end accounts for the fact that
        // the generation time for COVID is 5-20 days (so the R value can be lower just before
        // a lockdown/near the end of the run).
        double meanRBeforeLockdown = rs.getMeanRBefore(startLock - 10);
        double meanRDuringLockdown = rs.getMeanRBetween(startLock, nDays - 20);

        assertTrue(meanRDuringLockdown < meanRBeforeLockdown);
    }
}
