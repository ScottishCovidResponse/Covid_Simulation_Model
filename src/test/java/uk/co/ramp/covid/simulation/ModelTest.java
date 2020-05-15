package uk.co.ramp.covid.simulation;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import uk.co.ramp.covid.simulation.io.ParameterReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testBaseLine() {
        int population = 10000;
        int nInfections = 10;

        Model m = new Model()
                .setPopulationSize(population)
                .setnInfections(nInfections)
                .setnHouseholds(3000)
                .setIters(1)
                .setnDays(90)
                .setOutputFile("test_output.csv");

        List<List<DailyStats>> stats = m.run();

        int lastTotalInfected = 10;
        int lastDead = 0;
        int totalHealthy = 0;
        for (DailyStats s : stats.get(0)) {
            Assert.assertEquals(10000, s.getTotalPopulation());
            Assert.assertTrue(s.getDead() <= s.getRecovered() * 0.1);
            Assert.assertTrue(s.getDead() + nInfections >= s.getRecovered() * 0.005);
            Assert.assertTrue(s.getTotalInfected() < lastTotalInfected * 2);
            lastTotalInfected = s.getTotalInfected();
            lastDead = s.getDead();
            totalHealthy = s.getHealthy();
        }
        int totalInfections = population - totalHealthy;

        //Check number of dead are 3% of total infections (+/- 1%)
        Assert.assertEquals("Unexpected proportion of dead", 0.03, ((double)lastDead/(double)totalInfections), 0.01);
    }
}
