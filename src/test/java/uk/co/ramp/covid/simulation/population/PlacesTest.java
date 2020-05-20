package uk.co.ramp.covid.simulation.population;

import com.sun.source.tree.AssertTree;
import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.place.Office;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class PlacesTest {

    @Before
    public void setupParams() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        // NOTE: This test is no guarenteed to pass with a different seed due to randomness
        RNG.seed(42);
    }

    @Test
    public void createNOffices() {
        int n = 100;
        
        Places p = new Places();
        p.createNOffices(n);

        int s = 0, m = 0, l = 0;
        for (Office o : p.getOffices()) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        // Test params = pSmall: 0.2, pMed: 0.3, pLarge:0.5
        assertEquals(n, s + m + l);
        assertEquals(20, s, 5);
        assertEquals(30, m, 5);
        assertEquals(50, l, 5);
    }

    @Test
    public void getRandomOffice() {
        int n = 100;
        int iters = 10000; // Number of samples to try
        Places p = new Places();
        p.createNOffices(n);

        // Should be most likely to get larger offices
        List<Office> samples = new ArrayList<>();
        for (int i = 0; i < iters ; i++) {
            samples.add(p.getRandomOffice());
        }

        int s = 0, m = 0, l = 0;
        for (Office o : samples) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        assertTrue("We should sample each office size at least once", s > 0 && m > 0 && l > 0);
        assertTrue("We should sample large offices more often than small", l > s);
        assertTrue("We should sample large offices more often than medium", l > m);
        assertTrue("We should sample medium offices more often than small", m > s);

        int DELTA = (int) iters*0.5; // Allow a 5% delta
        assertTrue("We sample large offices almost three times as often as medium", l - DELTA <= m*3 && l + DELTA >= m*3);
        assertTrue("We sample large offices almost six times as often as small", l - DELTA <= s*6 && l + DELTA >= s*6);
        assertTrue("We sample medium offices almost two times as often as small", m - DELTA <= s*2 && m + DELTA >= s*2);
    }
}
