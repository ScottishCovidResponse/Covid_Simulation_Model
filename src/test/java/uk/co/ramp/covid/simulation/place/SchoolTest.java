package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SchoolTest {


    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testSchoolTransProb() {
        School school = new School();
        double expProb = PopulationParameters.get().getpBaseTrans() * 30d / (34000d / 50d);
        double delta = 0.01;
        assertEquals("Unexpected school TransProb", expProb, school.transProb, delta);
    }

    @Test
    public void testSchoolWorkers() throws ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        int nInfections = 10;

        Population p = PopulationGenerator.genValidPopulation(populationSize);
        p.allocatePeople();
        p.seedVirus(nInfections);
        List<Person> staff;
        //Run for a whole week
        for (int day = 0; day < 7; day++) {
            int totStaff;
            int startTime = Shifts.schoolTimes().getShift(day).getStart();
            int endTime = Shifts.schoolTimes().getShift(day).getEnd();
            DailyStats s = new DailyStats(day);
            for (int i = 0; i < 24; i++) {
                p.timeStep(day, i, s);
                totStaff = 0;
                for (School place : p.getPlaces().getSchools()) {
                    staff = place.getStaff(day, i);
                    totStaff += staff.size();
                }

                if (day < 5) {

                    //Staff should be at school during school times only
                    if (i < startTime || i >= endTime - 1) {
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