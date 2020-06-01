package uk.co.ramp.covid.simulation.covid;

import uk.co.ramp.covid.simulation.Time;

/** InfectionLog matains information about a particular COVID case, such as when it became
 * symptomatic and who it infected */
public class InfectionLog {

    private Time wasInfected;
    private Time becameSymptomatic;

    public InfectionLog() {}

    public void registerInfected(Time t) {
        if (wasInfected == null) {
            wasInfected = t;
        }
    }

    public void registerSymptomatic(Time t) {
        if (becameSymptomatic == null) {
            becameSymptomatic = t;
        }
    }
    
    public Time getSymptomaticTime() {
        return becameSymptomatic;
    }

    public Time getInfectionTime() {
        return wasInfected;
    }
}
