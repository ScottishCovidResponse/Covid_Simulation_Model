package uk.co.ramp.covid.simulation;

import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelTest {

    @Test
    public void testBaseLine() {
        int population = 10000;
        int nInfections = 10;

        Model m = new Model()
                .setPopulationSize(population)
                .setnInfections(nInfections)
                .setnHouseholds(3000)
                .setIters(1)
                .setnDays(90);

        List<List<DailyStats>> stats = m.run();

        int lastTotalInfected = 10;
        for (DailyStats s : stats.get(0)) {
            Assert.assertEquals(10000, s.getTotalPopulation());
            Assert.assertTrue(s.getDead() <= s.getRecovered() * 0.1);
            Assert.assertTrue(s.getDead() + nInfections >= s.getRecovered() * 0.005);
            Assert.assertTrue(s.getTotalInfected() < lastTotalInfected * 2);
            lastTotalInfected = s.getTotalInfected();
        }
    }
}
