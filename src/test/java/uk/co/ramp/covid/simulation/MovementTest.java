package uk.co.ramp.covid.simulation;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Movement tests do not test a particular class, but check basic assumptions around movement throughout a run */
public class MovementTest {

    Population p;
    int populationSize = 10000;
    int nInfections = 10;

    @Before
    public void initialiseTestModel() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        PopulationParameters.get().setpHouseholdWillIsolate(100.0);

        p = PopulationGenerator.genValidPopulation(populationSize);
        p.seedVirus(nInfections);
    }

    @Test
    public void allChildrenGoToSchool() {
        Set<Child> schooled = new HashSet();
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);
            
            for (School school : p.getPlaces().getSchools()) {
                for (Person c : school.getPeople()) {
                    if (c instanceof Child) {
                        schooled.add( (Child) c);
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
        
        assertEquals(numChildren, schooled.size());
    }

    @Test
    public void someInfantsGoToNursery() {
        Set<Infant> nursed = new HashSet();
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);

            for (Nursery nursery : p.getPlaces().getNurseries()) {
                for (Person c : nursery.getPeople()) {
                    if (c instanceof Infant) {
                        nursed.add( (Infant) c);
                    }
                }
            }

            t = t.advance();
        }

        // TODO: We can check specifics once we know how many infants go to nursery.
        // This is not trivial since not all infants who go to nursery will go on day 1.
        assertTrue(nursed.size() > 0);
    }

    @Test
    public void someAdultsGoToWork() {
        Set<Adult> working = new HashSet();
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);

            for (CommunalPlace place : p.getPlaces().getAllPlaces()) {
                for (Person per : place.getPeople()) {
                    if (per instanceof Adult) {
                        working.add( (Adult) per);
                    }
                }
            }
            t = t.advance();
        }

        assertTrue(working.size() > 0);
    }

    @Test
    public void someNonWorkersGoShopping() {
        Set<Person> shopping = new HashSet();
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

        assertTrue(shopping.size() > 0);
    }

    @Test
    public void someNonWorkersGoSToRestaurants() {
        Set<Person> eating = new HashSet();
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

        assertTrue(eating.size() > 0);
    }

    @Test
    public void somePeopleVisitNeighbours() {
        Set<Person> visiting = new HashSet();
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);

            for (Household place : p.getHouseholds()) {
                visiting.addAll(place.getVisitors());
            }
            t = t.advance();
        }
        assertTrue(visiting.size() > 0);
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
            assertEquals(populationSize, npeople);
            t = t.advance();
        }
    }

    @Ignore // Waiting for the integration Tests branch
    @Test
    public void openPlacesShouldBeStaffed() {
        int day = 1;
        Time t = new Time(24);
        DailyStats s = new DailyStats(t);
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, s);

            int npeople = 0;
            for (CommunalPlace place : p.getPlaces().getAllPlaces()) {
                // i + 1 since the ith timestep has already been (so we are in the next state)
                if (place.isOpen(day, i + 1)) {
                    List<Person> staff = place.getStaff(t);
                    assertTrue(staff.size() > 0);
                }
            }
            
            t = t.advance();

        }
    }
    
    private void doesNotGoOut(Household iso, List<Person> isolating) {
        for (CommunalPlace place : p.getPlaces().getAllPlaces()) {
            for (Person per : isolating) {
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
                for (int j = 0; j < time; j++) {
                    per.getcVirus().stepInfection(t);
                }
                per.cStatus();
            }
            doesNotGoOut(iso, isolating);
        }
        p.getHouseholds().forEach(h -> h.dayEnd());

        // Initial 2 days are over but we should still be isolating since there's a new case
        assertTrue(iso.isIsolating());
    }
}
