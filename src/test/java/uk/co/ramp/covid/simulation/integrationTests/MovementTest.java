package uk.co.ramp.covid.simulation.integrationTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.place.householdtypes.SingleAdult;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Movement tests do not test a particular class, but check basic assumptions around movement throughout a run */
public class MovementTest extends SimulationTest {

    Population p;
    int populationSize = 10000;
    int nInfections = 10;

    @Before
    public void initialiseTestModel() {
        PopulationParameters.get().householdProperties.pWillIsolate = new Probability(1.0);

        p = PopulationGenerator.genValidPopulation(populationSize);
        p.getSeeder().forceNInfections(nInfections);
    }

    @Test
    public void allChildrenGoToSchool() {
        Set<Child> schooled = new HashSet<>();
        
        p.setPostHourHook((pop, time) -> {
            for (School school : pop.getPlaces().getSchools()) {
                for (Person c : school.getPeople()) {
                    if (c instanceof Child) {
                        schooled.add((Child) c);
                    }
                }
            }
        });
        
        p.simulate(1);
        
        long numChildren = p.getAllPeople().stream().filter(p -> p instanceof Child).count();
        
        assertEquals("Some children not at school", numChildren, schooled.size());
    }

    @Test
    public void someInfantsGoToNursery() {
        Set<Infant> nursed = new HashSet<>();
        
        p.setPostHourHook((pop, time) -> {
            for (Nursery nursery : pop.getPlaces().getNurseries()) {
                for (Person c : nursery.getPeople()) {
                    if (c instanceof Infant) {
                        nursed.add((Infant) c);
                    }
                }
            }
        });
       
        p.simulate(1);
        assertTrue("No infants at nursery", nursed.size() > 0);
    }

    @Test
    public void someAdultsGoToWork() {
        Set<Adult> working = new HashSet<>();
        
        p.setPostHourHook((pop, time) -> {
            for (CommunalPlace place : pop.getPlaces().getCommunalPlaces()) {
                for (Person per : place.getPeople()) {
                    if (per instanceof Adult) {
                        working.add((Adult) per);
                    }
                }
            }
        });
        
        p.simulate(1);
        
        assertTrue("No-one goes to work", working.size() > 0);
    }

    @Test
    public void someNonWorkersGoShopping() {
        Set<Person> shopping = new HashSet<>();
        
        p.setPostHourHook((pop, time) -> {
            for (Shop place : pop.getPlaces().getShops()) {
                for (Person per : place.getPeople()) {
                    if (per.getPrimaryCommunalPlace() != place) {
                        shopping.add(per);
                    }
                }
            }
        });
       
        p.simulate(1);
        assertTrue("No-one visits shops", shopping.size() > 0);
    }

    @Test
    public void someNonWorkersGoSToRestaurants() {
        Set<Person> eating = new HashSet<>();

        p.setPostHourHook((pop, time) -> {
            for (Restaurant place : pop.getPlaces().getRestaurants()) {
                for (Person per : place.getPeople()) {
                    if (per.getPrimaryCommunalPlace() != place) {
                        eating.add(per);
                    }
                }
            }
        });

        p.simulate(1);
        assertTrue("No-one visits restaurants", eating.size() > 0);
    }

    @Test
    public void someNonWorkersGoToHospital() {
        // Phase 2 movement set when we construct population so we need to reconstruct it here
        CovidParameters.get().diseaseParameters.pSurvivorGoesToHospital = new Probability(1.0);
        CovidParameters.get().diseaseParameters.adultProgressionPhase2 = 100.0;
        CovidParameters.get().diseaseParameters.childProgressionPhase2 = 100.0;
        CovidParameters.get().diseaseParameters.pensionerProgressionPhase2 = 100.0;

        p = PopulationGenerator.genValidPopulation(populationSize);
        p.getSeeder().forceNInfections(200);

        Set<Person> visiting = new HashSet<>();

        p.setPostHourHook((pop, time) -> {
            for (Hospital place : pop.getPlaces().getAllHospitals()) {
                for (Person per : place.getPeople()) {
                    if (per.getPrimaryCommunalPlace() != place || per.isHospitalised()) {
                        visiting.add(per);
                    }
                }
            }
        });
        
        p.simulate(10);
        assertTrue("No-one visits hospitals", visiting.size() > 0);
    }


    @Test
    public void somePeopleVisitNeighbours() {
        Set<Person> visiting = new HashSet<>();
        PopulationParameters.get().householdProperties.householdVisitsNeighbourDaily = 0.5;

        p.setPostHourHook((pop, time) -> {
            for (Household place : pop.getHouseholds()) {
                visiting.addAll(place.getVisitors());
            }
        });
        
        p.simulate(1);
        assertTrue("No-one visits neighbours", visiting.size() > 0);
    }

    @Test
    public void weDontLosePeople() {
        p.setPostHourHook((pop, time) -> {
            int npeople = 0;
            for (Place place : pop.getPlaces().getCommunalPlaces()) {
                npeople += place.getNumPeople();
            }

            for (Household hld : pop.getHouseholds()) {
                npeople += hld.getNumPeople();
            }
            assertEquals("People have been lost", populationSize, npeople);
        });

        p.simulate(1);
    }

