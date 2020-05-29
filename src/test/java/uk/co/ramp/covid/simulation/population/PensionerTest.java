package uk.co.ramp.covid.simulation.population;

import org.junit.Assert;
import org.junit.Test;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PensionerTest {

    @Test
    public void testPensionerReports() throws IOException, ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        //Test Pensioner methods reportInfection() and reportDeath()
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        Population p = new Population(500,60);
        Pensioner pensioner = new Pensioner(70, Person.Sex.MALE);

        List<DailyStats> stats;
        int nDays = 1;
        stats = p.simulate(nDays);

        pensioner.reportInfection(stats.get(0));
        assertEquals("Unexpected number of pensioner infections", 1, stats.get(0).getPensionerInfected());

        pensioner.reportDeath(stats.get(0));
        assertEquals("Unexpected number of pensioner deaths", 1, stats.get(0).getPensionerDeaths());
    }

}