package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.CStatus;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.*;

public class Household extends Place {

    public enum HouseholdType {
       ADULT               { public String toString() {return "Adult only";                   } },
       PENSIONER           { public String toString() {return "Pensioner only";               } },
       ADULTPENSIONER      { public String toString() {return "Adult & pensioner";            } },
       ADULTCHILD          { public String toString() {return "Adult & children";             } },
       PENSIONERCHILD      { public String toString() {return "Pensioner & children";         } },
       ADULTPENSIONERCHILD { public String toString() {return "Adult & pensioner & children"; } }
    }

    public static final Set<HouseholdType> adultHouseholds = new HashSet<>(Arrays.asList(
            HouseholdType.ADULT, HouseholdType.ADULTPENSIONERCHILD,
            HouseholdType.ADULTPENSIONER, HouseholdType.ADULTCHILD));

    public static final Set<HouseholdType> pensionerHouseholds = new HashSet<>(Arrays.asList(
            HouseholdType.PENSIONER, HouseholdType.ADULTPENSIONERCHILD,
            HouseholdType.PENSIONERCHILD, HouseholdType.ADULTPENSIONER));

    public static final Set<HouseholdType> childHouseholds = new HashSet<>(Arrays.asList(
            HouseholdType.ADULTCHILD, HouseholdType.ADULTPENSIONERCHILD, HouseholdType.PENSIONERCHILD));

    private final HouseholdType hType;
    private final List<Household> neighbours;
    private final Places places;
    private int householdSize = 0;

    // Create household defined by who lives there
    public Household(HouseholdType hType, Places places) {
        this.hType = hType;
        this.neighbours = new ArrayList<>();
        this.places = places;
    }

    public List<Household> getNeighbours() {
        return neighbours;
    }

    public int nNeighbours() {
        return neighbours.size();
    }

    public HouseholdType gethType() {
        return this.hType;
    }

    public void addInhabitant(Person cPers) {
        cPers.setHome(this);
        householdSize++;
        this.people.add(cPers);
    }

    public int getHouseholdSize() {
        return householdSize;
    }

    public void addNeighbour(Household n) {
        neighbours.add(n);
    }

    public boolean isNeighbour(Household n) { return neighbours.contains(n); }

    public boolean seedInfection() {
        List<Person> inhabitants = getInhabitants();
        Person cPers = inhabitants.get(RNG.get().nextInt(0, inhabitants.size() - 1));
        return cPers.infect();
    }

    // Go through the household at each time step and see what they get up to
    public List<Person> cycleHouse(DailyStats stats) {
        doInfect(stats);
        return people;
    }

    private ArrayList<Person> neighbourVisit() {
        ArrayList<Person> visitPeople = new ArrayList<>();

        for (Person p : getInhabitants()) {
            if (!p.getQuarantine()) {
                visitPeople.add(p);
            }
        }

        if (visitPeople.size() == 0) {
            return null;
        }

        people.removeAll(visitPeople);

        return visitPeople;
    }

    // Neighbours go into a List of their own - we don't copy the list whole because the way multiple neighbour visits can happen concurrently
    public void welcomeNeighbours(Household visitHouse) {
        ArrayList<Person> visitVector = visitHouse.neighbourVisit();
        if (visitVector != null) {
            this.people.addAll(visitVector);
        }
    }

    public int sendNeighboursHome() {
        ArrayList<Person> left = new ArrayList<>();

        for (Person p : getVisitors()) {
            if (RNG.get().nextUniform(0, 1) < PopulationParameters.get().getHouseholdVisitorLeaveRate()) {
                left.add(p);
                if (p.cStatus() != CStatus.DEAD) {
                   p.returnHome();
                }
            }
        }

        people.removeAll(left);
        return left.size();
    }

