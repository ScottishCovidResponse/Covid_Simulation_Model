package uk.co.ramp.covid.simulation.output.network;


import org.junit.Test;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ContactsWriterTest extends SimulationTest {

    @Test
    public void testContactsWriter() throws FileNotFoundException {
        String path = "out/";
        String fileName1 = path + "contacts0.csv";
        String fileName2 = path + "people0.csv";
        //Delete output files if they already exist
        deleteOutputFiles(fileName1, fileName2);

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
        FileReader sr1 = new FileReader(fileName1);
        try (BufferedReader br = new BufferedReader(sr1)) {
            String line = br.readLine();
            String expectedHeader = "time,person1,person2,location,weight";
            assertEquals("Wrong header in contacts file", expectedHeader, line);

            String line2 = br.readLine();
            String[] values = line2.split(",");
            assertNotNull("No contact data found", values);

        } catch (IOException e) {
            e.printStackTrace();
        }

        FileReader sr2 = new FileReader(fileName2);
        try (BufferedReader br = new BufferedReader(sr2)) {
            String line = br.readLine();
            String expectedHeader = "id,age";
            assertEquals("Wrong header in people file", expectedHeader, line);

            String line2 = br.readLine();
            String[] values = line2.split(",");
            assertNotNull("No people data found", values);

        } catch (IOException e) {
            e.printStackTrace();
        }
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