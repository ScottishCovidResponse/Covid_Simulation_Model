/*
 * Paul Bessell
 * This initilaises each Household as a Vector of People.
 * It has a method for cycling throuhg the Household to challenge wiht infection (when relevant)
 */


package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import org.apache.commons.math3.random.RandomDataGenerator;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.population.CStatus;
import uk.co.ramp.covid.simulation.population.Person;
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
    private final ArrayList<Person> vPeople;
    private int[] neighbourList;
    private final ArrayList<Person> vVisitors;
    private final RandomDataGenerator rng;

    // Create household defined by who lives there
    public Household(HouseholdType hType) {
        this.hType = hType;
        vPeople = new ArrayList<>();
        vVisitors = new ArrayList<>();
        this.rng = RNG.get();
    }

    public int getNeighbourIndex(int nNeighbour) {
        return this.neighbourList[nNeighbour];
    }

    public int nNeighbours() {
        return this.neighbourList.length;
    }

    public HouseholdType gethType() {
        return this.hType;
    }

    public void addPerson(Person cPers) {
        this.vPeople.add(cPers);
        this.people.add(cPers);
    }

    public int getHouseholdSize() {
        return this.vPeople.size();
    }

    public Person getPerson(int pos) {
        return this.vPeople.get(pos);
    }

    public void setNeighbourList(int[] neighbours) {
        this.neighbourList = neighbours;
    }

    public boolean seedInfection() {
        Person cPers = this.vPeople.get(rng.nextInt(0, this.getHouseholdSize() - 1));
        return cPers.infect();
    }

    // Go through the household at each time step and see what they get up to
    public ArrayList<Person> cycleHouse(DailyStats stats) {
        doInfect(stats);
        // Fix the 2 sub-sets of people
        vPeople.retainAll(people);
        vVisitors.retainAll(people);
        return vPeople;
    }

    // When neighbours visit, stick everybody in a single vector ans see wat they get up to
    private ArrayList<Person> neighbourVisit() {
        ArrayList<Person> visitPeople = new ArrayList<>();

        for (Person p : vPeople) {
            if (!p.getQuarantine()) {
                visitPeople.add(p);
            }
        }

        if (visitPeople.size() == 0) {
            return null;
        }

        vPeople.removeAll(visitPeople);
        people.removeAll(visitPeople);

        return visitPeople;
    }

    // Neighbours go into a List of their own - we don't copy the list whole because the way multiple neighbour visits can happen concurrently
    public void welcomeNeighbours(Household visitHouse) {
        ArrayList<Person> visitVector = visitHouse.neighbourVisit();
        if (visitVector != null) {
            this.vVisitors.addAll(visitVector);
            this.people.addAll(visitVector);
        }
    }

    public int sendNeighboursHome() {
        ArrayList<Person> left = new ArrayList<>();

        for (Person p : vVisitors) {
            if (rng.nextUniform(0, 1) < PopulationParameters.get().getHouseholdVisitorLeaveRate()) {
                left.add(p);
                if (p.cStatus() != CStatus.DEAD) {
                   p.returnHome();
                }
            }
        }

        vVisitors.removeAll(left);
        people.removeAll(left);

        return left.size();
    }

    // Get a vector of people to go to the shops.
    public ArrayList<Person> shoppingTrip() {
        ArrayList<Person> shopping = new ArrayList<>();

        for (Person p : vPeople) {
            if (!p.getQuarantine()) {
                shopping.add(p);
            }
        }
        people.removeAll(shopping);
        vPeople.removeAll(shopping);

        return shopping;
    }

    public List<Person> getInhabitants() {
        return vPeople;
    }

    public List<Person> getVisitors() {
        return vVisitors;
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsHome();
    }

    // For each household processes any movements to Communal Places that are relevant
    public void cycleMovements(int day, int hour, boolean lockdown) {
        List<Person> left = new ArrayList<>();
        int i = 0;
        for (Person p : vPeople) {
            if (p.hasPrimaryCommunalPlace() && !p.getQuarantine()) {
                boolean visit = p.getPrimaryCommunalPlace().checkVisit(p, hour, day, lockdown);
                if (visit) {
                    left.add(p);
                }
            }
        }
        people.removeAll(left);
        vPeople.removeAll(left);
    }
}
