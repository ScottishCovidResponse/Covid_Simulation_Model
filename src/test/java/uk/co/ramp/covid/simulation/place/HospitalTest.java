package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

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

    @Test
    public void somePeopleDieInHospital() {
        int population = 10000;
        int nInfections = 300;
        int nIter = 1;
        int nDays = 60;
        int RNGSeed = 42;

        Model m = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> stats = m.run(0);

        int totalHospitalDeaths = 0;
        for (DailyStats s : stats.get(0)) {
            totalHospitalDeaths += s.getHospitalDeaths();
        }
        assertTrue("Some people should die in hospital", totalHospitalDeaths > 0);
    }

    @Test
    public void recoveredPeopleLeaveHospital() {
        int populationSize = 10000;
        int nInfections = 100;
        CovidParameters.get().hospitalisationParameters.pPhase2GoesToHosptial = new Probability(1.0);
        CovidParameters.get().diseaseParameters.adultProgressionPhase2 = 100.0;
        CovidParameters.get().diseaseParameters.mortalityRate = 0.0;

        Population pop = PopulationGenerator.genValidPopulation(populationSize);
        pop.seedVirus(nInfections);

        Person inf = null;
        for (Person p : pop.getAllPeople()) {
            if (p instanceof Adult) {
                inf = p;
                break;
            }
        }

        inf.infect();
        inf.getcVirus().forceSymptomatic(true);

        Time t = new Time();
        while (!inf.isHospitalised()) {
            pop.timeStep(t, new DailyStats(t));
            t.advance();
        }
        assertTrue(inf.isHospitalised());
        assertFalse(inf.isRecovered());

        boolean inHospital = false;
        for (Hospital h : pop.getPlaces().getHospitals()) {
            if (h.personInPlace(inf) && inf.isHospitalised()) {
                inHospital = true;
            }
        }
        assertTrue(inHospital);

        double time = inf.getcVirus().getP2() + 48;

        while (!inf.isRecovered()) {
            pop.timeStep(t, new DailyStats(t));
            t.advance();
        }

        // Give some time to let them get home
        for (int i = 0; i < 10; i++) {
            pop.timeStep(t, new DailyStats(t));
            t.advance();
        }

        assertTrue(inf.isRecovered());
        assertFalse(inf.isHospitalised());

        // Don't check explicitly for atHome since they could be back at work already
        inHospital = false;
        for (Hospital h : pop.getPlaces().getHospitals()) {
            if (h.personInPlace(inf) && inf.isHospitalised()) {
                inHospital = true;
            }
        }
        assertFalse(inHospital);
    }

    @Test
    public void peopleInCareNeverGoToCovidHospital() {
        final int populationSize = 10000;
        final int nInfections = 500;
        final int hours = 168;

        PopulationParameters.get().pensionerProperties.pEntersCareHome = new Probability(0.9);
        CovidParameters.get().hospitalisationParameters.pPhase2GoesToHosptial = new Probability(1.0);
        CovidParameters.get().diseaseParameters.pensionerProgressionPhase2 = 100.0;

        Population pop = PopulationGenerator.genValidPopulation(populationSize);
        pop.seedVirus(nInfections);

        Time t = new Time(0);
        DailyStats s = new DailyStats(t);
        for (int hour = 0; hour < hours; hour++) {
            pop.timeStep(t, s);
            t.advance();
            for (CovidHospital h : pop.getPlaces().getCovidHospitals()) {
                for (Person p : h.getPeople()) {
                    assertFalse(p.isInCare());
                }
            }
        }
    }


    @Test
    public void transmissionAdjustmentApplied() {
        int populationSize = 10000;
        int nInfections = 100;
        CovidParameters.get().hospitalisationParameters.pPhase2GoesToHosptial = new Probability(1.0);
        CovidParameters.get().diseaseParameters.adultProgressionPhase2 = 100.0;
        CovidParameters.get().diseaseParameters.mortalityRate = 0.0;

        Population pop = PopulationGenerator.genValidPopulation(populationSize);
        pop.seedVirus(nInfections);

        Person inf = null;
        for (Person p : pop.getAllPeople()) {
            if (p instanceof Adult) {
                inf = p;
                break;
            }
        }

        inf.infect();
        inf.getcVirus().forceSymptomatic(true);

        Time t = new Time();
        while (!inf.isHospitalised()) {
            pop.timeStep(t, new DailyStats(t));
            t.advance();
        }
        assertTrue(inf.isHospitalised());

        CovidHospital hosptial = null;
        for (CovidHospital h : pop.getPlaces().getCovidHospitals()) {
            if (h.personInPlace(inf)) {
                hosptial = h;
            }
        }
        
        assertTrue(hosptial.getBaseTransP(inf) > hosptial.getTransP(t, inf, null));
    }


}
