package uk.co.ramp.covid.simulation.place;


import org.junit.Test;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SchoolTest extends SimulationTest {

    @Test
    public void testSchoolTransProb(){
        School school = new School(CommunalPlace.Size.MED);
        double expProb = PopulationParameters.get().buildingProperties.baseTransmissionConstant;
        double delta = 0.01;
        assertEquals("Unexpected school TransProb", expProb, school.transConstant, delta);
    }

    @Test
    public void testSchoolWorkers() throws ImpossibleWorkerDistributionException {
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
                for (School place : p.getPlaces().getSchools()) {
                    staff = place.getStaff(t);
                    totStaff += staff.size();
                }

                if (day < 5) {

                    //Staff should be at school during school times only
                    if (i + 1 < startTime || i + 1 >= endTime) {
                        assertEquals("Unexpected staff at school", 0, totStaff);
                    } else {
                        assertTrue("No staff at school", totStaff > 0);
                    }
                } else {
                    //Staff should not be at school on weekends
                    assertEquals("Unexpected staff at school", 0, totStaff);
                }
            }

        }
    }

}
