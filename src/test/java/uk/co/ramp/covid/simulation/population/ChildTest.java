package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.place.School;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;
import static org.junit.Assert.assertTrue;

public class ChildTest {

    @Ignore("Failing Test")
    @Test
    public void testChildAtSchool() throws JsonParseException, IOException, ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        Population p = new Population(5000);
        Child child = new Child(10, Person.Sex.MALE);
        child.allocateCommunalPlace(p.getPlaces());
        assertTrue("Child not at school", child.getPrimaryCommunalPlace() instanceof School);
    }
}