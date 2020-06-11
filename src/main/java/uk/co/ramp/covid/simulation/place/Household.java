package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.*;

public abstract class Household extends Place {

    private final List<Household> neighbours;
    private final Places places;
    
    private boolean willIsolate = false;
    private boolean lockCompliant = false;
    private int isolationTimer = 0;

    // Create household defined by who lives there
    public Household(Places places) {
        this.neighbours = new ArrayList<>();
        this.places = places;
        if (PopulationParameters.get().householdProperties.pWillIsolate.sample()) {
            willIsolate = true;
        }
        if (PopulationParameters.get().householdProperties.pLockCompliance.sample()) {
        	lockCompliant = true;
        }

    }
    
    public void forceIsolationtimer(int time) {
        isolationTimer = time;
    }

    public List<Household> getNeighbours() {
        return neighbours;
    }

    public int nNeighbours() {
        return neighbours.size();
    }


    public int getHouseholdSize() {
        return adults + pensioners + children;
    }
    
    @Override
    protected double getTransConstant() {
    	return transConstant;
    }

    public void addNeighbour(Household n) {
        neighbours.add(n);
    }

    public boolean isNeighbour(Household n) { return neighbours.contains(n); }
    
    public void isolate() {
        if (willIsolate) {
            isolationTimer = PopulationParameters.get().householdProperties.householdIsolationPeriod;
        }
    }
    
    public void stopIsolating() {
        isolationTimer = 0;
    }
    
    public boolean isIsolating() {
        return isolationTimer > 0;
    }

    public boolean seedInfection() {
        List<Person> inhabitants = getInhabitants();
        Person cPers = inhabitants.get(RNG.get().nextInt(0, inhabitants.size() - 1));
        if (cPers.infect()) {
            // Seeding happens at the start so we use the default time here.
            // This will need to be altered to allow seeds during a run if required.
            cPers.getcVirus().getInfectionLog().registerInfected(new Time());
            return true;
        }
        return false;
    }

    public void sendNeighboursHome(Time t) {
        ArrayList<Person> left = new ArrayList<>();

        for (Person p : getVisitors()) {
            // People may have already left if their family has
            if (left.contains(p)) {
                continue;
            }

            // Under certain conditions we must go home, e.g. if there is a shift starting soon
            if (p.mustGoHome(t)) {
                left.add(p);
                p.returnHome(this);
                left.addAll(getFamilyToSendHome(p, null, t));
            }
            else if (PopulationParameters.get().householdProperties.pVisitorsLeaveHousehold.sample()) {
                left.add(p);
                left.addAll(getFamilyToSendHome(p, null, t));
            }
        }

        left.forEach(p -> p.returnHome(this));
    }
    
    private boolean isVisitor(Person p) {
        return p.getHome() != this;
    }

    public List<Person> getVisitors() {
        List<Person> ret = new ArrayList<>();
        for (Person p : getPeople()) {
            if (isVisitor(p)) {
                ret.add(p);
            }
        }
        return ret;
    }

    public List<Person> getInhabitants() {
        List<Person> ret = new ArrayList<>();
        for (Person p : getPeople()) {
            if (!isVisitor(p)) {
                ret.add(p);
            }
        }
        return ret;
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (getInhabitants().contains(p)) {
            s.incInfectionsHomeInhabitant();
        } else {
            s.incInfectionsHomeVisitor();
        }
    }


    @Override
    public void decideOnMovement(Time t, boolean lockdown) {
        if (!isIsolating()) {
            // Ordering here implies work takes highest priority, then shopping trips have higher priority
            // than neighbour and restaurant trips
            moveShift(t, lockdown);

            // Shops are only open 8-22
            if (t.getHour() + 1 >= 8 && t.getHour() + 1 < 22) {
                moveShop(t, lockdown);
            }

            moveNeighbour(lockdown);

            // Restaurants are only open 8-22
            if (!lockdown && t.getHour() + 1 >= 8 && t.getHour() + 1 < 22) {
                moveRestaurant(t);
            }
        }

        // We always send neighbours home outside the isIsolating condition to ensure
        // they aren't stuck when we start isolating
        sendNeighboursHome(t);
    }

      public void doTesting(Time t) {
        for (Person p : getInhabitants()) {
            if (p.isinfected()) {
                Time symptomaticTime = p.getcVirus().getInfectionLog().getSymptomaticTime();
                if (symptomaticTime != null
                        && symptomaticTime.getAbsTime() <= t.getAbsTime() + 24
                        && CovidParameters.get().testParameters.pDiagnosticTestAvailable.sample()) {
                    p.getTested();
                }
            }
        }
    }

