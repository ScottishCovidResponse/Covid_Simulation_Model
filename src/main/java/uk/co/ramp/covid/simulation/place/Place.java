package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.output.NetworkGenerator;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.CStatus;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Places;

import java.util.*;

public abstract class Place {

    // People are managed in 2 lists, those currently in the place "people" and
    // those who will be in the place in the next hour "nextPeople"
    protected List<Person> people;
    protected List<Person> nextPeople;
    
    protected double sDistance;
    protected double transConstant;
    protected double transAdjustment;


    abstract public void reportInfection(Time t, Person p, DailyStats s);

    protected  void reportDeath(DailyStats s) {

    }

    public Place() {
        this.people = new ArrayList<>();
        this.nextPeople = new ArrayList<>();
        this.transConstant = PopulationParameters.get().buildingProperties.baseTransmissionConstant;
        this.sDistance = 1.0;
        this.transAdjustment = 1.0;
    }

    public List<Person> getPeople() {
        return people;
    }
    
    // Immediately add a new person to this place
    public void addPerson(Person p) { people.add(p); }

    // Add a person to this place in the next time step
    public void addPersonNext(Person p) {
        nextPeople.add(p);
    }

    private void registerInfection(Time t, Person p, DailyStats s) {
        reportInfection(t, p, s);
        p.reportInfection(s);
    }


    private void registerDeath(Person p, DailyStats stats) {
        reportDeath(stats);
        p.reportDeath(stats);
    }

    protected double getTransConstant() {
    	if(people.size() == 0) {
    	   return 0.0;
        }

    	if(people.size() <= transAdjustment) {
    	    return transConstant;
        }

        return transConstant * transAdjustment / people.size();
    }
    
    private void writeContact(Time t) {
        for (Person a : people) {
            for (Person b : people) {
                if (a != b) {
                    NetworkGenerator.writeContact(t, a, b, this, this.getTransConstant());
                }
            }
        }
    }

    private void stepInfections(Time t, DailyStats stats) {
        List<Person> deaths = new ArrayList<>();
        for (Person cPers : people) {
            if (cPers.getInfectionStatus() && !cPers.isRecovered()) {
                cPers.stepInfection(t);
                if (cPers.cStatus() == CStatus.DEAD) {
                    registerDeath(cPers, stats);
                    deaths.add(cPers);
                }
                if (cPers.cStatus() == CStatus.RECOVERED) {
                    cPers.recover();
                }
            }
        }
        people.removeAll(deaths);
    }

    /** Handles infections between all people in this place */
    public void doInfect(Time t, DailyStats stats) {
        if (NetworkGenerator.generating()) {
            writeContact(t);
            return; // don't do infections
        }

       stepInfections(t, stats);

        for (Person cPers : people) {
            if (cPers.isInfectious()) {
                for (Person nPers : people) {
                    if (cPers != nPers && !nPers.getInfectionStatus()) {
                        double transP = getTransP(t, cPers, nPers);
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
    }
    
    public double getBaseTransP(Person infected) {
        return getTransConstant() * sDistance * infected.getTransAdjustment();
    }
    
    public double getTransP(Time t, Person infected, Person target) {
        return getBaseTransP(infected);
    }
    
    /** Do a timestep by switching to the new set of people */
    public void stepPeople() {

        // Anyone who didn't move should remain.
        nextPeople.addAll(people);

        // Switch the movement buffers
        List<Person> tmp = people;
        people = nextPeople;
        nextPeople = tmp;
        nextPeople.clear();
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
    public abstract void doMovement(Time t, boolean lockdown, Places places);
}
