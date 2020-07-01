package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
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

        p.setPostHourHook((pop, time) -> {
            int totStaff = 0;
            for (Office place : pop.getPlaces().getOffices()) {
                List<Person> staff = place.getStaff(time);
                totStaff += staff.size();
            }

            if (time.getDay() < 5) {
                int startTime = Shifts.nineFiveFiveDays().getShift(time.getDay()).getStart();
                int endTime = Shifts.nineFiveFiveDays().getShift(time.getDay()).getEnd();

                //Staff should be in offices during working hours only
                if (time.getHour() < startTime || time.getHour() >= endTime) {
                    assertEquals("Unexpected staff in office", 0, totStaff);
                } else {
                    assertTrue("Unexpectedly no staff in office", totStaff > 0);
                }
            } else {
                //Staff should not be in offices on weekends
                assertEquals("Unexpected staff in office", 0, totStaff);
            }
        });
        
        p.simulate(7);
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
