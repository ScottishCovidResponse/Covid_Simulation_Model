package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.place.School;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.util.SimulationTest;

import java.io.IOException;
import static org.junit.Assert.assertTrue;

public class ChildTest extends SimulationTest {

    @Test
    public void testChildAtSchool() throws JsonParseException, IOException, ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        Population p = PopulationGenerator.genValidPopulation(5000);
        Child child = new Child(10, Person.Sex.MALE);
        child.allocateCommunalPlace(p.getPlaces());
        assertTrue("Child not at school", child.getPrimaryCommunalPlace() instanceof School);
    }
}
