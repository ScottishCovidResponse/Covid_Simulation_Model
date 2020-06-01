package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Ignore;
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

public class OfficeTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testOfficeTransProb() throws JsonParseException {
        RNG.seed(123);
        Office office = new Office();
        double expProb = PopulationParameters.get().getpBaseTrans() * 10d / (10000d / 400d);
        double delta = 0.01;
        assertEquals("Unexpected office TransProb", expProb, office.transProb, delta);
    }

    @Ignore("Failing Test")
    @Test
    public void testOfficeWorkers() throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        int nHouseholds = 2000;
        int nInfections = 10;

        Population p = new Population(populationSize);
        p.allocatePeople();
        p.seedVirus(nInfections);
        List<Person> staff;
        //Run for a whole week
        for (int day = 0; day < 7; day++) {
            int totStaff;
            int startTime = Shifts.nineFiveFiveDays().getShift(day).getStart();
            int endTime = Shifts.nineFiveFiveDays().getShift(day).getEnd();
            DailyStats s = new DailyStats(day);
            for (int i = 0; i < 24; i++) {
                p.timeStep(day, i, s);
                totStaff = 0;
                for (Office place : p.getPlaces().getOffices()) {
                    staff = place.getStaff(day, i);
                    totStaff += staff.size();
                }

                if (day < 5) {

                    //Staff should be in offices during working hours only
                    if (i < startTime || i >= endTime - 1) {
                        assertEquals("Unexpected staff in office", 0, totStaff);
                    } else {
                        assertTrue("Unexpectedly no staff in office", totStaff > 0);
                    }
                } else {
                    //Staff should not be in offices on weekends
                    assertEquals("Unexpected staff in office", 0, totStaff);
                }
            }

        }
    }
}