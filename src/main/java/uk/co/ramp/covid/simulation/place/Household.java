package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.*;

public abstract class Household extends Place implements Home {

    private final List<Household> neighbours;
    
    private boolean willIsolate = false;
    private boolean lockCompliant = false;
    private int isolationTimer = 0;
    private boolean visitsNeighbourToday = false;

    // Create household defined by who lives there
    public Household() {
        this.neighbours = new ArrayList<>();
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
        // Homes can be empty if, for example, pensioners have all gone to care
        if (inhabitants.isEmpty()) {
            return false;
        }

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

        final int numInhabitants = getNumInhabitants();
        for (Person p : getPeople()) {
            if (!isVisitor(p)) {
                continue;
            }

            // People may have already left if their family has
            if (left.contains(p)) {
                continue;
            }

            // Go home if the house inhabitants have either left, or were never here
            if (numInhabitants == 0) {
                left.add(p);
                left.addAll(getFamilyToSendHome(p, null, t));
                continue;
            }

            // Under certain conditions we must go home, e.g. if there is a shift starting soon
            if (p.mustGoHome(t)) {
                left.add(p);
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

    private boolean isInhabitant(Person p) { return p.getHome() == this; }

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

    public int getNumInhabitants() {
        int n = 0;
        for (Person p : getPeople()) {
            if (isInhabitant(p)) {
                n++;
            }
        }
        return n;
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (getInhabitants().contains(p)) {
            s.incInfectionsHomeInhabitant();
        } else {
            s.incInfectionsHomeVisitor();
        }
    }
    
    public void goToHospital(Time t, Places places) {
        List<Person> left = new ArrayList<>();
        List<Runnable> hospitalMoves = new ArrayList<>();

        for (Person p : getPeople()) {
            if (p.cStatus() != null && p.cStatus() == CStatus.PHASE2) {
                if (p.goesToHosptialInPhase2()) {
                    p.hospitalise();
                    CovidHospital h = places.getRandomCovidHospital();
                    hospitalMoves.add(() -> p.moveTo(this, h));
                } else if (isVisitor(p)) {
                    left.add(p);
                    left.addAll(getFamilyToSendHome(p, null, t));
                }
            }
        }
        left.forEach(p -> p.returnHome(this));
        hospitalMoves.forEach(m -> m.run());
    }

    @Override
    public void determineMovement(Time t, boolean lockdown, Places places) {
        goToHospital(t, places);

        if (!isIsolating() && getNumInhabitants() > 0) {
            // Ordering here implies work takes highest priority, then shopping trips have higher priority
            // than neighbour and restaurant trips
            moveShift(t, lockdown);

            // Shops are only open 8-22
            if (t.getHour() + 1 >= 8 && t.getHour() + 1 < 22) {
                moveShop(t, lockdown, places);
            }

            if (!neighbours.isEmpty()) {
                moveNeighbour(t, lockdown);
            }

            // Restaurants are only open 8-22
            if (!lockdown && t.getHour() + 1 >= 8 && t.getHour() + 1 < 22) {
                moveRestaurant(t, places);
            }
        }

        // We always send neighbours home outside the isIsolating condition to ensure
        // they aren't stuck when we start isolating
        sendNeighboursHome(t);
    }

      public void doTesting(Time t) {
        for (Person p : getPeople()) {
            if (isInhabitant(p) && p.isinfected()) {
                Time symptomaticTime = p.getcVirus().getInfectionLog().getSymptomaticTime();
                if (symptomaticTime != null
                        && symptomaticTime.getAbsTime() <= t.getAbsTime() + 24
                        && CovidParameters.get().testParameters.pDiagnosticTestAvailable.sample()) {
                    p.getTested();
                }
            }
        }
    }

    private void moveNeighbour(Time t, boolean lockdown) {
       if (!visitsNeighbourToday
                || t.getHour() + 1 < PopulationParameters.get().householdProperties.neighbourOpeningTime
                || t.getHour() + 1 >= PopulationParameters.get().householdProperties.neighbourClosingTime) {
            return;
        }

        List<Runnable> neighbourMoves = new ArrayList<>();
        if(!lockdown || !lockCompliant) {

            int openT = PopulationParameters.get().householdProperties.neighbourOpeningTime;
            int closeT = PopulationParameters.get().householdProperties.neighbourClosingTime;
            // If we should visit a neighbour, do so at random
            if (new Probability(1.0 / (closeT - openT)).sample()) {
                List<Household> neighbours = getNeighbours();
                Household n = neighbours.get(RNG.get().nextInt(0, neighbours.size() - 1));

                // Retry is neighbour is isolating
                while (neighbours.size() > 1 && n.isIsolating()) {
                    neighbours.remove(n);
                    n = neighbours.get(RNG.get().nextInt(0, neighbours.size() - 1));
                }

                // Tried all neighbours and they are all isolating so don't go anywhere
                if (n.isIsolating()) {
                    return;
                }

                // Do the visit
                for (Person p : getPeople()) {
                    if (isInhabitant(p) && !p.getQuarantine()) {
                        Household chosenNeighbour = n;
                        neighbourMoves.add(() -> p.moveTo(this, chosenNeighbour));
                    }
                }

                // Enable if we want one neighbour visit max per day
                // visitsNeighbourToday = false;
            }
        }
        neighbourMoves.forEach(m -> m.run());
    }

    private void moveShop(Time t, boolean lockdown, Places places) {
        List<Person> goingShopping = new ArrayList<>();

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
            for (Person p : getPeople()) {
                if (isInhabitant(p) && !p.getQuarantine()) {
                    goingShopping.add(p);
                }
            }
            
            Shop chosen = s;
            goingShopping.forEach(p -> p.moveTo(this, chosen));
        }

    }

    private void moveRestaurant(Time t, Places places) {
        List<Person> goingEating = new ArrayList<>();

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
            for (Person p : getPeople()) {
                if (isInhabitant(p) && !p.getQuarantine()) {
                    goingEating.add(p);
                }
            }

            Restaurant chosen = r;
            goingEating.forEach(p -> p.moveTo(this, chosen));
        }
    }

    private void moveShift(Time t, boolean lockdown) {
        List<Person> working = new ArrayList<>();
        for (Person p : getPeople()) {
            if (isInhabitant(p) && !p.getQuarantine()
                    && p.worksNextHour(p.getPrimaryCommunalPlace(), t, lockdown)) {
                working.add(p);
            }
        }
        working.forEach(p -> p.moveToPrimaryPlace(this));
    }
    
    public void dayEnd() {
        if (isIsolating()) {
            isolationTimer--;
        }

        determineDailyNeighbourVisit();
    }
    
    private double getNeighbourProbability() {
    	if(nNeighbours() == 0) return 0.0;
    	return 1.0 - (Math.pow((1.0 - PopulationParameters.get().householdProperties.householdVisitsNeighbourDaily), (double) nNeighbours()));
    }
    
    public void determineDailyNeighbourVisit() {
        // Determine if we will attempt to visit a neighbour tomorrow
        if (new Probability(getNeighbourProbability()).sample()) {
            visitsNeighbourToday = true;
        } else {
            visitsNeighbourToday = false;
        }
    }
    
    public void trySendPensionersToCare(Places places) {
        List<Person> enteringCare = new ArrayList<>();
        for (Person p : getPeople()) {
            if (p.getAge() >= PopulationParameters.get().pensionerProperties.minAgeToEnterCare
                    && PopulationParameters.get().pensionerProperties.pEntersCareHome.sample()) {
                if (p.enterCare(places)) {
                    enteringCare.add(p);
                }
            }
        }
        getPeople().removeAll(enteringCare);
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
            addPerson(p);
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
            addPerson(p);
            p.setHome(this);
            children++;
        } else {
            throw new InvalidHouseholdAllocationException("Cannot add child/infant to household");
        }
    }

    public void addAdultOrPensioner(Person p) {
        // We need to do some type inference here to handle "any age" adults
        if ((adultAnyAgeRequired() || additionalAdultAnyAgeAllowed()) && (p instanceof Adult || p instanceof Pensioner)) {
            addPerson(p);
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
            addPerson(p);
            p.setHome(this);
            pensioners++;
        } else {
            throw new InvalidHouseholdAllocationException("Cannot add pensioner to household");
        }
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incHomeDeaths();
    }

}
