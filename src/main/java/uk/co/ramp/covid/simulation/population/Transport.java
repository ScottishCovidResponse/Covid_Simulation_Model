package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.output.network.ContactsWriter;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.Place;
import uk.co.ramp.covid.simulation.util.Time;

/** Transport is a special form of Place where we allow infections to occur, but not to advance,
 *  and no movements occur */
public class Transport extends Place {
    
    final int populationSize;

    Transport(int populationSize) {
        this.populationSize = populationSize;
        expectedInteractionsPerHour = PopulationParameters.get().publicTransportParameters.expectedInteractions;
    }

    public void addPassenger(Person p) {
        people.add(p);
    }

    // No-one "leaves" public transport in the usual manner since they are already going elsewhere
    @Override
    public void determineMovement(Time t, DailyStats s, boolean lockdown, Places places) { }

    @Override
    protected double getTransConstant() {
        if(people.size() == 0) {
            return 0.0;
        }

        return transConstant * expectedInteractionsPerHour / populationSize;
    }

    // We only do infections here, not step infections since 1 hour won't have passed.
    @Override
    public void doInfect(Time t, DailyStats stats, ContactsWriter contactsWriter) {
        if (contactsWriter != null) {
            addContacts(t, contactsWriter);
            return; // don't do infections
        }

        infectOthers(t, stats);
        people.clear();
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        s.incTransportInfections();
    }
}