    // Get a vector of people to go to the shops.
    public ArrayList<Person> shoppingTrip() {
        ArrayList<Person> shopping = new ArrayList<>();

        for (Person p : getInhabitants()) {
            if (!p.getQuarantine()) {
                shopping.add(p);
            }
        }
        people.removeAll(shopping);

        return shopping;
    }
    
    private boolean isVisitor(Person p) {
        return p.getHome() != this;
    }

    public List<Person> getVisitors() {
        List<Person> ret = new ArrayList<>();
        for (Person p : people) {
            if (isVisitor(p)) {
                ret.add(p);
            }
        }
        return ret;
    }

    public List<Person> getInhabitants() {
        List<Person> ret = new ArrayList<>();
        for (Person p : people) {
            if (!isVisitor(p)) {
                ret.add(p);
            }
        }
        return ret;
    }
    
    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsHome();
    }

    // For each household processes any movements to Communal Places that are relevant
    public void cycleMovements(int day, int hour, boolean lockdown) {
        List<Person> left = new ArrayList<>();
        for (Person p : getInhabitants()) {
            if (p.hasPrimaryCommunalPlace() && !p.getQuarantine()) {
                boolean visit = p.getPrimaryCommunalPlace().checkVisit(p, hour, day, lockdown);
                if (visit) {
                    left.add(p);
                }
            }
        }
        people.removeAll(left);
    }

    @Override
    public void doMovement(int day, int hour) {
       moveShift(day, hour);

       // Implies shopping trips have higher priority than neighbour and restaurant trips
       moveShop(day, hour);

       moveNeighbour(day, hour);
       moveRestaurant(day, hour);
    }

    private void moveNeighbour(int day, int hour) {
        List<Person> left = new ArrayList();

        for (Household n : getNeighbours()) {
            if (RNG.get().nextUniform(0, 1) < PopulationParameters.get().getNeighbourVisitFreq()) {
                // We visit neighbours as a family
                for (Person p : getInhabitants()) {
                    if (!p.getQuarantine()) {
                        n.addPerson(p);
                    }
                }
            }
        }
        
        people.removeAll(left);
    }

    private void moveShop(int day, int hour) {
        List<Person> left = new ArrayList();

        //TODO: Make these parameters
        double visitProb = 3.0 / 7.0 / 24.0; // Based on three visits per week to shops
        //TODO: Handle lockdown probabilities

        if (RNG.get().nextUniform(0, 1) < visitProb) {
            Shop s = places.getRandomShop();
            // We go shopping as a family
            if (s.isVisitorOpenNextHour(day, hour)) {
                for (Person p : getInhabitants()) {
                    if (!p.getQuarantine()) {
                        s.addPerson(p);
                    }
                }
            }
            // TODO: Should we keep trying shops till we find one that's open, or just give up if not.
            // Particularly important for overnight (where there could be some 24h shops open)
        }
        people.removeAll(left);
    }

    private void moveRestaurant(int day, int hour) {
        List<Person> left = new ArrayList();

        // TODO: Make this a parameter
        double visitProb = 2.0 / 7.0 / 24.0;
        //TODO: Handle lockdown probabilities

        if (RNG.get().nextUniform(0, 1) < visitProb) {
            Restaurant r = places.getRandomRestaurant();
            // We go to restaurants as a family
            if (r.isVisitorOpenNextHour(day, hour)) {
                for (Person p : getInhabitants()) {
                    if (!p.getQuarantine()) {
                        r.addPerson(p);
                    }
                }
            }
            // TODO: Should we keep trying restaurants till we find one that's open, or just give up if not.
            // Unlike shops, there probably isn't one open 24 hours
        }
        people.removeAll(left);
    }

    private void moveShift(int day, int hour) {
        List<Person> left = new ArrayList();
        for (Person p : getInhabitants()) {
            if (p.worksNextHour(p.getPrimaryCommunalPlace(), day, hour)) {
                if (!p.getQuarantine()) {
                    p.visitPrimaryPlace();
                }
                left.add(p);
            }
        }
        people.removeAll(left);
    }
}
