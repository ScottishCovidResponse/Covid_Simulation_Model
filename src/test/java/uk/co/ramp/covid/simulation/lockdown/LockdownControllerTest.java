package uk.co.ramp.covid.simulation.lockdown;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.place.Nursery;
import uk.co.ramp.covid.simulation.place.School;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import static org.junit.Assert.*;

public class LockdownControllerTest extends SimulationTest  {

    Population pop;

    @Before
    public void setup() {
        pop = PopulationGenerator.genValidPopulation(10000);
    }

    @Test
    public void testSetLockdown() {
        int start = 1;
        int end = 2;
        double socialDist = 2.0;
        pop.setLockdown(start, end, socialDist);
        assertEquals("Unexpected lockdown start", start, pop.getLockdownController().getLockdownStart().getAbsDay());
        assertEquals("Unexpected lockdown end", end, pop.getLockdownController().getLockdownEnd().getAbsDay());
    }

    @Test
    public void testLockdownOver() {
        int nDays = 5;
        int startLockdown = 2;
        int endLockdown = 4;
        double socialDist = 2.0;
        pop.setLockdown(startLockdown, endLockdown, socialDist);
        pop.simulate(nDays);
        assertFalse("Unexpectedly still in lockdown", pop.getLockdownController().inLockdown(Time.timeFromDay(nDays)));
    }

    @Test
    public void testInLockdown() {
        int nDays = 5;
        int start = 3;
        int end = 6;
        double socialDist = 2.0;
        pop.setLockdown(start, end, socialDist);
        pop.simulate(nDays);
        assertTrue("Unexpectedly not in lockdown",
                pop.getLockdownController().inLockdown(Time.timeFromDay(nDays)));

    }
    
    @Test
    public void testSetSchoolLockdown() {
        int start = 1;
        int end = 2;
        double socialDist = 2.0;
        pop.setSchoolLockdown(start, end, socialDist);
        assertEquals("Unexpected school lockdown start",
                start, pop.getLockdownController().getLockdownStart().getAbsDay());
        assertEquals("Unexpected school lockdown end",
                end, pop.getLockdownController().getLockdownEnd().getAbsDay());
    }

    @Test
    public void testSchoolExemption() {
        int nDays = 5;
        int startLockdown = 1;
        int endLockdown = 5;
        double socialDist = 2.0;
        //pop.setLockdown(startLockdown, endLockdown, socialDist);
        pop.setSchoolLockdown(startLockdown, endLockdown - 2, socialDist);
        pop.simulate(nDays);
        for (School s : pop.getPlaces().getSchools()) {
            assertTrue("School should be a key premises", s.isKeyPremises());
        }
        for (Nursery n : pop.getPlaces().getNurseries()) {
            assertTrue("Nursery should be a key premises", n.isKeyPremises());
        }
    }

}