package uk.co.ramp.covid.simulation.output.network;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import uk.co.ramp.covid.simulation.population.Person;

public class PeopleWriter {
   
    public static void writePeople(Appendable fileWriter, ArrayList<Person> allPeople) throws IOException {
        String[] headers = {"id", "age"};
        CSVPrinter csv = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader(headers));

        for (Person p : allPeople) {
            csv.printRecord(p.getID(), p.getAge());
        }
        csv.close();
    }

}
