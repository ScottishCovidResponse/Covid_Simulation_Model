package uk.co.ramp.covid.simulation.population;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.covid.CovidParameters;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class PopulationTest {

    private Population pop;
    private final int populationSize = 10000;
    private final int nHouseholds = 3000;

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        pop = PopulationGenerator.genValidPopulation(populationSize);
    }

    @Test
    public void testPopulationSize() {
        assertEquals("Unexpected population size", populationSize, pop.getPopulationSize());
    }

    @Test
    public void populateHouseholds() {

        // Final population size = initial population size (all people allocated)
        int p = 0;
        for (Household h : pop.getHouseholds()) {
           p += h.getHouseholdSize();
        }
        assertEquals("Sum total household size should equal population size",  populationSize, p);

        // Sanity check households
        for (Household h : pop.getHouseholds()){
            assertTrue("Each household must be assigned at least 1 person", h.getHouseholdSize() > 0);
            switch (h.gethType()) {
                // Adults only
                case ADULT: {
                    for (Object i : h.getInhabitants()) {
                        assertTrue("Non Adult in adult only household", i instanceof Adult);
                    }
                    break;
                }
                // Pensioner only
                case PENSIONER: {
                    for (Object i : h.getInhabitants()) {
                        assertTrue("Non Pensioner in pensioner only household", i instanceof Pensioner);
                    }
                    break;
                }
                // Adult + Pensioner (should contain at least one of each)
                case ADULTPENSIONER: {
                    boolean adultSeen = false;
                    boolean pensionerSeen = false;
                    for (Object i : h.getInhabitants()) {
                        adultSeen = adultSeen || i instanceof Adult;
                        pensionerSeen = adultSeen || i instanceof Pensioner;
                        assertTrue( "Non Pensioner/Adult in pensioner/adult household",
                                i instanceof Pensioner || i instanceof Adult);
                    }
                    assertTrue("No adult in an adult/pensioner household", adultSeen);
                    assertTrue("No pensioner in an adult/pensioner household", pensionerSeen);
                    break;
                }
                //Adult + Infant/Child ( Should contain at least one of each)
                case ADULTCHILD: {
                    boolean adultSeen = false;
                    boolean childInfantSeen = false;
                    for (Object i : h.getInhabitants()) {
                        adultSeen = adultSeen || i instanceof Adult;
                        childInfantSeen = childInfantSeen || i instanceof Child || i instanceof Infant;
                        assertTrue("Non Adult/Child/Infant in Adult/Child household",
                                i instanceof Child || i instanceof Infant || i instanceof Adult);

                    }
                    assertTrue("No adult in an adult/child household", adultSeen);
                    assertTrue("No child/infant in an adult/child household", childInfantSeen);
                    break;
                }
                //Pensioner + Infant/Child ( Should contain at least one of each)
                case PENSIONERCHILD: {
                    boolean pensionerSeen = false;
                    boolean childInfantSeen = false;
                    for (Object i : h.getInhabitants()) {
                        pensionerSeen = pensionerSeen || i instanceof Pensioner;
                        childInfantSeen = childInfantSeen || i instanceof Child || i instanceof Infant;
                        assertTrue("Non Pensioner/Child/Infact in Pensioner/Child household",
                                i instanceof Pensioner || i instanceof Child || i instanceof Infant);

                    }
                    assertTrue("No pensioner in an pensioner/child household", pensionerSeen);
                    assertTrue("No child/infant in an pensioner/child household", childInfantSeen);
                    break;
                }
                //Adult + Pensioner + Infant/Child ( Should contain at least one of each)
                case ADULTPENSIONERCHILD: {
                    boolean adultSeen = false;
                    boolean pensionerSeen = false;
                    boolean childInfantSeen = false;
                    for (Object i : h.getInhabitants()) {
                        adultSeen = adultSeen || i instanceof Adult;
                        pensionerSeen = pensionerSeen || i instanceof Pensioner;
                        childInfantSeen = childInfantSeen || i instanceof Child || i instanceof Infant;
                        assertTrue("Non Adult/Pensioner/Child/Infact in Pensioner/Child household",
                                i instanceof Adult || i instanceof Pensioner
                                        || i instanceof Child || i instanceof Infant);
                    }
                    assertTrue("No adult in an adult/pensioner/child household", adultSeen);
                    assertTrue("No pensioner in an adult/pensioner/child household", pensionerSeen);
                    assertTrue("No child/infant in an adult/pensioner/child household", childInfantSeen);
                    break;
                }
            }
        }
    }

    @Test (expected = ImpossibleAllocationException.class )
    public void testImpossibleAllocationException() throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        PopulationParameters.get().setHouseholdRatio(10.0);
        new Population(10);
    }

    @Test (expected = ImpossibleAllocationException.class )
    public void testBadHouseholdRatioExceptional() throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        PopulationParameters.get().setHouseholdRatio(50.0);
        new Population(10);
    }

    @Test
    public void testAllocateConstructionSite() {
        //Test that the primary place of adult construction workers is set to construction site
        Adult adult = new Adult(30, Person.Sex.FEMALE);
        adult.profession = Adult.Professions.CONSTRUCTION;
        pop.getHouseholds().get(0).addInhabitant(adult);
        adult.allocateCommunalPlace(pop.getPlaces());
        CommunalPlace cp = adult.getPrimaryCommunalPlace();
        assertTrue("Unexpected primary communal place", cp instanceof ConstructionSite);
    }

    @Test
    public void testAllocateHospital() {
        //Test that the primary place of adult hospital workers is set to hospital
        Adult adult = new Adult(30, Person.Sex.MALE);
        adult.profession = Adult.Professions.HOSPITAL;
        pop.getHouseholds().get(0).addInhabitant(adult);
        adult.allocateCommunalPlace(pop.getPlaces());
        CommunalPlace cp = adult.getPrimaryCommunalPlace();
        assertTrue("Unexpected primary communal place", cp instanceof Hospital);
    }

    @Test
    public void testAllocateOffice() {
        //Test that the primary place of adult office workers is set to office
        Adult adult = new Adult(30, Person.Sex.FEMALE);
        adult.profession = Adult.Professions.OFFICE;
        pop.getHouseholds().get(0).addInhabitant(adult);
        adult.allocateCommunalPlace(pop.getPlaces());
        CommunalPlace cp = adult.getPrimaryCommunalPlace();
        assertTrue("Unexpected primary communal place", cp instanceof Office);
    }

    @Test
    public void testAllocateRestaurant() {
        //Test that the primary place of adult restaurant workers is set to restaurant
        Adult adult = new Adult(30, Person.Sex.MALE);
        adult.profession = Adult.Professions.RESTAURANT;
        pop.getHouseholds().get(0).addInhabitant(adult);
        adult.allocateCommunalPlace(pop.getPlaces());
        CommunalPlace cp = adult.getPrimaryCommunalPlace();
        assertTrue("Unexpected primary communal place", cp instanceof Restaurant);
    }

    @Test
    public void testAllocateSchool() {
        //Test that the primary place of adult teachers is set to school
        Adult adult = new Adult(30, Person.Sex.FEMALE);
        adult.profession = Adult.Professions.TEACHER;
        pop.getHouseholds().get(0).addInhabitant(adult);
        adult.allocateCommunalPlace(pop.getPlaces());
        CommunalPlace cp = adult.getPrimaryCommunalPlace();
        assertTrue("Unexpected primary communal place", cp instanceof School);
    }

    @Test
    public void testAllocateShop() {
        //Test that the primary place of adult shop workers is set to shop
        Adult adult = new Adult(30, Person.Sex.MALE);
        adult.profession = Adult.Professions.SHOP;
        pop.getHouseholds().get(0).addInhabitant(adult);
        adult.allocateCommunalPlace(pop.getPlaces());
        CommunalPlace cp = adult.getPrimaryCommunalPlace();
        assertTrue("Unexpected primary communal place", cp instanceof Shop);
    }

    @Test
    public void testSeedVirus() {
        int nInfections = 10;
        int nInfected = 0;
        pop.seedVirus(nInfections);

        //Check that there are just 10 infections amongst the population
        for (Person p : pop.getAllPeople()) {
            if (p.getInfectionStatus()) {
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
            assertNotNull("Null neighbour list", pop.getHouseholds().get(i).nNeighbours());
            totalNeighbours += pop.getHouseholds().get(i).nNeighbours();
        }

        //Get the mean number of neighbours per household and compare against the expected
        double meanNeighbours = (double)totalNeighbours / (double) pop.getNumHouseholds();
        int expectedNeighbours = PopulationParameters.get().getExpectedNeighbours();
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
    public void testSetLockdown() {
        int start = 1;
        int end = 2;
        double socialDist = 2.0;
        pop.setLockdown(start, end, socialDist);
        assertEquals("Unexpected lockdown start", start, pop.getLockdownStart());
        assertEquals("Unexpected lockdown end", end, pop.getLockdownEnd());
        assertEquals("Unexpected social distance", socialDist, pop.getSocialDist(), 0.01);
    }

    @Test
    public void testLockdownOver() {
        List<DailyStats> stats;
        int nDays = 5;
        int startLockdown = 2;
        int endLockdown = 4;
        double socialDist = 2.0;
        pop.setLockdown(startLockdown, endLockdown, socialDist);
        stats = pop.simulate(nDays);
        assertFalse("Unexpectedly still in lockdown", pop.isLockdown());
    }

    @Test
    public void testInLockdown() {
        List<DailyStats> stats;
        int nDays = 5;
        int start = 3;
        int end = 6;
        double socialDist = 2.0;
        pop.setLockdown(start, end, socialDist);
        stats = pop.simulate(nDays);
        assertTrue("Unexpectedly not in lockdown", pop.isLockdown());
        assertTrue("Restaurants not in lockdown", pop.isrLockdown());
    }

    @Test
    public void testSetSchoolLockdown() {
        int start = 1;
        int end = 2;
        double socialDist = 2.0;
        pop.setSchoolLockdown(start, end, socialDist);
        assertEquals("Unexpected school lockdown start", start, pop.getLockdownStart());
        assertEquals("Unexpected school lockdown end", end, pop.getLockdownEnd());
        assertTrue("Unexpected school lockdown", pop.isSchoolL());
    }

    @Test
    public void testSchoolExemption() {
        List<DailyStats> stats;
        int nDays = 5;
        int startLockdown = 1;
        int endLockdown = 5;
        double socialDist = 2.0;
        pop.setLockdown(startLockdown, endLockdown, socialDist);
        pop.setSchoolLockdown(startLockdown, endLockdown - 2, socialDist);
        stats = pop.simulate(nDays);
        for (School s : pop.getPlaces().getSchools()) {
            assertTrue("School should be a key premises", s.isKeyPremises());
        }
        for (Nursery n : pop.getPlaces().getNurseries()) {
            assertTrue("Nursery should be a key premises", n.isKeyPremises());
        }
    }

    @Test
    public void allPlacesStaffed() throws ImpossibleAllocationException, IOException {
        for (CommunalPlace q : pop.getPlaces().getAllPlaces()) {
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
    public void secondaryInfectionsAreLogged() {
        pop.seedVirus(10);
        pop.simulate(20);
        int totalSecondary = 0;
        for (Person p : pop.getAllPeople()) {
            if (p.getcVirus() != null) {
                totalSecondary += p.getcVirus().getInfectionLog().getSecondaryInfections().size();
            }
        }
        assertTrue(totalSecondary > 0);
    }

    @Test
    public void symptomaticCasesAreLogged() {
        pop.seedVirus(1);

        Person infected = null;
        for (Person p : pop.getAllPeople()) {
            if (p.getcVirus() != null) {
                infected = p;
            }
        }
        
        infected.getcVirus().forceSymptomatic(true);
        
        pop.simulate(20);
        
        assertNotNull(infected.getcVirus().getInfectionLog().getSymptomaticTime());
    }
}
