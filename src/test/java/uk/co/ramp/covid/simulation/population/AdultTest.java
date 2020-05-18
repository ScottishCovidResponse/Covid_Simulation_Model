package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class AdultTest {

    @Test
    public void testSetProfession() throws JsonParseException, IOException {
        //Test that a profession is set for an adult
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
//        new RunModel(123);
        RNG.seed(123);
        Adult adult = new Adult();
        adult.setProfession();
        boolean professionSet = false;
        professionSet = professionSet
                     || adult.isConstructionWorker()
                     || adult.isShopWorker()
                     || adult.isHospitalWorker()
                     || adult.isOfficeWorker()
                     || adult.isRestaurant()
                     || adult.isTeacher();

        assertTrue("Unexpected adult profession", professionSet);
    }
}