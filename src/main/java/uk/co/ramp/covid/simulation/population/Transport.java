package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.output.DailyStats;

import java.util.ArrayList;
import java.util.List;

public class Transport {
    List<Person> takingTransport;

    protected double sDistance;
    protected double transConstant;
    protected double transAdjustment;

    Transport() {
        takingTransport = new ArrayList<>();
        sDistance = 1.0;
        // TODO: Set from parameers
        transConstant = 5.0;
        transAdjustment = 1.0;
    }
    
    public void addPassenger(Person p) {
        takingTransport.add(p);
    }

    // TODO: Lots of this is repeated in Place, can we factor this out?
    public double getBaseTransP(Person infected) {
        return getTransConstant() * sDistance * infected.getTransAdjustment();
    }

    protected double getTransConstant() {
        if(takingTransport.size() == 0) {
            return 0.0;
        }

        if(takingTransport.size() <= transAdjustment) {
            return transConstant;
        }

        return transConstant * transAdjustment / takingTransport.size();
    }

    // We only do infections here, not step infections since 1 hour won't have passed.
    public void doInfect(Time t, DailyStats stats) {
        for (Person cPers : takingTransport) {
            if (cPers.isInfectious()) {
                for (Person nPers : takingTransport) {
                    if (cPers != nPers && !nPers.getInfectionStatus()) {
                        double transP = getBaseTransP(cPers);
                        boolean infected = nPers.infChallenge(transP);
                        if (infected) {
                            registerInfection(t, nPers, stats);
                            nPers.getcVirus().getInfectionLog().registerInfected(t);
                            cPers.getcVirus().getInfectionLog().registerSecondaryInfection(t, nPers);
                        }
                    }
                }
            }
        }
        
        takingTransport.clear();
    }

    private void registerInfection(Time t, Person p, DailyStats s) {
        s.incTransportInfections();
        p.reportInfection(s);
    }
}
