package uk.co.ramp.covid.simulation.population;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.place.householdtypes.*;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.List;

import static org.junit.Assert.*;

public class PopulationTest extends SimulationTest {

    private Population pop;
    private final int populationSize = 10000;

    @Before
    public void setupParams() {
        pop = PopulationGenerator.genValidPopulation(populationSize);
    }

    @Test
    public void testPopulationSize() {
        assertEquals("Unexpected population size", populationSize, pop.getPopulationSize());
    }


    @Test
    public void populateHouseholds() {
        // We test the initial household distribution is okay before pensioner secondment
        PopulationParameters.get().pensionerProperties.pEntersCareHome = new Probability(0.0);
        
        pop = PopulationGenerator.genValidPopulation(populationSize);
        int p = 0;
        for (Household h : pop.getHouseholds()) {
           p += h.getHouseholdSize();
        }
        assertEquals("Sum total household size should equal population size",  populationSize, p);


        for (Household h : pop.getHouseholds()) {
            assertTrue("Each household must be assigned at least 1 person", h.getHouseholdSize() > 0);
            
            if (h instanceof SingleAdult) {
                assertEquals(1, h.getHouseholdSize());
                h.getInhabitants().forEach(per -> assertTrue(per instanceof Adult));
            }

            if (h instanceof SmallAdult) {
                assertEquals(2, h.getHouseholdSize());
                h.getInhabitants().forEach(per -> assertTrue(per instanceof Adult));
            }

            if (h instanceof SingleParent) {
                assertEquals(1, h.getInhabitants().stream()
                        .filter(per -> per instanceof Adult || per instanceof Pensioner).count());
                assertTrue(h.getInhabitants().stream()
                        .filter(per -> per instanceof Child || per instanceof Infant).count() >= 1);
            }

            if (h instanceof SmallFamily) {
                assertEquals(2, h.getInhabitants().stream()
                        .filter(per -> per instanceof Adult || per instanceof Pensioner).count());
                long numChildren = h.getInhabitants().stream()
                        .filter(per -> per instanceof Child || per instanceof Infant).count();
                assertTrue(numChildren == 1 || numChildren == 2 );
            }

            if (h instanceof LargeTwoAdultFamily) {
                long numAdults = h.getInhabitants().stream()
                        .filter(per -> per instanceof Adult || per instanceof Pensioner).count();
                long numChildren = h.getInhabitants().stream()
                        .filter(per -> per instanceof Child || per instanceof Infant).count();

                assertEquals(2, numAdults);
                assertTrue(numChildren >= 3);
            }

            if (h instanceof LargeManyAdultFamily) {
                long numAdults = h.getInhabitants().stream()
                        .filter(per -> per instanceof Adult || per instanceof Pensioner).count();
                long numChildren = h.getInhabitants().stream()
                        .filter(per -> per instanceof Child || per instanceof Infant).count();

                assertTrue(numAdults >= 3);
                assertTrue(numChildren >= 1);
            }

            if (h instanceof LargeAdult) {
                long numAdults = h.getInhabitants().stream()
                        .filter(per -> per instanceof Adult || per instanceof Pensioner).count();
                assertTrue(numAdults >= 3);
            }

            if (h instanceof AdultPensioner) {
                long numAdults = h.getInhabitants().stream()
                        .filter(per -> per instanceof Adult).count();
                long numPensioners = h.getInhabitants().stream()
                        .filter(per -> per instanceof Pensioner).count();
                long numChildren = h.getInhabitants().stream()
                        .filter(per -> per instanceof Child || per instanceof Infant).count();
                assertTrue(numAdults == 1 && numPensioners == 1);
                assertEquals(0, numChildren);
            }

            if (h instanceof DoubleOlder) {
                assertEquals(2, h.getHouseholdSize());
                h.getInhabitants().forEach(per -> assertTrue(per instanceof Pensioner));
            }

            if (h instanceof SingleOlder) {
                assertEquals(1, h.getHouseholdSize());
                h.getInhabitants().forEach(per -> assertTrue(per instanceof Pensioner));
            }
        }
    }

