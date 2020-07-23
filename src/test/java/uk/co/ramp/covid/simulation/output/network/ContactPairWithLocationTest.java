package uk.co.ramp.covid.simulation.output.network;


import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Shop;
import uk.co.ramp.covid.simulation.population.Adult;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Time;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static uk.co.ramp.covid.simulation.population.Person.Sex.FEMALE;
import static uk.co.ramp.covid.simulation.population.Person.Sex.MALE;

public class ContactPairWithLocationTest extends SimulationTest {
    Adult p1;
    Adult p2;
    Shop s1;
    Adult p3;
    Adult p4;
    Shop s2;

    @Before
    public void setup() {
        p1 = new Adult(30, FEMALE);
        p2 = new Adult(40, MALE);
        s1 = new Shop(CommunalPlace.Size.SMALL);

        p3 = new Adult(30, FEMALE);
        p4 = new Adult(40, MALE);
        s2 = new Shop(CommunalPlace.Size.SMALL);
    }
    @Test
    public void testCompare() {

        ContactPairWithLocation cp1 = new ContactPairWithLocation(p1, p2, s1);
        ContactPairWithLocation cp2 = new ContactPairWithLocation(p3, p4, s2);

        ContactPairWithLocation cp3 = cp1;

        //These contact pairs are not the same
        assertEquals(-1, cp1.compareTo(cp2));

        //These contact pairs are the same
        assertEquals(0, cp3.compareTo(cp1));
    }

    //Test that a contact is added to the contacts file correctly
    @Test
    public void testAddContacts() throws IOException {
        String path = "src/test/resources/";
        File contactsFile = new File(path, "contacts.csv");
        SimpleContactsWriter contactsWriter = new SimpleContactsWriter(new FileWriter(contactsFile));

        ContactPairWithLocation cp1 = new ContactPairWithLocation(p1, p2, s1);
        contactsWriter.addContact(new Time(24), cp1, 1.0);
        contactsWriter.close();
        FileReader sr1 = new FileReader(contactsFile);
        try (BufferedReader br = new BufferedReader(sr1)) {
            String header = br.readLine();
            String contact = br.readLine();
            String expectedContact = "24," + p1.getID() + "," + p2.getID() + ",Shop,1.0";
            assertEquals("Unexpected value in contacts file", expectedContact, contact);

        } catch (IOException e) {
            e.printStackTrace();
        }

        contactsFile.delete();
    }
}