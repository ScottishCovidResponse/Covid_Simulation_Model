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
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Place {
    private static final Logger LOGGER = LogManager.getLogger(Place.class);

    // People are managed in 2 lists, those currently in the place "people" and
    // those who will be in the place in the next hour "nextPeople"
    protected List<Person> people;
    protected List<Person> nextPeople;
    
    protected double sDistance;
    protected double transConstant;
    protected double expectedInteractionsPerHour;
    private final RandomDataGenerator rng;
    protected double environmentAdjustment;



    abstract public void reportInfection(Time t, Person p, DailyStats s);

    protected  void reportDeath(DailyStats s) {
        s.additionalDeaths.increment();
    }
    

    public Place() {
        this.rng = RNG.get();
        this.people = new ArrayList<>();
        this.nextPeople = new ArrayList<>();
        this.transConstant = PopulationParameters.get().buildingProperties.baseTransmissionConstant;
        this.sDistance = 1.0;
        this.expectedInteractionsPerHour = 1.0;
        this.environmentAdjustment = 1.0;
    }

    protected double getEnvironmentAdjustment(Person susceptible, Person infected, Time t) {
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

    protected void registerInfection(Time t, Person p, DailyStats s) {
        reportInfection(t, p, s);
        p.reportInfection(s);
    }


    private void registerDeath(Person p, DailyStats stats) {
        reportDeath(stats);
        p.recordDeath(stats);
    }

    protected double getTransConstant() {
    	if(people.size() == 0) {
    	   return 0.0;
        }

    	if(people.size() <= expectedInteractionsPerHour) {
    	    return transConstant;
        }

        return transConstant * expectedInteractionsPerHour / people.size();
    }
    
    private Collection<Pair<Integer, Integer>> genRandomPairs(int minValue, int maxValue, int count) {
        Set<Pair<Integer, Integer>> pairs = new HashSet<>();
        while(pairs.size() < count) {
            int i = rng.nextInt(minValue, maxValue);
            int j = rng.nextInt(minValue, maxValue);
            if (i != j) {
                if (i > j) {
                    int k = i;
                    i = j;
                    j = k;
                }
                pairs.add(new Pair<>(i, j));
            }
        }
        return pairs;
    }

    // rng.nextBinomial() expects an integer. This method handles >Integer.MAX_VALUE trials,
    // taking advantage of the fact that Bin(n + m, p) = Bin(n, p) + Bin(m, p).
    public static int nextBinomial(RandomDataGenerator rng, long numberOfTrials, double probabilityOfSuccess) {
        long result = 0;
        long remainingTrials = numberOfTrials;

        while (remainingTrials > Integer.MAX_VALUE) {
            result += rng.nextBinomial(Integer.MAX_VALUE, probabilityOfSuccess);
            remainingTrials -= Integer.MAX_VALUE;
        }
        result += rng.nextBinomial((int)remainingTrials, probabilityOfSuccess);

        if (result > Integer.MAX_VALUE) {
            LOGGER.error("Calculated number of contacts is too large.");
            throw new IntegerOverflowException("Calculated number of contacts is too large.");
        }
        return (int)result;
    }

    protected void addContacts(Time t, ContactsWriter contactsWriter) {
        long nPairs = (long) getNumPeople() * (getNumPeople() - 1) / 2;
        int nContacts = nextBinomial(rng, nPairs, this.getTransConstant() / 24.0);
        Collection<Pair<Integer, Integer>> randomPairs = genRandomPairs(0, getNumPeople() - 1, nContacts);
        for (Pair<Integer, Integer> pair : randomPairs) {
            contactsWriter.addContact(t, people.get(pair.getFirst()), people.get(pair.getSecond()), this, 1);
        }
    }

    protected void stepInfections(Time t, DailyStats stats) {
        List<Person> deaths = new ArrayList<>();
        for (Person cPers : people) {
            if (cPers.isInfected()) {
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
        infectOthers(t, stats);
    }

    public void infectOthers(Time t, DailyStats stats) {
        for (Person cPers : people) {
            if (cPers.isInfectious() && getNumPeople() > 1) {
            	int nInfected = rng.nextBinomial(getNumPeople() - 1, getTransP(cPers) / 24.0);
            	List <Person> usedPerson = new ArrayList<>();
            	if(nInfected > 0) {
            		int safetyValve = 0;
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
	            		else if(safetyValve < getNumPeople() * 100){
	            			safetyValve ++;
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

    public void keepFamilyInPlace(Person p) {
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
    
    public void setSocialDistancing(double sdist) {
        sDistance = sdist;
    }


    /** Handles movement between people in this place */
    public abstract void determineMovement(Time t, DailyStats s, Places places);
}
