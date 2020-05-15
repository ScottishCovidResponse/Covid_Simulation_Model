package uk.co.ramp.covid.simulation.population;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.place.Household;

import java.io.IOException;

import static org.junit.Assert.*;

public class PopulationTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void populateHouseholds() {
        int populationSize = 500;
        Population p = new Population(populationSize,60);

        try {
            p.populateHouseholds();
        } catch (ImpossibleAllocationException e) {
            fail("Could not allocate households in test");
        }

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
}