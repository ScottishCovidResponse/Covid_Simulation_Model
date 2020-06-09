package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

public class NurseryTest extends SimulationTest {

    @Test
    public void testNurseryTransProb() throws JsonParseException {
        Nursery nursery = new Nursery(CommunalPlace.Size.MED);
        double expProb = PopulationParameters.get().buildingProperties.baseTransmissionConstant;
        double delta = 0.01;
        assertEquals("Unexpected nursery TransProb", expProb, nursery.transConstant, delta);
    }

    @Test
    public void testNurseryWorkers() throws ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        Population p = PopulationGenerator.genValidPopulation(populationSize);
        p.allocatePeople();
        List<Person> staff;
        Time t = new Time(0);
        //Run for a whole week
        for (int day = 0; day < 7; day++) {
            int totStaff;
            int startTime = Shifts.schoolTimes().getShift(day).getStart();
            int endTime = Shifts.schoolTimes().getShift(day).getEnd();
            DailyStats s = new DailyStats(t);
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                t = t.advance();
                totStaff = 0;
                for (Nursery place : p.getPlaces().getNurseries()) {
                    staff = place.getStaff(t);
                    totStaff += staff.size();
                }

                if (day < 5) {
                    //Staff should be at nursery during school times only
                    if (i + 1 < startTime || i + 1 >= endTime) {
                        assertEquals("Unexpected staff at nursery", 0, totStaff);
                    } else {
                        assertTrue("No staff at nursery", totStaff > 0);
                    }
                } else {
                    //Staff should not be at nursery on weekends
                    assertEquals("Unexpected staff at nursery", 0, totStaff);
                }
            }

        }
    }

}
