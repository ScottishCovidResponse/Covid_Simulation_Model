package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.apache.logging.log4j.core.appender.db.jdbc.FactoryMethodConnectionSource;
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
        Adult adult = new Adult(30, Person.Sex.FEMALE);
        adult.setProfession();
        boolean professionSet = false;
        professionSet = professionSet
                     || adult.profession == Adult.Professions.CONSTRUCTION
                     || adult.profession == Adult.Professions.SHOP
                     || adult.profession == Adult.Professions.HOSPITAL
                     || adult.profession == Adult.Professions.OFFICE
                     || adult.profession == Adult.Professions.RESTAURANT
                     || adult.profession == Adult.Professions.TEACHER
                     || adult.profession == Adult.Professions.NONE;

        assertTrue("Unexpected adult profession", professionSet);
    }
}
