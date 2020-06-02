package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

public class NurseryTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testNurseryTransProb() throws JsonParseException {
        Nursery nursery = new Nursery(CommunalPlace.Size.MED);
        double expProb = PopulationParameters.get().getpBaseTrans() * 30d / (34000d / 50d);
        double delta = 0.01;
        assertEquals("Unexpected nursery TransProb", expProb, nursery.transProb, delta);
    }

    @Test
    public void testNurseryWorkers() throws ImpossibleWorkerDistributionException {
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
                for (Nursery place : p.getPlaces().getNurseries()) {
                    staff = place.getStaff(day, i + 1);
                    totStaff += staff.size();
                }

                if (day < 5) {
                    //Staff should be at nursery during school times only
                    if (i + 1 < startTime || i +1 >= endTime) {
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