    @Test (expected = ImpossibleAllocationException.class )
    public void testBadHouseholdRatioExceptional() throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        PopulationParameters.get().householdDistribution.populationToHouseholdsRatio = 50.0;
        new Population(10);
    }

    @Test (expected = ImpossibleWorkerDistributionException.class)
    public void testImpossibleWorkerDistribution() throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        PopulationParameters.get().buildingDistribution.populationToConstructionSitesRatio = 10;
        Population p = new Population(populationSize);
        p.allocatePeople();
    }

    @Test
    public void testAllocateConstructionSite() {
        //Test that the primary place of adult construction workers is set to construction site
        Adult adult = new Adult(30, Person.Sex.FEMALE);
        adult.profession = Adult.Professions.CONSTRUCTION;
        adult.allocateCommunalPlace(pop.getPlaces());
        CommunalPlace cp = adult.getPrimaryCommunalPlace();
        assertTrue("Unexpected primary communal place", cp instanceof ConstructionSite);
    }

    @Test
    public void testAllocateHospital() {
        //Test that the primary place of adult hospital workers is set to hospital
        Adult adult = new Adult(30, Person.Sex.MALE);
        adult.profession = Adult.Professions.HOSPITAL;
        adult.allocateCommunalPlace(pop.getPlaces());
        CommunalPlace cp = adult.getPrimaryCommunalPlace();
        assertTrue("Unexpected primary communal place", cp instanceof Hospital);
    }

    @Test
    public void testAllocateOffice() {
        //Test that the primary place of adult office workers is set to office
        Adult adult = new Adult(30, Person.Sex.FEMALE);
        adult.profession = Adult.Professions.OFFICE;
        adult.allocateCommunalPlace(pop.getPlaces());
        CommunalPlace cp = adult.getPrimaryCommunalPlace();
        assertTrue("Unexpected primary communal place", cp instanceof Office);
    }

    @Test
    public void testAllocateRestaurant() {
        //Test that the primary place of adult restaurant workers is set to restaurant
        Adult adult = new Adult(30, Person.Sex.MALE);
        adult.profession = Adult.Professions.RESTAURANT;
        adult.allocateCommunalPlace(pop.getPlaces());
        CommunalPlace cp = adult.getPrimaryCommunalPlace();
        assertTrue("Unexpected primary communal place", cp instanceof Restaurant);
    }

    @Test
    public void testAllocateSchool() {
        //Test that the primary place of adult teachers is set to school
        Adult adult = new Adult(30, Person.Sex.FEMALE);
        adult.profession = Adult.Professions.TEACHER;
        adult.allocateCommunalPlace(pop.getPlaces());
        CommunalPlace cp = adult.getPrimaryCommunalPlace();
        assertTrue("Unexpected primary communal place", cp instanceof School);
    }

    @Test
    public void testAllocateShop() {
        //Test that the primary place of adult shop workers is set to shop
        Adult adult = new Adult(30, Person.Sex.MALE);
        adult.profession = Adult.Professions.SHOP;
        adult.allocateCommunalPlace(pop.getPlaces());
        CommunalPlace cp = adult.getPrimaryCommunalPlace();
        assertTrue("Unexpected primary communal place", cp instanceof Shop);
    }

    @Test
    public void testSeedVirus() {
        int nInfections = 10;
        int nInfected = 0;
        pop.getSeeder().forceNInfections(nInfections);

        //Check that there are just 10 infections amongst the population
        for (Person p : pop.getAllPeople()) {
            if (p.isInfected()) {
                nInfected++;
            }
        }

        assertEquals("Unexpected number of infections", nInfections, nInfected);
    }

    @Test
    public void testAssignNeighbours() {
        int totalNeighbours = 0;

        //loop for each household and check neighbour list is not null
        for (int i = 0; i < pop.getNumHouseholds(); i++) {
            assertTrue("Null neighbour list", pop.getHouseholds().get(i).nNeighbours() >= 0);
            totalNeighbours += pop.getHouseholds().get(i).nNeighbours();
        }

        //Get the mean number of neighbours per household and compare against the expected
        double meanNeighbours = (double)totalNeighbours / (double) pop.getNumHouseholds();
        int expectedNeighbours = PopulationParameters.get().householdProperties.expectedNeighbours;
        assertEquals("Unexpected mean number of neighbours", meanNeighbours, expectedNeighbours, 0.5);
    }

    @Test
    public void testTimestep() {
        List<DailyStats> stats;
        int nDays = 3;
        stats = pop.simulate(nDays);
        assertEquals("Unexpected number of daily stats", nDays, stats.size());
    }



    @Test
    public void allPlacesStaffed() {
        for (CommunalPlace q : pop.getPlaces().getCommunalPlaces()) {
            assertTrue("Place not fully staffed, nStaff: " + q.getnStaff(), q.isFullyStaffed());
        }
    }
    
    @Test
    public void populationIsValid() {
        // Ages from 0-100
        for (Person p : pop.getAllPeople()) {
            assertTrue(p.getAge() >= 0 && p.getAge() <= 100);
        }
        
        // Roughly (with 5% tolerance) a 50-50 m/f split
        int m = 0;
        int f = 0;
        for (Person p : pop.getAllPeople()) {
            if (p.getSex() == Person.Sex.MALE) {
                m++;
            } else {
                f++;
            }
        }

        assertEquals(0, Math.abs(m-f), populationSize * 0.10);
        
        // Proportions of people type make sense
        int adult = 0;
        int child = 0;
        int infant = 0;
        int pensioner = 0;
        
        for (Person p : pop.getAllPeople()) {
            if (p instanceof Infant) {
                infant++;
            }
            else if (p instanceof Adult) {
                adult++;
            }
            else if (p instanceof Child) {
                child++;
            }
            else if (p instanceof Pensioner) {
                pensioner++;
            }
        }

        // Most people are adults
        assertTrue(adult > child);
        assertTrue(adult > infant);
        assertTrue(adult > pensioner);

        // There's a lot of pensioners too
        assertTrue(pensioner > child);
        assertTrue(pensioner > infant);

        assertTrue(child > infant);
    }

    @Test
    public void neighbourDistributionsAreBasedOnHouseholdTypes() {
        int sameGroup = 0;
        int otherGroups = 0;
        
        for (Household h : pop.getHouseholds()) {
            for (Household o : h.getNeighbours()) {
                if (h.getNeighbourGroup() == o.getNeighbourGroup()) {
                    sameGroup++;
                } else {
                    otherGroups++;
                }
            }
        }
        
        assertTrue("Neighbours of the same group should be more than other groups: " + sameGroup + ">" + otherGroups,
                sameGroup > otherGroups);
    }
}
