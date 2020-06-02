package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.util.RNG;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class InfantTest {

    @Test
    public void testInfant() throws JsonParseException, IOException {
        RNG.seed(0); // This test is sensitive to random numbers
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        int nNursery = 0;
        //Test 50% of infants go to nursery
        for (int i = 0; i < 1000; i++) {
            Infant infant = new Infant(2, Person.Sex.MALE);
            if (infant.isGoesToNursery()) nNursery++;
        }
        assertEquals("Unexpected number of infants at nursery", 500, nNursery, 10);
    }

    @Test
    public void testInfantReports() throws IOException, ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        //Test Infant methods reportInfection() and reportDeath()
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        Population p = PopulationGenerator.genValidPopulation(500);
        Infant infant = new Infant(3, Person.Sex.FEMALE);

        List<DailyStats> stats;
        int nDays = 1;
        stats = p.simulate(nDays);

        infant.reportInfection(stats.get(0));
        assertEquals("Unexpected number of infant infections", 1, stats.get(0).getInfantInfected());

        infant.reportDeath(stats.get(0));
        assertEquals("Unexpected number of infant deaths", 1, stats.get(0).getInfantDeaths());
    }
}