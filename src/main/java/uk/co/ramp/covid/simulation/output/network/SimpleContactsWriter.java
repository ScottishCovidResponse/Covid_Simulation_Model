package uk.co.ramp.covid.simulation.output.network;

import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.place.Place;
import uk.co.ramp.covid.simulation.population.Person;

/**
 * SimpleContactsWriter writes contacts with one hour timesteps.
 * It is also used as the base class for ContactsWriter, which writes larger
 * timesteps (days).
 */
public class SimpleContactsWriter {

    private final CSVPrinter csvPrinter;
    
    public SimpleContactsWriter(Appendable fileWriter) throws IOException {
        String[] headers = { "time", "person1", "person2", "location", "weight" };
        csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader(headers));
    }

    protected void writeContact(int timestep, ContactPairWithLocation c, double weight) {
        try {
            csvPrinter.printRecord(timestep, c.getPerson1(), c.getPerson2(), c.getLocation(), weight);
        } catch (IOException e) {
            throw new ContactsWriterException("Error writing contacts", e);
        }
    }

    public void close() {
        try {
            csvPrinter.close(true);
        } catch (IOException e) {
            throw new ContactsWriterException("Error closing contacts printer", e);
        }
    }

    public void addContact(Time t, Person a, Person b, Place place, double weight) {
        addContact(t, new ContactPairWithLocation(a, b, place), weight);
    }

    // addContact() and finishTimeStep() are overridden in ContactsWriter.
    protected void addContact(Time t, ContactPairWithLocation c, double weight) {
        writeContact(t.getAbsTime(), c, weight);
    }

    public void finishTimeStep(Time t) {}

}
