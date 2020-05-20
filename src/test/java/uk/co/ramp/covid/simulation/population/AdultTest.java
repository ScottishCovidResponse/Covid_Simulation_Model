package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.util.RNG;
import java.io.IOException;
import static org.junit.Assert.assertTrue;

public class AdultTest {

    @Test
    public void testSetProfession() throws JsonParseException, IOException {
        //Test that a profession is set for an adult
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        RNG.seed(123);
        Adult adult = new Adult();
        adult.setProfession();
        boolean professionSet = false;
        professionSet = professionSet
                     || adult.profession == Adult.Professions.CONSTRUCTION
                     || adult.isShopWorker()
                     || adult.profession == Adult.Professions.HOSPITAL
                     || adult.profession == Adult.Professions.OFFICE
                     || adult.profession == Adult.Professions.RESTAURANT
                     || adult.profession == Adult.Professions.TEACHER
                     || adult.profession == Adult.Professions.NONE;

        assertTrue("Unexpected adult profession", professionSet);
    }
}