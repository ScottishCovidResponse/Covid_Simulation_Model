package uk.co.ramp.covid.simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import uk.co.ramp.covid.simulation.place.Place;
import uk.co.ramp.covid.simulation.population.Person;

public class NetworkGenerator {
    private static CSVPrinter contactsPrinter = null;
    
    private static void writePeople(ArrayList<Person> allPeople, String outputDir) throws IOException {
        String[] headers = {"id", "age"};
        File outputFile = new File(outputDir, "people.csv");

        CSVPrinter csv = new CSVPrinter(new FileWriter(outputFile), CSVFormat.DEFAULT.withHeader(headers));

        for (Person p : allPeople) {
            csv.printRecord(p.getID(), p.getAge());
        }
        csv.close();
    }
    
    private static void openContactsPrinter(String outputDir) throws IOException {
        String[] headers = {""};
        File outputFile = new File(outputDir, "contacts.csv");
        contactsPrinter = new CSVPrinter(new FileWriter(outputFile), CSVFormat.DEFAULT.withHeader(headers));
    }

    public static void startNetworkGeneration(ArrayList<Person> allPeople, String outputDir) throws IOException {
        writePeople(allPeople, outputDir);
        openContactsPrinter(outputDir);
    }
    
    public static boolean generating() {
        return contactsPrinter != null;
    }
    
    public static void writeContact(Time t, Person a, Person b, Place place, double weight) {
        try {
            contactsPrinter.printRecord(t.getAbsTime(), a.getID(), b.getID(), place.getClass().getSimpleName(), weight);
        } catch (IOException e) {
            throw new NetworkGeneratorException("Error writing contacts", e);
        }
    }
}
