package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.co.ramp.covid.simulation.population.Person.Sex.FEMALE;
import static uk.co.ramp.covid.simulation.population.Person.Sex.MALE;

import java.util.List;

public class OfficeTest extends SimulationTest {

    @Test
    public void testOfficeTransProb() throws JsonParseException {
        Office office = new Office(CommunalPlace.Size.MED);
        double expProb = PopulationParameters.get().buildingProperties.baseTransmissionConstant;
        double delta = 0.01;
        assertEquals("Unexpected office TransProb", expProb, office.transConstant, delta);
    }

    @Test
    public void testOfficeWorkers() throws ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        Population p = PopulationGenerator.genValidPopulation(populationSize);
        p.allocatePeople();
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

    @Test
    public void testSendHome() {
        Office office = new Office(CommunalPlace.Size.MED);
        office.addPerson(new Adult(30, FEMALE));
        office.addPerson(new Adult(35, MALE));
        int time = office.times.getClose() - 1;
        office.determineMovement(new Time(time), new DailyStats(new Time(time)), false, null);
        office.commitMovement();
        int expPeople = 0;
        assertEquals("Unexpected people left in office", expPeople, office.getNumPeople());
    }

}
