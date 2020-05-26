package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.CStatus;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Place {

    protected List<Person> people;
    protected double sDistance;
    protected double transProb;

    abstract public void reportInfection(DailyStats s);

    public Place() {
        this.people = new ArrayList<>();
        this.transProb = PopulationParameters.get().getpBaseTrans();
        this.sDistance = 1.0;
    }

    public List<Person> getPeople() {
        return people;
    }
    
    public void addPerson(Person p) {
        people.add(p);
    }

    private void registerInfection(DailyStats s, Person p) {
        reportInfection(s);
        p.reportInfection(s);
    }

    /** Handles infections between all people in this place */
    public void doInfect(DailyStats stats) {
        List<Person> deaths = new ArrayList<>();
        for (Person cPers : people) {
            if (cPers.getInfectionStatus() && !cPers.isRecovered()) {
                cPers.stepInfection();
                if (cPers.isInfectious()) {
                    for (Person nPers : people) {
                        if (cPers != nPers) {
                            if (!nPers.getInfectionStatus()) {
                                boolean infected = nPers.infChallenge(this.transProb * this.sDistance);
                                if (infected) {
                                    registerInfection(stats, nPers);
                                }
                            }
                        }
                    }
                }
                if (cPers.cStatus() == CStatus.DEAD) {
                    cPers.reportDeath(stats);
                    deaths.add(cPers);
                }
                if (cPers.cStatus() == CStatus.RECOVERED) {
                    cPers.setRecovered(true);
                }
            }
        }
        people.removeAll(deaths);
    }

    /** Handles movement between people in this place */
    public abstract void doMovement(int day, int hour);
}