    @Test
    public void openPlacesShouldBeStaffed() {

        p.setPostHourHook((pop, time) -> {
            for (CommunalPlace place : pop.getPlaces().getCommunalPlaces()) {
                List<Person> staff = place.getStaff(time);
                if (place.isOpen(time)) {
                    if (staff.size() == 0) {
                        break;
                    }
                    assertTrue("No staff found in open place", staff.size() > 0);
                } else {
                    assertEquals("Unexpected staff found in closed place", 0, staff.size());
                }
            }
        });
        p.simulate(1);
    }

    private void doesNotGoOut(Household iso, List<Person> isolating) {
        for (CommunalPlace place : p.getPlaces().getCommunalPlaces()) {
            for (Person per : isolating) {
                if (per.isHospitalised()) {
                    continue;
                }
                assertFalse(place.personInPlace(per));
            }
        }

        for (Household h : p.getHouseholds()) {
            if (h != iso) {
                for (Person per : isolating) {
                    assertFalse(h.personInPlace(per));
                }
            }
        }
    }

    @Test
    public void isolatingHouseholdsDontMove() {
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        Household iso = p.getHouseholds().get(0);
        iso.forceIsolationtimer(14);
        List<Person> isolating = iso.getInhabitants();

        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);
            doesNotGoOut(iso, isolating);
            t = t.advance();
        }
    }

    @Test
    public void stopIsolatingAfterTimerExpires() {
        final int daysIsolated = 2;

        Household iso = p.getHouseholds().get(0);
        iso.forceIsolationtimer(daysIsolated);
        List<Person> isolating = iso.getInhabitants();
        
        p.setPostHourHook((pop, time) -> {
            doesNotGoOut(iso, isolating);
        });

        // 2 Days isolationg
        p.simulate(daysIsolated);

        // Since arrays pass by reference this allows updating inside the lambda
        final Integer[] excursions = {0};
        p.setPostHourHook((pop, time) -> {
            for (CommunalPlace place : pop.getPlaces().getCommunalPlaces()) {
                for (Person per : isolating) {
                    if (place.personInPlace(per)) {
                        excursions[0]++;
                    }
                }
            }

            for (Household h : pop.getHouseholds()) {
                if (h != iso) {
                    for (Person per : isolating) {
                        if (h.personInPlace(per)) {
                            excursions[0]++;
                        }
                    }
                }
            }
        });

        // Now we can go out again
        p.simulateFromTime(new Time(48), 1);
        assertTrue(excursions[0] > 0);
    }

    @Test
    public void newInfectionsResetIsolationTimer() {
        int daysIsolated = 2;
        CovidParameters.get().testParameters.pDiagnosticTestAvailableHour = new Probability(0.0);
        PopulationParameters.get().personProperties.pQuarantinesIfSymptomatic = new Probability(1.0);

        Population p = PopulationGenerator.genValidPopulation(populationSize);

        Household iso = p.getHouseholds().get(0);
        iso.forceIsolationtimer(daysIsolated);
        List<Person> isolating = iso.getInhabitants();

        // Isolate for a day
        p.setPostHourHook((pop, time) -> {
            doesNotGoOut(iso, isolating);
        });
        p.simulate(1);

        // New infection happens on day 2 (hour 5)
        p.setPostHourHook((pop, t) -> {
            if (t.getHour() == 5) {
                Person per = isolating.get(0);
                per.forceInfect();
                per.getcVirus().forceSymptomatic(true);
                // Usually there's a delay before symptonms but we just force it here
                double time = per.getcVirus().getSymptomDelay() + 1;
                for (int j = 0; j < time + 1; j++) {
                    per.getcVirus().stepInfection(t);
                }
                per.cStatus();
            }
            doesNotGoOut(iso, isolating);
        });
        p.simulateFromTime(new Time(24), 1);

        // Initial 2 days are over but we should still be isolating since there's a new case
        assertTrue(iso.isIsolating());
    }

    @Test
    public void somePeopleGetTested() {
        // As most tests are positive we force lots of infections to check some go negative.
        p.getSeeder().forceNInfections(100);
        CovidParameters.get().testParameters.pDiagnosticTestAvailableHour = new Probability(1.0);
        p.simulate(30);

        int numTested = 0;
        int numNegative = 0;
        int numPositive = 0;
        for (Person p : p.getAllPeople()) {

            // Only adults/pensioners get tested
            if (p instanceof Child || p instanceof Infant) {
                assertFalse("A child was unexpectedly tested", p.wasTested());
            }

            // Count the number of positive and negative tests
            if (p.wasTested()) {
                numTested++;

                if (!p.getTestOutcome()) {
                    numNegative++;
                } else {
                    numPositive++;
                }
            }
        }
        assertTrue("No-one was tested", numTested > 0);
        assertTrue("No-one tested negative", numNegative > 0);
        assertEquals("Unexpected number of positive tests", numTested - numNegative, numPositive);
    }

    @Test
    public void negativeTestsExitQuarantine() {
        Time t = new Time(24);

        Household iso = null;
        for (Household h : p.getHouseholds()) {
            if (h instanceof SingleAdult) {
                iso = h;
                break;
            }
        }

        iso.forceIsolationtimer(14);
        Person per = iso.getInhabitants().get(0);
        per.forceQuarantine();

        per.forceInfect();
        per.getcVirus().forceSymptomatic(true);
        // Usually there's a delay before symptonms but we just force it here
        double time = per.getcVirus().getSymptomDelay() + 1;
        for (int j = 0; j < time; j++) {
            per.getcVirus().stepInfection(t);
        }
        per.cStatus();

        CovidParameters.get().testParameters.pDiagnosticTestDetectsSuccessfully = new Probability(0.0);
        per.getTested();
        assertTrue(per.wasTested());
        assertFalse(per.getTestOutcome());
        assertFalse(per.isQuarantined());
        assertFalse(iso.isIsolating());
    }

    @Test
    public void positiveTestsStayInQuarantine() {
        Time t = new Time(0);
        PopulationParameters.get().personProperties.pQuarantinesIfSymptomatic = new Probability(1.0);
        CovidParameters.get().diseaseParameters.adultProgressionPhase2 = 100.0;
        p = PopulationGenerator.genValidPopulation(populationSize);
        p.getSeeder().forceNInfections(nInfections);

        Household iso = null;
        for (Household h : p.getHouseholds()) {
            if (h instanceof SingleAdult) {
                iso = h;
                break;
            }
        }

        iso.forceIsolationtimer(14);
        Person per = iso.getInhabitants().get(0);
        per.forceQuarantine();

        per.forceInfect();
        per.getcVirus().forceSymptomatic(true);

        //  Usually there's a delay before symptonms but we just force it here
        while (!per.getcVirus().isSymptomatic()) {
            per.getcVirus().stepInfection(t);
        }
        per.cStatus();

        CovidParameters.get().testParameters.pDiagnosticTestDetectsSuccessfully = new Probability(1.0);
        per.getTested();
        assertTrue(per.wasTested());
        assertTrue(per.getTestOutcome());
        assertTrue(per.isQuarantined());
        assertTrue(iso.isIsolating());
    }

    @Test
    public void neighboursShouldLeaveEmptyHouses() {
        final int simDays = 5;
        p = PopulationGenerator.genValidPopulation(populationSize);
        p.getSeeder().forceNInfections(nInfections);

        // Going to an empty neighbours house is okay, but only for 1 hour (when you discover they aren't in)
        List<Set<Person>> inEmptyNeighbourHouse = new ArrayList<>();
        
        p.setPostHourHook((population, time) -> {
            inEmptyNeighbourHouse.add(new HashSet<>());
            for (Household h : population.getHouseholds()) {
                if (h.getNumInhabitants() == 0) {
                    inEmptyNeighbourHouse.get(time.getAbsTime() - 1).addAll(h.getVisitors());
                }
            }
        });
        
        p.simulate(simDays);

        for (int i = 0; i < simDays * 24 - 1; i++) {
            for (Person p : inEmptyNeighbourHouse.get(i)) {
                assertFalse(inEmptyNeighbourHouse.get(i+1).contains(p));
            }
        }
    }

    @Test
    public void peopleAreInASinglePlace() {
        final int simDays = 5;

        p = PopulationGenerator.genValidPopulation(populationSize);
        p.getSeeder().forceNInfections(nInfections);

        p.setPostHourHook((pop, time) -> {
            Set<Person> seen = new HashSet<>();

            for (Place place : p.getPlaces().getCommunalPlaces()) {
                for (Person per : place.getPeople()) {
                    assertTrue(seen.add(per));
                }
            }

            for (Household h : p.getHouseholds()) {
                for (Person per : h.getPeople()) {
                    assertTrue(seen.add(per));
                }
            }
        });
        
        p.simulate(simDays);

    }

    @Test
    public void peopleAttendHospitalAppts() {
        final int simDays = 14;

        // We need to force > 1 hospital, else it will be designated COVID and no accept patients
        PopulationParameters.get().buildingDistribution.populationToHospitalsRatio = 5000;
        p = PopulationGenerator.genValidPopulation(populationSize);
        p.getSeeder().forceNInfections(nInfections);

        // Final arrays to allow variable capture in the lambda
        final int[] hospitalApptVisitors = {0};
        final int[] hospitalApptCare = {0};
        p.setPostHourHook((pop, time) -> {
            for (Hospital h : pop.getPlaces().getAllHospitals()) {
                for (Person p : h.getPeople()) {
                    if (h.isPatient(p, time)) {
                        hospitalApptVisitors[0]++;
                        if (p.isInCare()) {
                            hospitalApptCare[0]++;
                        }
                    }
                }
            }
        });

        p.simulate(simDays);
        
        assertNotEquals("No one had a hospital appt", 0, hospitalApptVisitors[0]);
        assertNotEquals("No one from care had a hospital appt", 0, hospitalApptCare[0]);
    }

}
