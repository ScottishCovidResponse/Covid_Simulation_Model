package uk.co.ramp.covid.simulation;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.util.SimulationTest;

import static org.junit.Assert.*;

public class RStatsTest extends SimulationTest {

    private Population pop;
    private final int populationSize = 10000;

    @Before
    public void setupParams() {
        pop = PopulationGenerator.genValidPopulation(populationSize);
    }

    @Test
    public void meanRWithNoInfectionsIsNull() {
        pop.seedVirus(0);
        pop.simulate(20);
        RStats rs = new RStats(pop);

        for (int i = 0; i < 20; i++) {
            assertNull(rs.getMeanR(i));
            assertNull(rs.getMeanGenerationTime(i));
        }
    }

    @Test
    public void meanRPositiveWhenInfectionsOccur() {
        pop.seedVirus(20);
        pop.simulate(30);
        RStats rs = new RStats(pop);

        assertTrue(rs.getMeanRBefore(30) > 0);
    }
}