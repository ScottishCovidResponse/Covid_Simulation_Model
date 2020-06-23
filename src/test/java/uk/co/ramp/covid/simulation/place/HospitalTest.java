package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static uk.co.ramp.covid.simulation.population.Person.Sex.FEMALE;
import static uk.co.ramp.covid.simulation.population.Person.Sex.MALE;

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
                    for (Hospital place : p.getPlaces().getAllHospitals()) {
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

        assert inf != null;
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
        for (Hospital h : pop.getPlaces().getAllHospitals()) {
            if (h.personInPlace(inf) && inf.isHospitalised()) {
                inHospital = true;
            }
        }
        assertTrue(inHospital);

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
        for (Hospital h : pop.getPlaces().getAllHospitals()) {
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

        assertNotNull("No person found", inf);
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

        assertNotNull("No hospitals found", hosptial);
        assertTrue(hosptial.getBaseTransP(inf) > hosptial.getTransP(t, inf, null));
    }

    @Test
    public void nonCovidHospitalsReduceStaffOnLockdown() {
        int populationSize = 10000;
        int nInfections = 100;

        // Need to force some non-covid hospitals
        PopulationParameters.get().buildingDistribution.populationToHospitalsRatio = 3000;
        Population pop = PopulationGenerator.genValidPopulation(populationSize);
        pop.seedVirus(nInfections);

        Set<Person> hospitalWorkersPreLockdown = new HashSet<>();
        pop.simulate(1);
        for (Hospital h : pop.getPlaces().getAllHospitals()) {
            hospitalWorkersPreLockdown.addAll(h.getStaff(Time.timeFromDay(1)));
        }
        
        pop.getLockdownController().setLockdown(Time.timeFromDay(2), Time.timeFromDay(4), 1.0);
        
        Set<Person> hospitalWorkersInLockdown = new HashSet<>();
        pop.simulateFromTime(Time.timeFromDay(1), 2);
        for (Hospital h : pop.getPlaces().getAllHospitals()) {
            hospitalWorkersInLockdown.addAll(h.getStaff(Time.timeFromDay(3)));
        }
        
        assertTrue("More hospital workers during lockdown than before",
                hospitalWorkersInLockdown.size() < hospitalWorkersPreLockdown.size());

    }

    @Test
    public void testSendHome() {
        Hospital hospital = new CovidHospital(CommunalPlace.Size.MED);
        Home h = new CareHome(CommunalPlace.Size.MED);
        Adult adult1 = new Adult(30, FEMALE);
        Adult adult2 = new Adult(30, MALE);
        adult1.setHome(h);
        adult2.setHome(h);
        hospital.addPerson(adult1);
        hospital.addPerson(adult2);
        hospital.determineMovement(new Time(0), new DailyStats(new Time(0)), false, null);
        hospital.commitMovement();
        int expPeople = 0;
        assertEquals("Unexpected people left in hospital", expPeople, hospital.getNumPeople());
    }

    @Test
    public void childrenAreAcompaniedByAdultsForApts() {
        int simDays = 7;
        int populationSize = 10000;
        int nInfections = 100;

        // Need to force some non-covid hospitals
        PopulationParameters.get().buildingDistribution.populationToHospitalsRatio = 3000;
        Population pop = PopulationGenerator.genValidPopulation(populationSize);
        pop.seedVirus(nInfections);

        Time t = new Time(0);
        for (int i = 0; i < simDays; i++) {
            for (Person per : pop.getAllPeople()) {
                per.deteremineHospitalVisits(t, false, pop.getPlaces());
            }

            for (int j = 0; j < 24; j++) {
                pop.timeStep(t, new DailyStats(t));
                t = t.advance();

                for (Hospital h : pop.getPlaces().getAllHospitals()) {
                    for (Person p : h.getPeople()) {
                        if (p.hasHospitalAppt() && !p.getHospitalAppt().isOver(t) 
                                && (p instanceof Child || p instanceof Infant)) {

                            Person acompanying = null;
                            for (Person p2 : h.getPeople()) {
                                if (p != p2 && p2.getHome() == p.getHome() && p2.hasHospitalAppt()
                                        && (p2 instanceof Adult || p2 instanceof Pensioner)) {
                                    acompanying = p2;
                                }
                            }
                            assertNotNull(acompanying);

                        }
                    }
                }
            }
        }
    }

}
