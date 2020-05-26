package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Assert;
import org.junit.Test;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.util.RNG;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class InfantTest {

    @Test
    public void testInfant() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        RNG.seed(100);
        int nNursery = 0;
        PopulationParameters.get().setAttendsNursery(1.0);
        //Test 0% of infants go to nursery
        for (int i = 0; i < 100; i++) {
            Infant infant = new Infant();
            if (infant.isGoesToNursery()) nNursery++;
        }
        assertEquals("Unexpected number of infants at nursery", 100, nNursery);
    }

    @Test
    public void testInfantReports() throws IOException {
        //Test Infant methods reportInfection() and reportDeath()
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        RNG.seed(123);
        Population p = new Population(500,60);
        try {
            p.populateHouseholds();
        } catch (ImpossibleAllocationException e) {
            Assert.fail("Could not populate households in test");
        }
        p.createMixing();
        p.assignNeighbours();
        Infant infant = new Infant();

        List<DailyStats> stats;
        int nDays = 1;
        stats = p.timeStep(nDays);

        infant.reportInfection(stats.get(0));
        assertEquals("Unexpected number of infant infections", 1, stats.get(0).getInfantInfected());

        infant.reportDeath(stats.get(0));
        assertEquals("Unexpected number of infant deaths", 1, stats.get(0).getInfantDeaths());
    }
}