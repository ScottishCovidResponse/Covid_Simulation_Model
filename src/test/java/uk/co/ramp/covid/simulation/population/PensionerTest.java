package uk.co.ramp.covid.simulation.population;

import org.junit.Test;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.co.ramp.covid.simulation.population.Person.Sex.FEMALE;

public class PensionerTest extends SimulationTest {

    @Test
    public void testPensionerReports() {
        //Test Pensioner methods reportInfection() and reportDeath()
        Population p = PopulationGenerator.genValidPopulation(500);
        Pensioner pensioner = new Pensioner(70, Person.Sex.MALE);

        List<DailyStats> stats;
        int nDays = 1;
        stats = p.simulate(nDays);

        pensioner.reportInfection(stats.get(0));
        assertEquals("Unexpected number of pensioner infections", 1, stats.get(0).getPensionerInfected());

        pensioner.reportDeath(stats.get(0));
        assertEquals("Unexpected number of pensioner deaths", 1, stats.get(0).getPensionerDeaths());
    }

    @Test (expected = InvalidAgeException.class)
    public void testInvalidAgeException() {
        new Pensioner(5, FEMALE);
    }

}