package uk.co.ramp.covid.simulation.parameters;

import org.junit.Assert;
import org.junit.Test;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PopulationDistributionTest extends SimulationTest {

    @Test(expected = InvalidParametersException.class)
    public void readFromMapNonBreakableKey() {
        Map<String, Double> broken = new HashMap<>();
        broken.put("m4040",0.0);

        PopulationDistribution dist = new PopulationDistribution();
        dist.readFromMap(broken);
    }

    @Test(expected = InvalidParametersException.class)
    public void readFromMapBadSex() {
        Map<String, Double> broken = new HashMap<>();
        broken.put("s_40_45",0.0);

        PopulationDistribution dist = new PopulationDistribution();
        dist.readFromMap(broken);
    }

    @Test(expected = InvalidParametersException.class)
    public void readFromMapBadAgeRange() {
        Map<String, Double> broken = new HashMap<>();
        broken.put("m_50_45",0.0);

        PopulationDistribution dist = new PopulationDistribution();
        dist.readFromMap(broken);
    }

    @Test(expected = NumberFormatException.class)
    public void readFromMapBadAge() {
        Map<String, Double> broken = new HashMap<>();
        broken.put("m_x_45",0.0);

        PopulationDistribution dist = new PopulationDistribution();
        dist.readFromMap(broken);
    }

    @Test
    public void readFromMapWorks() {
        Map<String, Double> correct = new HashMap<>();
        correct.put("m_30_30",1.0);

        PopulationDistribution dist = new PopulationDistribution();
        dist.readFromMap(correct);

        PopulationDistribution.SexAge s = dist.sample();
        assertEquals(30, s.getAge());
        Assert.assertEquals(Person.Sex.MALE, s.getSex());
    }

    @Test
    public void seeAllAgesInALargeSample() {
        PopulationDistribution dist = new PopulationDistribution();
        dist.readFromMap(PopulationParameters.get().populationDistribution);

        int SAMPLES = 1000000;
        int[] seen = new int[101];

        for (int i = 0; i < SAMPLES; i++) {
            PopulationDistribution.SexAge s = dist.sample();
            seen[s.getAge()]++;
        }

        for (int i = 0; i < 101; i++) {
            assertTrue(seen[i] > 0);
        }
    }

}