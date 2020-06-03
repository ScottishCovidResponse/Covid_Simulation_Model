package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.CStatus;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

import java.util.ArrayList;
import java.util.List;

public abstract class Place {

    // People are managed in 2 lists, those currently in the place "people" and
    // those who will be in the place in the next hour "nextPeople"
    protected List<Person> people;
    protected List<Person> nextPeople;
    
    protected double sDistance;
    protected double transProb;
    protected double transAdjustment;


    abstract public void reportInfection(Time t, Person p, DailyStats s);

    public Place() {
        this.people = new ArrayList<>();
        this.nextPeople = new ArrayList<>();
        this.transProb = PopulationParameters.get().getpBaseTrans();
        this.sDistance = 1.0;
        this.transAdjustment = 1.0;

    }

    public List<Person> getPeople() {
        return people;
    }

    public void addPersonNext(Person p) {
        nextPeople.add(p);
    }

    private void registerInfection(Time t, Person p, DailyStats s) {
        reportInfection(t, p, s);
        p.reportInfection(s);
    } 
    private double getTransProb() {
    	double cTransProb = 0.0;
    	if(people.size() == 0) cTransProb = 0.0;
    	else if(people.size() <= transAdjustment) cTransProb = transProb;
      	else if(people.size() > transAdjustment) cTransProb = transProb * transAdjustment / people.size();
    	return cTransProb;
    }
    
    /** Handles infections between all people in this place */
    public void doInfect(Time t, DailyStats stats) {
        List<Person> deaths = new ArrayList<>();
        for (Person cPers : people) {
            if (cPers.getInfectionStatus() && !cPers.isRecovered()) {
                cPers.stepInfection(t);
                if (cPers.isInfectious()) {
                    for (Person nPers : people) {
                        if (cPers != nPers) {
                            if (!nPers.getInfectionStatus()) {
                                boolean infected = nPers.infChallenge(this.getTransProb() * this.sDistance * cPers.getTransAdjustment());
                                if (infected) {
                                    registerInfection(t, nPers, stats);
                                    nPers.getcVirus().getInfectionLog().registerInfected(t);
                                    cPers.getcVirus().getInfectionLog().registerSecondaryInfection(t, nPers);
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
    
    /** Do a timestep by switching to the new set of people */
    public void stepPeople() {
        // Anyone who didn't move should remain.
        nextPeople.addAll(people);
        people = nextPeople;
        nextPeople = new ArrayList<>();
    }

    public List<Person> sendFamilyHome(Person p, CommunalPlace place, Time t) {
        List<Person> left = new ArrayList<>();
        for (Person q : people) {
            if (p != q && !q.worksNextHour(place, t, false) && q.getHome() == p.getHome()) {
                q.returnHome();
                left.add(q);
            }
        }
        return left;
    }

    /** Handles movement between people in this place */
    public abstract void doMovement(Time t, boolean lockdown);
}
