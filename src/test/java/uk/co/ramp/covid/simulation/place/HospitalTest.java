package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

public class HospitalTest extends SimulationTest {

    @Test
    public void testHospitalTransProb() throws JsonParseException {
        Hospital hospital = new Hospital(CommunalPlace.Size.MED);
        double expProb = PopulationParameters.get().buildingProperties.baseTransmissionConstant;
        double delta = 0.01;
        assertEquals("Unexpected hospital TransProb", expProb, hospital.transConstant, delta);
    }

    @Test
    public void testHospitalWorkers() throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        Population p = new Population(populationSize);
        p.allocatePeople();
        List<Person> staff;
        Time t = new Time(0);
        //Run for a whole week
        boolean firstSkipped = false;
        for (int day = 0; day < 7; day++) {
            DailyStats s = new DailyStats(t);
            for (int i = 0; i < 24; i++) {
                // Since movement puts people in place for the *next* hour, it's easiest to check this before the timestep
                // First check is skipped to give workers time to move to work
                if (firstSkipped) {
                    for (Hospital place : p.getPlaces().getHospitals()) {
                        staff = place.getStaff(t);
                        assertTrue("Day " + day + " Time " + i  + " Unexpectedly no staff in hospital",
                                staff.size() > 0);
                    }
                }
                firstSkipped = true;
                p.timeStep(t, s);
                t.advance();
            }
        }
    }

}
