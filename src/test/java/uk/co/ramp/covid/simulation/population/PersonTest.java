package uk.co.ramp.covid.simulation.population;

import org.junit.Test;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import static org.junit.Assert.*;

public class PersonTest extends SimulationTest {

    @Test
    public void testInfect() {
        //Test that the infect() method returns true
        Person person = new Adult(30, Person.Sex.FEMALE);
        assertTrue("Person unexpectedly not infected", person.forceInfect());
    }

    @Test
    public void testInfectionStatus() {
        //Test that an infected person's infection status is true
        Person person = new Adult(30, Person.Sex.MALE);
        boolean inf = person.forceInfect();
        assertTrue("Unexpected value returned by infect()", inf);
        assertTrue("Person unexpectedly not infected", person.isInfected());
    }

    @Test
    public void testStepInfection() {
        //Test that stepInfection returns a valid status
        Person person = new Adult(30, Person.Sex.FEMALE);
        person.infChallenge(1.0);
        person.stepInfection(new Time());
        assertNotNull("Invalid CStatus", person.cStatus());
    }

    @Test
    public void testCStatus() {
        //Test the status of a new person is healthy
        Person person = new Adult(30, Person.Sex.MALE);
        assertSame("Person not healthy", CStatus.HEALTHY, person.cStatus());

        //Test the status of an infected person is not healthy
        person.infChallenge(1.0);
        person.stepInfection(new Time());
        assertNotSame("Person unexpectedly healthy", CStatus.HEALTHY, person.cStatus());

    }

    @Test
    public void testSetMortality() {
        double m10 = new Child(10, Person.Sex.MALE).setMortality();
        double m50 = new Adult(50, Person.Sex.MALE).setMortality();
        double m51 = new Adult(51, Person.Sex.MALE).setMortality();
        double m80 = new Pensioner(80, Person.Sex.MALE).setMortality();
        double m100 = new Pensioner(100, Person.Sex.MALE).setMortality();
        
        double delta = 0.00001;

        assertEquals(m10, 0.0500, delta);
        assertEquals(m50, 0.0500, delta);
        assertEquals(m51, 0.0504, delta);
        assertEquals(m80, 0.41, delta);
        assertEquals(m100, 1.05, delta);
    }

}
