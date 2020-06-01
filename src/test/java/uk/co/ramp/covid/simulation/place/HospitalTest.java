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

public class HospitalTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testHospitalTransProb() throws JsonParseException {
        Hospital hospital = new Hospital();
        double expProb = PopulationParameters.get().getpBaseTrans() * 15d / (5000d / 10d);
        double delta = 0.01;
        assertEquals("Unexpected hospital TransProb", expProb, hospital.transProb, delta);
    }

    @Ignore("Failing Test")
    @Test
    public void testHospitalWorkers() throws ImpossibleAllocationException {
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
            DailyStats s = new DailyStats(day);
            for (int i = 0; i < 24; i++) {
                p.timeStep(day, i, s);
                //There should always be staff in hospitals
                for (Hospital place : p.getPlaces().getHospitals()) {
                    staff = place.getStaff(day, i);
                    assertTrue("Day " + day + " Time " + i +" Unexpectedly no staff in hospital", staff.size() > 0);
                }
            }
        }
    }

}
