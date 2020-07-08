package uk.co.ramp.covid.simulation.covid;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InfectionLogTest extends SimulationTest {

    private Population pop;

    @Before
    public void setupParams() {
        int populationSize = 10000;
        pop = PopulationGenerator.genValidPopulation(populationSize);
    }

    @Test
    public void secondaryInfectionsAreLogged() {
        pop.getSeeder().forceNInfections(10);
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
        pop.getSeeder().forceNInfections(1);

        Person infected = null;
        for (Person p : pop.getAllPeople()) {
            if (p.getcVirus() != null) {
                infected = p;
            }
        }

        assertNotNull("No infected cases found", infected);
        infected.getcVirus().forceSymptomatic(true);

        pop.simulate(50);

        assertNotNull("No symptomatic cases logged", infected.getcVirus().getInfectionLog().getSymptomaticTime());
    }

}