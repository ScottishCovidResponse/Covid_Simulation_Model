package uk.co.ramp.covid.simulation.covid;

import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;

import java.util.HashSet;
import java.util.Set;

/** InfectionLog maintains information about a particular COVID case, such as when it became
 * symptomatic and who it infected */
public class InfectionLog {

    public class SecondaryInfection {
        private final Person p;
        private final Time t;

        public SecondaryInfection(Person p, Time t) {
            this.p = p;
            this.t = t;
        }

        public Person getInfectedPerson() {
            return p;
        }

        public Time getInfectionTime() {
            return t;
        }
    }

    private Time wasInfected;
    private Time becameSymptomatic;
    private final Set<SecondaryInfection> secondaryInfections;

    public InfectionLog() {
        secondaryInfections = new HashSet<>();
    }

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
    
    public void registerSecondaryInfection(Time t, Person p) {
        secondaryInfections.add(new SecondaryInfection(p, t));
    }
    
    public Set<SecondaryInfection> getSecondaryInfections() {
        return secondaryInfections;
    }
    
    public Time getSymptomaticTime() {
        return becameSymptomatic;
    }

    public Time getInfectionTime() {
        return wasInfected;
    }
}
