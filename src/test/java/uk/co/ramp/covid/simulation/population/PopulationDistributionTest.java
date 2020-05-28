package uk.co.ramp.covid.simulation.population;

import org.junit.Test;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PopulationDistributionTest {

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
        assertEquals(Person.Sex.MALE, s.getSex());
    }
}