package uk.co.ramp.covid.simulation.integrationTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.place.householdtypes.SingleAdult;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
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
        p.seedVirus(nInfections);
    }

    @Test
    public void allChildrenGoToSchool() {
        Set<Child> schooled = new HashSet<>();
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);

            for (School school : p.getPlaces().getSchools()) {
                for (Person c : school.getPeople()) {
                    if (c instanceof Child) {
                        schooled.add((Child) c);
                    }
                }
            }

            t = t.advance();
        }

        int numChildren = 0;
        for (Person per : p.getAllPeople()) {
            if (per instanceof Child) {
                numChildren++;
            }
        }

        assertEquals("Some children not at school", numChildren, schooled.size());
    }

    @Test
    public void someInfantsGoToNursery() {
        Set<Infant> nursed = new HashSet<>();
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);

            for (Nursery nursery : p.getPlaces().getNurseries()) {
                for (Person c : nursery.getPeople()) {
                    if (c instanceof Infant) {
                        nursed.add((Infant) c);
                    }
                }
            }

            t = t.advance();
        }

        // TODO: We can check specifics once we know how many infants go to nursery.
        // This is not trivial since not all infants who go to nursery will go on day 1.
        assertTrue("No infants at nursery", nursed.size() > 0);
    }

    @Test
    public void someAdultsGoToWork() {
        Set<Adult> working = new HashSet<>();
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);

            for (CommunalPlace place : p.getPlaces().getAllPlaces()) {
                for (Person per : place.getPeople()) {
                    if (per instanceof Adult) {
                        working.add((Adult) per);
                    }
                }
            }
            t = t.advance();
        }

        assertTrue("No-one goes to work", working.size() > 0);
    }

    @Test
    public void someNonWorkersGoShopping() {
        Set<Person> shopping = new HashSet<>();
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);

            for (Shop place : p.getPlaces().getShops()) {
                for (Person per : place.getPeople()) {
                    // TODO-FIXME: Technically shop workers can also shop where they work if they are not currently working.
                    if (per.getPrimaryCommunalPlace() != place) {
                        shopping.add(per);
                    }
                }
            }
            t = t.advance();
        }

        assertTrue("No-one visits shops", shopping.size() > 0);
    }

    @Test
    public void someNonWorkersGoSToRestaurants() {
        Set<Person> eating = new HashSet<>();
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);

            for (Restaurant place : p.getPlaces().getRestaurants()) {
                for (Person per : place.getPeople()) {
                    // TODO-FIXME: Technically restaurant workers can also eat where they work if they are not currently working.
                    if (per.getPrimaryCommunalPlace() != place) {
                        eating.add(per);
                    }
                }
            }
            t = t.advance();
        }

        assertTrue("No-one visits restaurants", eating.size() > 0);
    }

    @Test
    public void someNonWorkersGoToHospital() {
        // Phase 2 movement set when we construct population so we need to reconstruct it here
        CovidParameters.get().hospitalisationParameters.pPhase2GoesToHosptial = new Probability(1.0);
        p = PopulationGenerator.genValidPopulation(populationSize);
        p.seedVirus(200);

        Set<Person> visiting = new HashSet<>();
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 120; i++) {
            p.timeStep(t, s);
            for (Hospital place : p.getPlaces().getHospitals()) {
                for (Person per : place.getPeople()) {
                    if (per.getPrimaryCommunalPlace() != place || per.isHospitalised()) {
                        visiting.add(per);
                    }
                }
            }
            t = t.advance();
        }
        assertTrue("No-one visits hospitals", visiting.size() > 0);
    }


    @Test
    public void somePeopleVisitNeighbours() {
        Set<Person> visiting = new HashSet<>();
        Time t = new Time(24);
        PopulationParameters.get().householdProperties.householdVisitsNeighbourDaily = 0.5;
        p.getHouseholds().forEach(h -> h.determineDailyNeighbourVisit());
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);

            for (Household place : p.getHouseholds()) {
                visiting.addAll(place.getVisitors());
            }
            t = t.advance();
        }
        assertTrue("No-one visits neighbours", visiting.size() > 0);
    }

    @Test
    public void weDontLosePeople() {
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);

            int npeople = 0;
            for (Place place : p.getPlaces().getAllPlaces()) {
                npeople += place.getPeople().size();
            }

            for (Household hld : p.getHouseholds()) {
                npeople += hld.getPeople().size();
            }
            assertEquals("People have been lost", populationSize, npeople);
            t = t.advance();
        }
    }

    @Test
    public void openPlacesShouldBeStaffed() {
        int day = 1;
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);
            t = t.advance();

            for (CommunalPlace place : p.getPlaces().getAllPlaces()) {
                List<Person> staff = place.getStaff(t);
                if (place.isOpen(day, t.getHour())) {
                    assertTrue("No staff found in open place", staff.size() > 0);
                } else {
                    assertEquals("Unexpected staff found in closed place", 0, staff.size());
                }
            }
        }
    }

    private void doesNotGoOut(Household iso, List<Person> isolating) {
        for (CommunalPlace place : p.getPlaces().getAllPlaces()) {
            for (Person per : isolating) {
                if (per.isHospitalised()) {
                    continue;
                }
                assertFalse(place.getPeople().contains(per));
            }
        }

        for (Household h : p.getHouseholds()) {
            if (h != iso) {
                for (Person per : isolating) {
                    assertFalse(h.getPeople().contains(per));
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
        int daysIsolated = 2;
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        Household iso = p.getHouseholds().get(0);
        iso.forceIsolationtimer(daysIsolated);
        List<Person> isolating = iso.getInhabitants();

        // Handle the first isolation day
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);
            doesNotGoOut(iso, isolating);
            t = t.advance();
        }
        p.getHouseholds().forEach(h -> h.dayEnd());

        // Second day isolating
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);
            doesNotGoOut(iso, isolating);
            t = t.advance();
        }
        p.getHouseholds().forEach(h -> h.dayEnd());

        // Now we can go out again
        int excursions = 0;
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);

            for (CommunalPlace place : p.getPlaces().getAllPlaces()) {
                for (Person per : isolating) {
                    if (place.getPeople().contains(per)) {
                        excursions++;
                    }
                }
            }

            for (Household h : p.getHouseholds()) {
                if (h != iso) {
                    for (Person per : isolating) {
                        if (h.getPeople().contains(per)) {
                            excursions++;
                        }
                    }
                }
            }
            t = t.advance();
        }
        assertTrue(excursions > 0);
    }

    @Test
    public void newInfectionsResetIsolationTimer() {
        int daysIsolated = 2;

        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        Household iso = p.getHouseholds().get(0);
        iso.forceIsolationtimer(daysIsolated);
        List<Person> isolating = iso.getInhabitants();

        // Handle the first isolation day
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);
            doesNotGoOut(iso, isolating);
            t = t.advance();
        }
        p.getHouseholds().forEach(h -> h.dayEnd());

        // Second day isolating
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);
            if (i == 5) {
                Person per = isolating.get(0);
                per.infect();
                per.getcVirus().forceSymptomatic(true);
                // Usually there's a delay before symptonms but we just force it here
                double time = per.getcVirus().getSymptomDelay() + 1;
                for (int j = 0; j < time + 1; j++) {
                    per.getcVirus().stepInfection(t);
                }
                per.cStatus();
            }
            doesNotGoOut(iso, isolating);
            t = t.advance();
        }
        p.getHouseholds().forEach(h -> h.dayEnd());

        // Initial 2 days are over but we should still be isolating since there's a new case
        assertTrue(iso.isIsolating());
    }

    @Test
    public void somePeopleGetTested() {
        // As most tests are positive we force lots of infections to check some go negative.
        p.seedVirus(100);
        CovidParameters.get().testParameters.pDiagnosticTestAvailable = new Probability(1.0);
        p.simulate(50);

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
                if (!p.getTestOutcome().get()) {
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

        per.infect();
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
        assertFalse(per.getTestOutcome().get());
        assertFalse(per.getQuarantine());
        assertFalse(iso.isIsolating());
    }

    @Test
    public void positiveTestsStayInQuarantine() {
        Time t = new Time(0);
        PopulationParameters.get().personProperties.pQuarantinesIfSymptomatic = new Probability(1.0);
        p = PopulationGenerator.genValidPopulation(populationSize);
        p.seedVirus(nInfections);

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

        per.infect();
        per.getcVirus().forceSymptomatic(true);
        // Usually there's a delay before symptonms but we just force it here
        double time = per.getcVirus().getSymptomDelay() + 1;
        for (int j = 0; j < time; j++) {
            per.getcVirus().stepInfection(t);
        }
        per.cStatus();

        CovidParameters.get().testParameters.pDiagnosticTestDetectsSuccessfully = new Probability(1.0);
        per.getTested();
        assertTrue(per.wasTested());
        assertTrue(per.getTestOutcome().get());
        assertTrue(per.getQuarantine());
        assertTrue(iso.isIsolating());
    }

    @Test
    public void neighboursShouldLeaveEmptyHouses() {
        final int simHours = 100;
        Time t = new Time(0);
        p = PopulationGenerator.genValidPopulation(populationSize);
        p.seedVirus(nInfections);

        // Going to an empty neighbours house is okay, but only for 1 hour (when you discover they aren't in)
        List<Set<Person>> inEmptyNeighbourHouse = new ArrayList<>();
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < simHours; i++) {
            inEmptyNeighbourHouse.add(new HashSet<>());

            p.timeStep(t, s);
            t = t.advance();

            for (Household h : p.getHouseholds()) {
                if (h.getNumInhabitants() == 0) {
                    inEmptyNeighbourHouse.get(i).addAll(h.getVisitors());
                }
            }

        }

        for (int i = 0; i < simHours - 1; i++) {
            for (Person p : inEmptyNeighbourHouse.get(i)) {
                assertFalse(inEmptyNeighbourHouse.get(i+1).contains(p));
            }
        }
    }
}
