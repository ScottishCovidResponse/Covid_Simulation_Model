package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.RNG;

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
        RNG.seed(123);
        Nursery nursery = new Nursery();
        double expProb = PopulationParameters.get().getpBaseTrans() * 30d / (34000d / 50d);
        double delta = 0.01;
        assertEquals("Unexpected nursery TransProb", expProb, nursery.transProb, delta);
    }

    @Test
    public void testNurseryWorkers() throws ImpossibleAllocationException {
        int populationSize = 10000;
        int nHouseholds = 2000;
        int nInfections = 10;

        Population p = new Population(populationSize, nHouseholds);
        p.populateHouseholds();
        p.createMixing();
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
                    staff = place.getStaff(day, i);
                    totStaff += staff.size();
                }

                if (day < 5) {
                    //Staff should be at nursery during school times only
                    if (i < startTime || i >= endTime - 1) {
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