package uk.co.ramp.covid.simulation.output.network;


import org.junit.Test;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Shop;
import uk.co.ramp.covid.simulation.population.Adult;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Time;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.co.ramp.covid.simulation.population.Person.Sex.FEMALE;
import static uk.co.ramp.covid.simulation.population.Person.Sex.MALE;

public class ContactsWriterTest extends SimulationTest {

    @Test
    public void testContactsWriter() throws FileNotFoundException {

        String path = "src/test/resources/";
        String contactsFile = path + "contacts0.csv";
        String peopleFile = path + "people0.csv";
        //Delete output files if they already exist
        deleteOutputFiles(contactsFile, peopleFile);

        Model m = new Model()
                .setPopulationSize(10000)
                .setnInitialInfections(200)
                .setExternalInfectionDays(0)
                .setIters(1)
                .setnDays(2)
                .setRNGSeed(0)
                .setNetworkOutputDir(path)
                .setOutputDirectory(path);

        int startIterID = 2;
        List<List<DailyStats>> stats = m.run(startIterID);

        //Check that the header is correct and data exists in the csv files
        FileReader sr1 = new FileReader(contactsFile);
        try (BufferedReader br = new BufferedReader(sr1)) {
            String line = br.readLine();
            String expectedHeader = "time,person1,person2,location,weight";
            assertEquals("Wrong header in contacts file", expectedHeader, line);

            String line2 = br.readLine();
            String[] values = line2.split(",");
            assertEquals("No contact data found", 5, values.length);

        } catch (IOException e) {
            e.printStackTrace();
        }

        FileReader sr2 = new FileReader(peopleFile);
        try (BufferedReader br = new BufferedReader(sr2)) {
            String line = br.readLine();
            String expectedHeader = "id,age";
            assertEquals("Wrong header in people file", expectedHeader, line);

            String line2 = br.readLine();
            String[] values = line2.split(",");
            assertEquals("No people data found", 2, values.length);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //Delete output files
        deleteOutputFiles(contactsFile, peopleFile);
    }

    @Test (expected = ContactsWriterException.class)
    public void testException() throws IOException {
        String path = "src/test/resources/";
        String contactsFile = path + "contacts0.csv";
        SimpleContactsWriter contactsWriter = new SimpleContactsWriter(new FileWriter(contactsFile));

        Adult p1 = new Adult(30, FEMALE);
        Adult p2 = new Adult(40, MALE);
        Shop s1 = new Shop(CommunalPlace.Size.SMALL);
        ContactPairWithLocation cp1 = new ContactPairWithLocation(p1, p2, s1);

        //Close the contacts writer to cause an exception
        contactsWriter.close();
        contactsWriter.addContact(new Time(24), cp1, 1.0);

    }

    private void deleteOutputFiles(String fileName1, String fileName2) {
        File file1 = new File(fileName1);
        File file2 = new File(fileName2);
        if (file1.exists()) {
            file1.delete();
        }

        if (file2.exists()) {
            file2.delete();
        }
    }
}