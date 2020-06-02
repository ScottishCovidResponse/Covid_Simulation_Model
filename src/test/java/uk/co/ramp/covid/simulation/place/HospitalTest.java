package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

public class HospitalTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testHospitalTransProb() throws JsonParseException {
        Hospital hospital = new Hospital(CommunalPlace.Size.MED);
        double expProb = PopulationParameters.get().getpBaseTrans() * 15d / (5000d / 10d);
        double delta = 0.01;
        assertEquals("Unexpected hospital TransProb", expProb, hospital.transProb, delta);
    }

    @Test
    public void testHospitalWorkers() throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        int nInfections = 10;

        Population p = new Population(populationSize);
        p.allocatePeople();
        p.seedVirus(nInfections);
        List<Person> staff;
        //Run for a whole week
        boolean firstSkipped = false;
        for (int day = 0; day < 7; day++) {
            DailyStats s = new DailyStats(day);
            for (int i = 0; i < 24; i++) {
                // Since movement puts people in place for the *next* hour, it's easiest to check this before the timestep
                // First check is skipped to give workers time to move to work
                if (firstSkipped) {
                    for (Hospital place : p.getPlaces().getHospitals()) {
                        staff = place.getStaff(day, i);
                        assertTrue("Day " + day + " Time " + i  + " Unexpectedly no staff in hospital",
                                staff.size() > 0);
                    }
                   
                }
                p.timeStep(day, i, s);
                firstSkipped = true;
            }
        }
    }

}
