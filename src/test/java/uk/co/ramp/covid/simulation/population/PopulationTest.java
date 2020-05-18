package uk.co.ramp.covid.simulation.population;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class PopulationTest {

    Population p;
    int populationSize;

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        populationSize = 500;
        RNG.seed(123);
        p = new Population(populationSize,60);
        p.populateHouseholds();
    }

    @Test
    public void populateHouseholds() {

        // Final population size = initial population size (all people allocated)
        int pop = 0;
        for (Household h : p.getPopulation()) {
           pop += h.getHouseholdSize();
        }
        assertEquals("Sum total household size should equal population size",  populationSize, pop);

        // Sanity check households
        for (Household h : p.getPopulation()){
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

    @Test
    public void testSeedVirus() {
        int nInfections = 10;
        int nInfected = 0;
        p.seedVirus(nInfections);

        //Check that there are just 10 infections amongst the population
        for (int i = 0; i < p.nHousehold; i++) {
            for (int j = 0; j < p.population[i].getHouseholdSize(); j++) {
               if (p.population[i].getPerson(j).getInfectionStatus()) {
                   nInfected ++;
               }
            }
        }
        assertEquals("Unexpected number of infections", nInfections, nInfected);
    }

    @Test
    public void testAssignNeighbours() {
        p.populateHouseholds();
        p.createMixing();
        p.assignNeighbours();
        int totalNeighbours = 0;

        //loop for each household and check neighbour list is not null
        for (int i = 0; i < p.nHousehold; i++) {
            assertNotNull("Null neighbour list", p.population[i].nNeighbours());
            totalNeighbours += p.population[i].nNeighbours();
        }

        //Get the mean number of neighbours per household and compare against the expected
        double meanNeighbours = (double)totalNeighbours / (double)p.nHousehold;
        int expectedNeighbours = PopulationParameters.get().getExpectedNeighbours();
        assertEquals("Unexpected mean number of neighbours", meanNeighbours, expectedNeighbours, 0.5);
    }

    @Test
    public void testTimestep() {
        List<DailyStats> stats;
        int nDays = 3;

        p.populateHouseholds();
        p.createMixing();
        p.assignNeighbours();
        stats = p.timeStep(nDays);
        assertEquals("Unexpected number of daily stats", nDays, stats.size());
    }

    @Test
    public void testSetLockdown() {
        int start = 1;
        int end = 2;
        double socialDist = 2.0;
        p.setLockdown(start, end, socialDist);
        assertEquals("Unexpected lockdown start", start, p.lockdownStart);
        assertEquals("Unexpected lockdown end", end, p.lockdownEnd);
        assertEquals("Unexpected social distance", socialDist, p.socialDist, 0.01);
    }

    @Test
    public void testSetSchoolLockdown() {
        int start = 1;
        int end = 2;
        double socialDist = 2.0;
        p.setSchoolLockdown(start, end, socialDist);
        assertEquals("Unexpected school lockdown start", start, p.lockdownStart);
        assertEquals("Unexpected school lockdown end", end, p.lockdownEnd);
        assertTrue("Unexpected school lockdown", p.schoolL);
    }


}