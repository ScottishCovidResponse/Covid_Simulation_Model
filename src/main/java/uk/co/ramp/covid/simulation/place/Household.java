package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.householdtypes.NeighbourGroup;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.*;
import java.util.function.Supplier;

public abstract class Household extends Place implements Home {

    private final List<Household> neighbours;
    
    private boolean willIsolate;
    private boolean lockCompliant;
    private int isolationTimer = 0;
    private boolean visitsNeighbourToday = false;

    // Create household defined by who lives there
    public Household() {
        this.neighbours = new ArrayList<>();
        willIsolate = PopulationParameters.get().householdProperties.pWillIsolate.sample();
        lockCompliant = PopulationParameters.get().householdProperties.pLockCompliance.sample();
    }
    
    public void forceIsolationtimer(int time) {
        isolationTimer = time;
    }

    public List<Household> getNeighbours() {
        return new ArrayList<>(neighbours);
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
        for (Person p : getPeople()) {
            if (p.hasMoved() || !isVisitor(p)) {
                continue;
            }
            
            // Go home if the house inhabitants have either left, or were never here
            if (getNumInhabitants() == 0) {
                p.returnHome(this);
                sendFamilyHome(p, null, t);
                continue;
            }

            // Under certain conditions we must go home, e.g. if there is a shift starting soon
            if (p.mustGoHome(t)) {
                p.returnHome(this);
                sendFamilyHome(p, null, t);
                continue;
            }

            if (PopulationParameters.get().householdProperties.pVisitorsLeaveHousehold.sample()) {
                p.returnHome(this);
                sendFamilyHome(p, null, t);
            } else {
                p.stayInPlace(this);
                keepFamilyInPlace(p, null, t);
            }
        }
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
            if (!p.hasMoved() && isInhabitant(p)) {
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
    
    public void sendCOVIDPatientsToHospital(Time t, DailyStats s, Places places) {
        for (Person p : getPeople() ) {
            if (p.hasMoved()) {
                continue;
            }

            if (p.cStatus() != null && p.cStatus() == CStatus.PHASE2) {
                if (p.goesToHosptialInPhase2()) {
                    p.hospitalise(s);
                    CovidHospital h = places.getRandomCovidHospital();
                    p.moveTo(this, h);
                } else if (isVisitor(p)) {
                    p.returnHome(this);
                    sendFamilyHome(p, null, t);
                }
            }
        }
    }

    @Override
    public void determineMovement(Time t, DailyStats s, boolean lockdown, Places places) {
        sendCOVIDPatientsToHospital(t, s, places);

        if (!isIsolating() && getNumInhabitants() > 0) {
            // Ordering here implies hospital appts take highest priority
            moveHospital(t);

            moveShift(t);

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

        // Anyone who is left stays here
        remainInPlace();
    }

    private void moveHospital(Time t) {
        for (Person p : getPeople() ) {
            if (p.hasMoved() || !p.hasHospitalAppt()) {
                continue;
            }

            if (p.getHospitalAppt().getStartTime().equals(t.advance())) {
               // Children need to find an adult to go with them else they don't go
               if (p instanceof Child || p instanceof Infant) {
                   for (Person per : getPeople() ) {
                       if (per != p && (per instanceof Adult || per instanceof Pensioner)
                               && !per.hasMoved() && !per.hasHospitalAppt()) {
                           // Giving them the same appt ensures they leave at the same time
                           per.setHospitalAppt(p.getHospitalAppt());
                           per.moveTo(this, per.getHospitalAppt().getApptLocation());
                           p.moveTo(this, p.getHospitalAppt().getApptLocation());
                           break;
                       }
                   }
               } else {
                    p.moveTo(this, p.getHospitalAppt().getApptLocation());
                }
            }
        }
    }

      public void handleSymptomaticCases(Time t) {
        for (Person p : getPeople()) {
            if (isInhabitant(p) && p.isinfected()) {
                Time symptomaticTime = p.getcVirus().getInfectionLog().getSymptomaticTime();
                
                if (symptomaticTime == null) {
                    continue;
                }

                if (symptomaticTime.getAbsTime() <=
                        t.getAbsTime() + PopulationParameters.get().personProperties.symptomToQuarantineDelay) {
                    p.enterQuarantine();

                    // A person might choose not to quarantine, in which case the household doesn't isolate either
                    if (p.isQuarantined()) {
                        // Isolation timer resets each time a new inhabitant gets symptoms
                        isolate();
                    }

                }

                if (symptomaticTime.getAbsTime() <=
                        t.getAbsTime() + PopulationParameters.get().personProperties.symptomToTestingDelay
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
       
        if(!lockdown || !lockCompliant) {

            int openT = PopulationParameters.get().householdProperties.neighbourOpeningTime;
            int closeT = PopulationParameters.get().householdProperties.neighbourClosingTime;
            // If we should visit a neighbour, do so at random
            if (new Probability(1.0 / (closeT - openT)).sample()) {
                List<Household> neighbours = getNeighbours();
                Household n = neighbours.get(RNG.get().nextInt(0, neighbours.size() - 1));

                // Retry if neighbour is isolating
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
                    if (!p.hasMoved() && isInhabitant(p) && !p.isQuarantined()) {
                        p.moveTo(this, n);
                    }
                }

                // Enable if we want one neighbour visit max per day
                // visitsNeighbourToday = false;
            }
        }
    }
    
    private void familyTrip(Time t, Supplier<CommunalPlace> randPlace, Probability pVisit) {
        if (pVisit.sample()) {
            CommunalPlace place = randPlace.get();
            if (place == null) {
                return;
            }

            int retries = 5;
            while (!place.isVisitorOpenNextHour(t)) {
                place = randPlace.get();
                retries--;
                if (retries == 0) {
                    return;
                }
            }

            for (Person p : getPeople()) {
                if (!p.hasMoved() && isInhabitant(p) && !p.isQuarantined()) {
                    p.moveTo(this, place);
                }
            }
        }
    }

    private void moveShop(Time t, boolean lockdown, Places places) {
        Probability visitProb = PopulationParameters.get().householdProperties.pGoShopping;
        if (lockdown) {
            visitProb = new Probability(visitProb.asDouble() * 0.5);
        }
        familyTrip(t, places::getRandomShop, visitProb);
    }

    private void moveRestaurant(Time t, Places places) {
        Probability visitProb = PopulationParameters.get().householdProperties.pGoRestaurant;
        familyTrip(t, places::getRandomRestaurant, visitProb);
    }

    private void moveShift(Time t) {
        for (Person p : getPeople()) {
            if (p.hasMoved()) {
                continue;
            }

            if (isInhabitant(p) && !p.isQuarantined()
                    && p.worksNextHour(p.getPrimaryCommunalPlace(), t)) {
                p.moveToPrimaryPlace(this);
            }
        }
    }
    
    public void dayEnd() {
        if (isIsolating()) {
            isolationTimer--;
        }

        determineDailyNeighbourVisit();
    }
    
    private double getNeighbourProbability() {
    	if(nNeighbours() == 0) return 0.0;
    	return 1.0 - (Math.pow((1.0 - PopulationParameters.get().householdProperties.householdVisitsNeighbourDaily), nNeighbours()));
    }
    
    public void determineDailyNeighbourVisit() {
        // Determine if we will attempt to visit a neighbour tomorrow
        visitsNeighbourToday = new Probability(getNeighbourProbability()).sample();
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
        for (Person p : enteringCare) {
            removePerson(p);
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
    public abstract NeighbourGroup getNeighbourGroup();

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
