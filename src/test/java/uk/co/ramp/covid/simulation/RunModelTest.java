package uk.co.ramp.covid.simulation;

import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;

public class RunModelTest {

    RunModel mModel = new RunModel();

    class OutputForOneDay {
        private int[] values;

        public OutputForOneDay(String line) {
            values = Arrays.asList(line.split(",")).stream().mapToInt(s -> Integer.parseInt(s)).toArray();
        }

        public int getDay() {
            return values[0];
        }

        public int getHealthy() {
            return values[1];
        }

        public int getDead() {
            return values[6];
        }

        public int getRecovered() {
            return values[7];
        }

        public int getTotalInfected() {
            return values[2] + values[3] + values[4] + values[5];
        }

        public int getTotalPopulation() {
            int sum = 0;
            for (int i = 1; i < values.length; i++)
                sum += values[i];
            return sum;
        }
    }

    @Test
    public void testOneBaselineIter() {
        int populationSize = 10000;
        int nHouseholds = 3000;
        int nInfections = 10;
        ArrayList<String> vNext = mModel.oneBaselineIter(populationSize, nHouseholds, nInfections, 90);

        OutputForOneDay output;
        int lastTotalInfected = nInfections;
        for (String s : vNext) {
            output = new OutputForOneDay(s);
            Assert.assertEquals(populationSize, output.getTotalPopulation());
            Assert.assertTrue(output.getDead() <= output.getRecovered() * 0.1);
            Assert.assertTrue(output.getDead() + nInfections >= output.getRecovered() * 0.005);
            Assert.assertTrue(output.getTotalInfected() < lastTotalInfected * 2);
            lastTotalInfected = output.getTotalInfected();

            System.out.println(output.getDead() * 100.0 / output.getRecovered());
        }

        Assert.assertTrue(true);
    }
}
