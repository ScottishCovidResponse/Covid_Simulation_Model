package uk.co.ramp.covid.simulation.population;

import org.junit.Test;
import uk.co.ramp.covid.simulation.place.Household;

import static org.junit.Assert.*;

public class PopulationTest {

    @Test
    public void populateHouseholds() {
        int populationSize = 200;
        Population p = new Population(populationSize,80);
        p.populateHouseholds();

        // Final population size = initial population size (all people allocated)
        int pop = 0;
        for (Household h : p.getPopulation()) {
           pop += h.getHouseholdSize();
        }
        assertEquals("Sum total household size should equal population size",  populationSize, pop);

        // Sanity check households
        for (Household h : p.getPopulation()){
            assertTrue("Each household must be assigned at least 1 person", h.getHouseholdSize() > 0);
            switch (h.getnType()) {
                // Adults only
                case 1: {
                    for (Object i : h.getInhabitants()) {
                        Person inhabitant = (Person) i;
                        assertTrue("Non Adult in adult only household", i instanceof Adult);
                    }
                    break;
                }
                // Pensioner only
                case 2: {
                    for (Object i : h.getInhabitants()) {
                        Person inhabitant = (Person) i;
                        assertTrue("Non Pensioner in pensioner only household", i instanceof Pensioner);
                    }
                    break;
                }
                // Adult + Pensioner (should contain at least one of each)
                case 3: {
                    boolean adultSeen = false;
                    boolean pensionerSeen = false;
                    for (Object i : h.getInhabitants()) {
                        Person inhabitant = (Person) i;
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
                case 4: {
                    boolean adultSeen = false;
                    boolean childInfantSeen = false;
                    for (Object i : h.getInhabitants()) {
                        Person inhabitanadultSeent = (Person) i;
                        adultSeen = adultSeen || i instanceof Adult;
                        childInfantSeen = childInfantSeen || i instanceof Child || i instanceof Infant;
                        assertTrue("Non Adult/Child/Infant in Adult/Child household",
                                i instanceof Child || i instanceof Infant || i instanceof Adult);

                    }
                    assertTrue("No adult in an adult/child household", adultSeen);
                    assertTrue("No child/infant in an adult/child household", childInfantSeen);
                    break;
                }
            }
        }
    }
}