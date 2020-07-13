package uk.co.ramp.covid.simulation.population;

import org.junit.Test;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import java.util.List;

import static org.junit.Assert.*;

public class TransportTest extends SimulationTest {

    @Test
    public void somePeopleAreInfectedOnPublicTransport() {
        Model m = new Model()
                .setPopulationSize(10000)
                .setnInitialInfections(100)
                .setExternalInfectionDays(0)
                .setIters(1)
                .setnDays(20)
                .setRNGSeed(42)
                .setNoOutput();

        List<List<DailyStats>> stats = m.run(0);
        List<DailyStats> istats = stats.get(0);
        
        int tinf = 0;
        for (DailyStats s : istats) {
            tinf += s.transportInfections.get();
        }
        assertTrue(tinf > 0);

    }

}