    private void moveNeighbour(boolean lockdown) {

        if((!lockdown) || (!this.lockCompliant)) {
	        for (Household n : getNeighbours()) {
	            if (n.isIsolating()) {
	                continue;
	            }

                if (PopulationParameters.get().householdProperties.pHouseholdVisitsNeighbour.sample()) {
	                // We visit neighbours as a family
	                for (Person p : getInhabitants()) {
	                    if (!p.getQuarantine()) {
	                        movePersonToPlace(p, n);
	                    }
	                }
	                break;
	            }
	        }
        }
    }

    private void moveShop(Time t, boolean lockdown) {
        Probability visitProb = PopulationParameters.get().householdProperties.pGoShopping;
        if (lockdown) {
            visitProb = new Probability(visitProb.asDouble() * 0.5);
        }

        if (visitProb.sample()) {
            Shop s = places.getRandomShop();
            if (s == null) {
                return;
            }

            // Sometimes we can get a case where there are only small shops (open 9-5) so we fail to find a shop
            // In this case we time out the search.
            int retries = 5;
            while (!s.isVisitorOpenNextHour(t)) {
                s = places.getRandomShop();
                retries--;
                if (retries == 0) {
                    return;
                }
            }

            // Go to restaurants as a family
            for (Person p : getInhabitants()) {
                if (!p.getQuarantine()) {
                	movePersonToPlace(p, s);
                }
            }
        }
    }

    private void moveRestaurant(Time t) {

        if (PopulationParameters.get().householdProperties.pGoRestaurant.sample()) {
            Restaurant r = places.getRandomRestaurant();
            if (r == null) {
                return;
            }

            int retries = 5;
            while (!r.isVisitorOpenNextHour(t)) {
                r = places.getRandomRestaurant();
                retries--;
                if (retries == 0) {
                    return;
                }
            }

            // Go to restaurants as a family
            for (Person p : getInhabitants()) {
                if (!p.getQuarantine()) {
                	movePersonToPlace(p, r);
                }
            }
        }
    }

    private void moveShift(Time t, boolean lockdown) {
        for (Person p : getInhabitants()) {
            if (p.worksNextHour(p.getPrimaryCommunalPlace(), t, lockdown)) {
                if (!p.getQuarantine()) {
                    p.visitPrimaryPlace(this);
                }
            }
        }
    }
    
    public void dayEnd() {
        if (isIsolating()) {
            isolationTimer--;
        }
    }

    // Household Type management
    protected int adults = 0;
    protected int children = 0;
    protected int pensioners = 0;

    // These functions control the allocation of particular household types.
    // The *Required functions should return true when it is essential another member of that type be added to the household.
    // The additional*Allowed functions should return true if they can accept another member of that type, but it is not essential that they do so.
    // For example, a household requiring at least one adult would have adultsRequired be true when adults < 1,
    // and additionalAdultsRequired as true (allowing any number of additional adults to be added).
    public abstract boolean adultRequired();
    public abstract boolean additionalAdultsAllowed();
    public abstract boolean childRequired();
    public abstract boolean additionalChildrenAllowed();
    public abstract boolean pensionerRequired();
    public abstract boolean additionalPensionersAllowed();
    public abstract boolean adultAnyAgeRequired();
    public abstract boolean additionalAdultAnyAgeAllowed();

    public void addAdult(Adult p) {
        if (adultRequired() || additionalAdultsAllowed()
                || adultAnyAgeRequired() || additionalAdultAnyAgeAllowed()) {
            addNewPerson(p);
            p.setHome(this);
            adults++;
        } else {
            throw new InvalidHouseholdAllocationException("Cannot add adult to household");
        }
      
    }
    
    public void addChildOrInfant(Person p) {
        // We need to do some type inference here to handle the fact infants are
        // treated as children for household population
        if ((childRequired() || additionalChildrenAllowed()) && (p instanceof Child || p instanceof Infant)) {
            addNewPerson(p);
            p.setHome(this);
            children++;
        } else {
            throw new InvalidHouseholdAllocationException("Cannot add child/infant to household");
        }
    }

    public void addAdultOrPensioner(Person p) {
        // We need to do some type inference here to handle "any age" adults
        if ((adultAnyAgeRequired() || additionalAdultAnyAgeAllowed()) && (p instanceof Adult || p instanceof Pensioner)) {
            addNewPerson(p);
            p.setHome(this);
            if (p instanceof Adult) {
                adults++;
            } else {
                pensioners++;
            }
        } else {
            throw new InvalidHouseholdAllocationException("Cannot add adult/pensioner to household");
        }
    }


    public void addPensioner(Pensioner p) {
        if (pensionerRequired() || additionalPensionersAllowed()
                || adultAnyAgeRequired() || additionalAdultAnyAgeAllowed()) {
            addNewPerson(p);
            p.setHome(this);
            pensioners++;
        } else {
            throw new InvalidHouseholdAllocationException("Cannot add pensioner to household");
        }
    }

}
