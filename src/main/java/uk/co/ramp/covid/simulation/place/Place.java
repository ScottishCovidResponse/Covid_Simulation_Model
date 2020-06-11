package uk.co.ramp.covid.simulation.place;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.CStatus;
import uk.co.ramp.covid.simulation.population.Person;

public abstract class Place {

    // People are managed in 2 lists, those currently in the place "people" and
    // those who will be in the place in the next hour "nextPeople"
    private Collection<Person> people;
    private Collection<Person> peopleMovingAway;
    private Map<Person, Place> movementsToHere;  // we might want to hold more info, like transport type, in which case will need a MovementInfo class

    protected double sDistance;
    protected double transConstant;
    protected double transAdjustment;


    abstract public void reportInfection(Time t, Person p, DailyStats s);

    public Place() {
        this.people = new LinkedHashSet<Person>();
        this.peopleMovingAway = new LinkedHashSet<Person>();
        this.movementsToHere = new LinkedHashMap<Person, Place>();
        this.transConstant = PopulationParameters.get().buildingProperties.baseTransmissionConstant;
        this.sDistance = 1.0;
        this.transAdjustment = 1.0;
    }

    public Collection<Person> getPeople() {
        return people;
    }

    public int getNumberOfPeople() {
		return people.size();
	}

    /** adding people during initialisation, i.e. not movement */
    public void addNewPerson(Person p) {
    	people.add(p);
    }

    /** Move a person from this location to fromPlace */
    public void movePersonToPlace(Person p, Place toPlace) {
		people.remove(p);
		peopleMovingAway.add(p);
    	toPlace.movementsToHere.put(p, this);  // this puts movement in a 'buffer' that is applied by implementMovement()
    }

    private void registerInfection(Time t, Person p, DailyStats s) {
        reportInfection(t, p, s);
        p.reportInfection(s);
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
    
    /** Handles infections between all people in this place */
    public void doInfect(Time t, DailyStats stats) {
        Collection<Person> deaths = new LinkedHashSet<>();
        for (Person cPers : people) {
            if (cPers.getInfectionStatus() && !cPers.isRecovered()) {
                cPers.stepInfection(t);
                if (cPers.isInfectious()) {
                    for (Person nPers : people) {
                        if (cPers != nPers) {
                            if (!nPers.getInfectionStatus()) {
                                boolean infected = nPers.infChallenge(this.getTransConstant() * this.sDistance * cPers.getTransAdjustment());
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
    
    /** Do a timestep by adding arriving people */
    public void implementMovement() {
    	people.addAll(movementsToHere.keySet());
    	peopleMovingAway = new LinkedHashSet<Person>();
    	movementsToHere = new LinkedHashMap<Person, Place>();
    }

    public Collection<Person> getFamilyToSendHome(Person p, CommunalPlace place, Time t) {
    	Collection<Person> familyToSendHome = new LinkedHashSet<Person>();
        for (Person q : people) {
            if (p != q && !q.worksNextHour(place, t, false) && q.getHome() == p.getHome()) {
            	familyToSendHome.add(q);
            }
        }
        return familyToSendHome;
    }

    /** Handles movement between people in this place */
    public abstract void decideOnMovement(Time t, boolean lockdown);

	public Collection<Person> getPeopleMovingToHere() {
		return movementsToHere.keySet();
	}
	
	public Collection<Person> getPeopleMovingFromHere() {
		return peopleMovingAway;
	}
}
