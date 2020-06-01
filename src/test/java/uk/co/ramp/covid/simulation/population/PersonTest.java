package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import com.sun.org.apache.bcel.internal.generic.FADD;
import org.junit.Before;
import org.junit.Test;
import sun.security.pkcs11.wrapper.CK_SSL3_MASTER_KEY_DERIVE_PARAMS;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.util.RNG;
import java.io.IOException;
import static org.junit.Assert.*;

public class PersonTest {

    @Before
    public void initialise() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
    }

    @Test
    public void testInfect() {
        //Test that the infect() method returns true
        Person person = new Adult(30, Person.Sex.FEMALE);
        assertTrue("Person unexpectedly not infected", person.infect());
    }

    @Test
    public void testInfectionStatus() {
        //Test that an infected person's infection status is true
        Person person = new Adult(30, Person.Sex.MALE);
        boolean inf = person.infect();
        assertTrue("Person unexpectedly not infected", person.getInfectionStatus());
    }

    @Test
    public void testStepInfection() {
        //Test that stepInfection returns a valid status
        Person person = new Adult(30, Person.Sex.FEMALE);
        person.infChallenge(100.0);
        assertNotNull("Invalid CStatus", person.stepInfection(new Time()));
    }

    @Test
    public void testCStatus() {
        //Test the status of a new person is healthy
        Person person = new Adult(30, Person.Sex.MALE);
        assertSame("Person not healthy", CStatus.HEALTHY, person.cStatus());

        //Test the status of an infected person is not healthy
        person.infChallenge(100.0);
        person.stepInfection(new Time());
        assertNotSame("Person unexpectedly healthy", CStatus.HEALTHY, person.cStatus());

    }


}