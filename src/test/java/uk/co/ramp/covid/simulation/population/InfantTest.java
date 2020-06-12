package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static uk.co.ramp.covid.simulation.population.Person.Sex.FEMALE;

public class InfantTest extends SimulationTest {

    @Test
    public void testInfant() throws JsonParseException {
        int nNursery = 0;
        //Test approximately 12% of infants go to nursery
        for (int i = 0; i < 1000; i++) {
            Infant infant = new Infant(2, Person.Sex.MALE);
            if (infant.isGoesToNursery()) nNursery++;
        }
        assertEquals("Unexpected number of infants at nursery", 120, nNursery, 30);
    }

    @Test
    public void testInfantReports() {
        //Test Infant methods reportInfection() and reportDeath()
        Population p = PopulationGenerator.genValidPopulation(500);
        Infant infant = new Infant(3, FEMALE);

        List<DailyStats> stats;
        int nDays = 1;
        stats = p.simulate(nDays);

        infant.reportInfection(stats.get(0));
        assertEquals("Unexpected number of infant infections", 1, stats.get(0).getInfantInfected());

        infant.reportDeath(stats.get(0));
        assertEquals("Unexpected number of infant deaths", 1, stats.get(0).getInfantDeaths());
    }

    @Test (expected = InvalidAgeException.class)
    public void testInvalidAgeException() {
        new Infant(5, FEMALE);
    }
}