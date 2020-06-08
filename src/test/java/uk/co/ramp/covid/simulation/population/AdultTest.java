package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.util.SimulationTest;

import static org.junit.Assert.assertTrue;
import static uk.co.ramp.covid.simulation.population.Person.Sex.FEMALE;

public class AdultTest extends SimulationTest {

    @Test
    public void testSetProfession() throws JsonParseException {
        //Test that a profession is set for an adult
        Adult adult = new Adult(30, FEMALE);
        adult.setProfession();
        boolean professionSet = false;
        professionSet = professionSet
                     || adult.profession == Adult.Professions.CONSTRUCTION
                     || adult.profession == Adult.Professions.SHOP
                     || adult.profession == Adult.Professions.HOSPITAL
                     || adult.profession == Adult.Professions.OFFICE
                     || adult.profession == Adult.Professions.RESTAURANT
                     || adult.profession == Adult.Professions.NURSERY
                     || adult.profession == Adult.Professions.TEACHER
                     || adult.profession == Adult.Professions.NONE;

        assertTrue("Unexpected adult profession", professionSet);
    }

    @Test (expected = InvalidAgeException.class)
    public void testInvalidAgeException() {
        new Adult(66, FEMALE);
    }
}
