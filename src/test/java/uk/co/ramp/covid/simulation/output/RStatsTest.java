package uk.co.ramp.covid.simulation.output;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.co.ramp.covid.simulation.lockdown.FullLockdownEvent;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Time;

import java.sql.Timestamp;

import static org.junit.Assert.*;

public class RStatsTest extends SimulationTest {

    private Population pop;

    @Before
    public void setupParams() {
        int populationSize = 50000;
        pop = PopulationGenerator.genValidPopulation(populationSize);
    }

    @Test
    public void meanRWithNoInfectionsIsNull() {
        pop.seedVirus(0);
        pop.simulate(20);
        RStats rs = new RStats(pop);

        for (int i = 0; i < 20; i++) {
            assertNull(rs.getSecInfections(i));
            assertNull(rs.getMeanGenerationTime(i));
        }
    }

    @Test
    public void meanRPositiveWhenInfectionsOccur() {
        pop.seedVirus(20);
        pop.simulate(20);
        RStats rs = new RStats(pop);

        assertTrue("Mean R unexpectedly = 0", rs.getMeanRBefore(20) > 0);
        assertTrue("Secondary Infections unexpectedly = 0", rs.getSecInfections(0) > 0);
        assertTrue("Mean generation time unexpectedly = 0", rs.getMeanGenerationTime(0) > 0);
    }

    @Test
    public void testMeanRWithLockdown() {
        int populationSize = 50000;
        pop = PopulationGenerator.genValidPopulation(populationSize);
        int startLock = 30;
        int nDays = 90;
        pop.seedVirus(10);
        pop.getLockdownController().addComponent(
                new FullLockdownEvent(Time.timeFromDay(startLock), pop, 2.0));
        pop.simulate(nDays);
        RStats rs = new RStats(pop);
        double peakR = 0.0;

        for (int i = 0; i < nDays; i++) {
            //Get the peak R value before lockdown starts
            if (i < startLock) peakR = Math.max(peakR, rs.getMeanRBefore(i + 1));
            //Test that the R value during lockdown is always below the peak R
            if (i > startLock + 5) {
                assertTrue("Peak R occurred at Day " + i, rs.getMeanRBefore(i + 1) < peakR);
            }
        }

    }
}