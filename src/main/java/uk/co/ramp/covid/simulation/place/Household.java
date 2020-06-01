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
    
    private boolean willIsolate = false;
    private int isolationTimer = 0;

    // Create household defined by who lives there
    public Household(HouseholdType hType, Places places) {
        this.hType = hType;
        this.neighbours = new ArrayList<>();
        this.places = places;
        if (RNG.get().nextUniform(0,1) < PopulationParameters.get().getpHouseholdWillIsolate()) {
            willIsolate = true;
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
    
    public void isolate() {
        if (willIsolate) {
            isolationTimer = PopulationParameters.get().getHouseholdIsolationPeriod();
        }
    }
    
    public boolean isIsolating() {
        return isolationTimer > 0;
    }

    public boolean seedInfection() {
        List<Person> inhabitants = getInhabitants();
        Person cPers = inhabitants.get(RNG.get().nextInt(0, inhabitants.size() - 1));
        return cPers.infect();
    }

    public int sendNeighboursHome(int day, int hour) {
        ArrayList<Person> left = new ArrayList<>();

        for (Person p : getVisitors()) {
            // People may have already left if their family has
            if (left.contains(p)) {
                continue;
            }

            // Under certain conditions we must go home, e.g. if there is a shift starting soon
            if (p.mustGoHome(day, hour)) {
                left.add(p);
                p.returnHome();
                left.addAll(sendFamilyHome(p, null, day, hour));
            }
            else if (RNG.get().nextUniform(0, 1) < PopulationParameters.get().getHouseholdVisitorLeaveRate()) {
                left.add(p);
                left.addAll(sendFamilyHome(p, null, day, hour));
                if (p.cStatus() != CStatus.DEAD) {
                   p.returnHome();
                }
            }
        }

        people.removeAll(left);
        return left.size();
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
    public void reportInfection(int day, int hour, Person p, DailyStats s) {
        if (getInhabitants().contains(p)) {
            s.incInfectionsHomeInhabitant();
        } else {
            s.incInfectionsHomeVisitor();
        }
    }

    @Override
    public void doMovement(int day, int hour, boolean lockdown) {
        if (!isIsolating()) {
            // Ordering here implies work takes highest priority, then shopping trips have higher priority
            // than neighbour and restaurant trips
            moveShift(day, hour, lockdown);

            // Shops are only open 8-22
            if (hour + 1 >= 8 && hour + 1 < 22) {
                moveShop(day, hour, lockdown);
            }

            moveNeighbour(day, hour);

            // Restaurants are only open 8-22
            if (!lockdown && hour + 1 >= 8 && hour + 1 < 22) {
                moveRestaurant(day, hour);
            }
        }

        // We always send neighbours home outside the isIsolating condition to ensure
        // they aren't stuck when we start isolating
        sendNeighboursHome(day, hour);
    }

    private void moveNeighbour(int day, int hour) {
        List<Person> left = new ArrayList();

        for (Household n : getNeighbours()) {
            if (n.isIsolating()) {
                continue;
            }

            if (RNG.get().nextUniform(0, 1) < PopulationParameters.get().getNeighbourVisitFreq()) {
                // We visit neighbours as a family
                for (Person p : getInhabitants()) {
                    if (!p.getQuarantine()) {
                        n.addPersonNext(p);
                        left.add(p);
                    }
                }
                break;
            }
        }
        
        people.removeAll(left);
    }

    private void moveShop(int day, int hour, boolean lockdown) {
        List<Person> left = new ArrayList();

        double visitProb = PopulationParameters.get().getpGoShopping();
        if (lockdown) {
            visitProb = visitProb * 0.5;
        }

        if (RNG.get().nextUniform(0, 1) < visitProb) {
            Shop s = places.getRandomShop();
            if (s == null) {
                return;
            }

            // Sometimes we can get a case where there are only small shops (open 9-5) so we fail to find a shop
            // In this case we time out the search.
            int retries = 5;
            while (!s.isVisitorOpenNextHour(day, hour)) {
                s = places.getRandomShop();
                retries--;
                if (retries == 0) {
                    return;
                }
            }

            // Go to restaurants as a family
            for (Person p : getInhabitants()) {
                if (!p.getQuarantine()) {
                    s.addPersonNext(p);
                    left.add(p);
                }
            }
        }
        people.removeAll(left);
    }

    private void moveRestaurant(int day, int hour) {
        List<Person> left = new ArrayList();

        double visitProb = PopulationParameters.get().getpGoRestaurant();

        if (RNG.get().nextUniform(0, 1) < visitProb) {
            Restaurant r = places.getRandomRestaurant();
            if (r == null) {
                return;
            }

            int retries = 5;
            while (!r.isVisitorOpenNextHour(day, hour)) {
                r = places.getRandomRestaurant();
                retries--;
                if (retries == 0) {
                    return;
                }
            }

            // Go to restaurants as a family
            for (Person p : getInhabitants()) {
                if (!p.getQuarantine()) {
                    r.addPersonNext(p);
                    left.add(p);
                }
            }
        }
        people.removeAll(left);
    }

    private void moveShift(int day, int hour, boolean lockdown) {
        List<Person> left = new ArrayList();
        for (Person p : getInhabitants()) {
            if (p.worksNextHour(p.getPrimaryCommunalPlace(), day, hour, lockdown)) {
                if (!p.getQuarantine()) {
                    p.visitPrimaryPlace();
                    left.add(p);
                }
            }
        }
        people.removeAll(left);
    }
    
    public void dayEnd() {
        if (isIsolating()) {
            isolationTimer--;
        }
    }
}
