package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.util.SimulationTest;

import java.io.IOException;
import static org.junit.Assert.assertTrue;

public class AdultTest extends SimulationTest {

    @Test
    public void testSetProfession() throws JsonParseException, IOException {
        //Test that a profession is set for an adult
        Adult adult = new Adult(30, Person.Sex.FEMALE);
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
}
