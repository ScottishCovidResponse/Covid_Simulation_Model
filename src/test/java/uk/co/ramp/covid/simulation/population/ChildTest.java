package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Test;
import org.junit.Assert;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.place.School;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;
import static org.junit.Assert.assertTrue;

public class ChildTest {

    @Test
    public void testChildAtSchool() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        RNG.seed(123);
        Population p = new Population(5000,600);
        try {
            p.populateHouseholds();
        } catch (ImpossibleAllocationException e) {
            Assert.fail("Could not populate households in test");
        }
        p.createMixing();
        Child child = new Child(10, Person.Sex.MALE);
        child.allocateCommunalPlace(p.getPlaces());
        assertTrue("Child not at school", child.getPrimaryCommunalPlace() instanceof School);
    }
}