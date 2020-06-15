package uk.co.ramp.covid.simulation.output.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import uk.co.ramp.covid.simulation.util.Time;

/**
 * ContactsWriter writes contacts with one day timesteps.
 */
public class ContactsWriter extends SimpleContactsWriter {

    private Time loggedTime = new Time(0);
    private final Map<ContactPairWithLocation, Double> contactWeights = new HashMap<>();
    private static final int hoursPerTimestep = 24;
    
    public ContactsWriter(Appendable fileWriter) throws IOException {
        super(fileWriter);
    }

    @Override
    protected void addContact(Time t, ContactPairWithLocation c, double weight) {
        contactWeights.compute(c, (k, v) -> v == null ? weight : v + weight);
    }

    @Override
    public void finishTimeStep(Time t) {
        if (t.getAbsTime() == loggedTime.getAbsTime() + hoursPerTimestep - 1) {
            // Finished last hour of current output timestep.
            // Print out contacts and then clear the values.
            int outputTime = loggedTime.getAbsTime() / hoursPerTimestep;
            for (Map.Entry<ContactPairWithLocation, Double> entry : contactWeights.entrySet()) {
                writeContact(outputTime, entry.getKey(), entry.getValue());
            }
            contactWeights.clear();
            
            // set new logged time to the beginning of the next timestep
            loggedTime = new Time(t.getAbsTime() + 1);
        }
    }
}
