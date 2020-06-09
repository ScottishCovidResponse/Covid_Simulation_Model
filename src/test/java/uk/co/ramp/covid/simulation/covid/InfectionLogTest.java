package uk.co.ramp.covid.simulation.covid;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InfectionLogTest extends SimulationTest {

    private Population pop;
    private final int populationSize = 10000;

    @Before
    public void setupParams() {
        pop = PopulationGenerator.genValidPopulation(populationSize);
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
        assertTrue("No secondary infections logged", totalSecondary > 0);
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

        pop.simulate(50);

        assertNotNull("No symptomatic cases logged", infected.getcVirus().getInfectionLog().getSymptomaticTime());
    }

}