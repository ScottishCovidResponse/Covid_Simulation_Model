package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;

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
        Office office = new Office(CommunalPlace.Size.MED);
        double expProb = PopulationParameters.get().getpBaseTrans();
        double delta = 0.01;
        assertEquals("Unexpected office TransProb", expProb, office.transProb, delta);
    }

    @Test
    public void testOfficeWorkers() throws ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        int nInfections = 10;

        Population p = PopulationGenerator.genValidPopulation(populationSize);
        p.allocatePeople();
        p.seedVirus(nInfections);
        List<Person> staff;
        Time t = new Time(0);
        //Run for a whole week
        for (int day = 0; day < 7; day++) {
            int totStaff;
            int startTime = Shifts.nineFiveFiveDays().getShift(day).getStart();
            int endTime = Shifts.nineFiveFiveDays().getShift(day).getEnd();
            DailyStats s = new DailyStats(t);
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                t = t.advance();
                totStaff = 0;
                for (Office place : p.getPlaces().getOffices()) {
                    staff = place.getStaff(t);
                    totStaff += staff.size();
                }

                if (day < 5) {

                    //Staff should be in offices during working hours only
                    if (i + 1< startTime || i + 1 >= endTime) {
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
