package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import java.io.IOException;
import static org.junit.Assert.*;

public class PersonTest {

    @Before
    public void initialise() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("parameters/example_params.json");
        new RunModel(123);
    }

    @Test
    public void testInfect() {
        //Test that the infect() method returns true
        Person person = new Person();
        assertTrue("Person unexpectedly not infected", person.infect());
    }

    @Test
    public void testInfectionStatus() {
        //Test that an infected person's infection status is true
        Person person = new Person();
        boolean inf = person.infect();
        assertTrue("Person unexpectedly not infected", person.getInfectionStatus());
    }

    @Test
    public void testStepInfection() {
        //Test that stepInfection returns a valid status
        Person person = new Person();
        person.infChallenge(100.0);
        assertNotNull("Invalid CStatus", person.stepInfection());
    }

    @Test
    public void testCStatus() {
        //Test the status of a new person is healthy
        Person person = new Person();
        assertSame("Person not healthy", CStatus.HEALTHY, person.cStatus());

        //Test the status of an infected person is not healthy
        person.infChallenge(100.0);
        person.stepInfection();
        assertNotSame("Person unexpectedly healthy", CStatus.HEALTHY, person.cStatus());

    }


}