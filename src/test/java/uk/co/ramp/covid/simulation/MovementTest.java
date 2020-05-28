package uk.co.ramp.covid.simulation;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.population.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Movement tests do not test a particular class, but check basic assumptions around movement throughout a run */
public class MovementTest {

    Population p;
    int populationSize = 10000;
    int nHouseholds = 2000;
    int nInfections = 10;

    @Before
    public void initialiseTestModel() throws ImpossibleAllocationException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");

        p = new Population(populationSize, nHouseholds);
        p.populateHouseholds();
        p.createMixing();
        p.allocatePeople();
        p.seedVirus(nInfections);
    }

    @Test
    public void allChildrenGoToSchool() {
        int day = 1;
        Set<Child> schooled = new HashSet();
        DailyStats s = new DailyStats(day);
        for (int i = 0; i < 24; i++) {
            p.timeStep(day, i, s);
            
            for (School school : p.getPlaces().getSchools()) {
                for (Person c : school.getPeople()) {
                    if (c instanceof Child) {
                        schooled.add( (Child) c);
                    }
                }
            }
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
        int day = 1;
        Set<Infant> nursed = new HashSet();
        DailyStats s = new DailyStats(day);
        for (int i = 0; i < 24; i++) {
            p.timeStep(day, i, s);

            for (Nursery nursery : p.getPlaces().getNurseries()) {
                for (Person c : nursery.getPeople()) {
                    if (c instanceof Infant) {
                        nursed.add( (Infant) c);
                    }
                }
            }
        }

        // TODO: We can check specifics once we know how many infants go to nursery.
        // This is not trivial since not all infants who go to nursery will go on day 1.
        assertTrue(nursed.size() > 0);
    }

    @Test
    public void someAdultsGoToWork() {
        int day = 1;
        Set<Adult> working = new HashSet();
        DailyStats s = new DailyStats(day);
        for (int i = 0; i < 24; i++) {
            p.timeStep(day, i, s);

            for (CommunalPlace place : p.getPlaces().getAllPlaces()) {
                for (Person per : place.getPeople()) {
                    if (per instanceof Adult) {
                        working.add( (Adult) per);
                    }
                }
            }
        }

        assertTrue(working.size() > 0);
    }

    @Test
    public void someNonWorkersGoShopping() {
        int day = 1;
        Set<Person> shopping = new HashSet();
        DailyStats s = new DailyStats(day);
        for (int i = 0; i < 24; i++) {
            p.timeStep(day, i, s);

            for (Shop place : p.getPlaces().getShops()) {
                for (Person per : place.getPeople()) {
                    // TODO-FIXME: Technically shop workers can also shop where they work if they are not currently working.
                    if (per.getPrimaryCommunalPlace() != place) {
                        shopping.add(per);
                    }
                }
            }
        }

        assertTrue(shopping.size() > 0);
    }

    @Test
    public void someNonWorkersGoSToRestaurants() {
        int day = 1;
        Set<Person> eating = new HashSet();
        DailyStats s = new DailyStats(day);
        for (int i = 0; i < 24; i++) {
            p.timeStep(day, i, s);

            for (Restaurant place : p.getPlaces().getRestaurants()) {
                for (Person per : place.getPeople()) {
                    // TODO-FIXME: Technically restaurant workers can also eat where they work if they are not currently working.
                    if (per.getPrimaryCommunalPlace() != place) {
                        eating.add(per);
                    }
                }
            }
        }

        assertTrue(eating.size() > 0);
    }

    @Test
    public void somePeopleVisitNeighbours() {
        int day = 1;
        Set<Person> visiting = new HashSet();
        DailyStats s = new DailyStats(day);
        for (int i = 0; i < 24; i++) {
            p.timeStep(day, i, s);

            for (Household place : p.getHouseholds()) {
                visiting.addAll(place.getVisitors());
            }
        }
        assertTrue(visiting.size() > 0);
    }

    @Test
    public void weDontLosePeople() {
        int day = 1;
        DailyStats s = new DailyStats(day);
        for (int i = 0; i < 24; i++) {
            p.timeStep(day, i, s);

            int npeople = 0;
            for (Place place : p.getPlaces().getAllPlaces()) {
                npeople += place.getPeople().size();
            }

            for (Household hld : p.getHouseholds()) {
                npeople += hld.getPeople().size();
            }
            assertEquals(populationSize, npeople);
        }
    }

    @Test
    public void openPlacesShouldBeStaffed() {
        int day = 1;
        DailyStats s = new DailyStats(day);
        for (int i = 0; i < 24; i++) {
            p.timeStep(day, i, s);

            int npeople = 0;
            for (CommunalPlace place : p.getPlaces().getAllPlaces()) {
                // i + 1 since the ith timestep has already been (so we are in the next state)
                if (place.isOpen(day, i + 1)) {
                    List<Person> staff = place.getStaff(day, i + 1);
                   assertTrue(staff.size() > 0);
                }
            }

        }
    }
}
