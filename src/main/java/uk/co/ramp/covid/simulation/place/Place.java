package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.output.network.ContactsWriter;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.CStatus;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.*;

import org.apache.commons.math3.random.RandomDataGenerator;

public abstract class Place {

    // People are managed in 2 lists, those currently in the place "people" and
    // those who will be in the place in the next hour "nextPeople"
    private List<Person> people;
    private List<Person> nextPeople;
    
    protected double sDistance;
    protected double transConstant;
    protected double transAdjustment;
    private final RandomDataGenerator rng;
    protected double environmentAdjustment;



    abstract public void reportInfection(Time t, Person p, DailyStats s);

    protected  void reportDeath(DailyStats s) {
        s.incAdditionalDeaths();
    }
    

    public Place() {
        this.rng = RNG.get();
        this.people = new ArrayList<>();
        this.nextPeople = new ArrayList<>();
        this.transConstant = PopulationParameters.get().buildingProperties.baseTransmissionConstant;
        this.sDistance = 1.0;
        this.transAdjustment = 1.0;
        this.environmentAdjustment = 1.0;
    }

    protected double getEnvironmentAdjustment(Person susceptible, Person target, Time t) {
    	return environmentAdjustment;
    }

    // We use iterable here to make it harder to accidentally modify people (which is effectively immutable)
    public Iterable<Person> getPeople() {
        return people;
    }
    
    public int getNumPeople() {
        return people.size();
    }
    
    // Immediately add a new person to this place (for use in initialisation. Not movement)
    public void addPerson(Person p) { people.add(p); }
    
    public void removePerson(Person p) { people.remove(p); }
    
    public boolean personInPlace(Person p) { return people.contains(p); }

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
    
    private void addContacts(Time t, ContactsWriter contactsWriter) {
        double weight = this.getTransConstant();
        for (Person a : people) {
            for (Person b : people) {
                if (a != b) {
                    contactsWriter.addContact(t, a, b, this, weight);
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
    public void doInfect(Time t, DailyStats stats, ContactsWriter contactsWriter) {
        if (contactsWriter != null) {
            addContacts(t, contactsWriter);
            return; // don't do infections
        }

       stepInfections(t, stats);

        for (Person cPers : people) {
            if (cPers.isInfectious() && getNumPeople() > 1) {
            	int nInfected = rng.nextBinomial(getNumPeople() - 1, getTransP(cPers) / 24);
            	List <Person> usedPerson = new ArrayList<>();
            	if(nInfected > 0) {
            		for(int nextInt = 1; nextInt <= nInfected; nextInt++) {
            		Person nPers = people.get(rng.nextInt(0, getNumPeople() - 1));
	            		if(nPers != cPers && !(usedPerson.contains(nPers))) {
	            			usedPerson.add(nPers);
	                        boolean infected = nPers.infChallenge(getEnvironmentAdjustment(nPers, cPers, t));
	                        if (infected) {
	                            registerInfection(t, nPers, stats);
	                            nPers.getcVirus().getInfectionLog().registerInfected(t);
	                            cPers.getcVirus().getInfectionLog().registerSecondaryInfection(t, nPers);
	                        }
	            		}
	            		else {
	            			nextInt --;
	            		}
            		}
            	}
            }
        }
    }
    
    public double getBaseTransP(Person infected) {
        return getTransConstant() * sDistance * infected.getTransAdjustment();
    }
    
    public double getTransP(Person infected) {
        return getBaseTransP(infected);
    }
    
    /** Do a timestep by switching to the new set of people */
    public void commitMovement() {
        // Switch the movement buffers
        List<Person> tmp = people;
        people = nextPeople;
        nextPeople = tmp;
        nextPeople.clear();
        
        for (Person p : people) {
            p.unsetMoved();
        }
        
    }

    public void sendFamilyHome(Person p, CommunalPlace place, Time t) {
        for (Person q : people) {
            if (p != q
                    && q.getHome() == p.getHome()
                    && !p.hasMoved()
                    && !q.worksNextHour(place, t)) {
                q.returnHome(this);
            }
        }
    }

    public void keepFamilyInPlace(Person p, CommunalPlace place, Time t) {
        for (Person q : people) {
            if (p != q && q.getHome() == p.getHome() && !p.hasMoved()) {
                q.stayInPlace(this);
            }
        }
    }

    protected void remainInPlace() {
        for (Person p : getPeople() ) {
            if (!p.hasMoved()) {
                p.stayInPlace(this);
            }
        }
    }


    /** Handles movement between people in this place */
    public abstract void determineMovement(Time t, DailyStats s, boolean lockdown, Places places);
}
