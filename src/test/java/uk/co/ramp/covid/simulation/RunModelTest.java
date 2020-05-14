package uk.co.ramp.covid.simulation;

import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunModelTest {
    RunModel mModel = new RunModel();

    @Test
    public void testOneBaselineIter() {
        int populationSize = 10000;
        int nHouseholds = 3000;
        int nInfections = 10;
        List<DailyStats> stats = mModel.oneBaselineIter(populationSize, nHouseholds, nInfections, 90);

        int lastTotalInfected = nInfections;
        for (DailyStats s : stats) {
            Assert.assertEquals(populationSize, s.getTotalPopulation());
            Assert.assertTrue(s.getDead() <= s.getRecovered() * 0.1);
            Assert.assertTrue(s.getDead() + nInfections >= s.getRecovered() * 0.005);
            Assert.assertTrue(s.getTotalInfected() < lastTotalInfected * 2);
            lastTotalInfected = s.getTotalInfected();
        }
    }
}
