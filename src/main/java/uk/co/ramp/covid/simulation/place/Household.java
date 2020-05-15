/*
 * Paul Bessell
 * This initilaises each Household as a Vector of People.
 * It has a method for cycling throuhg the Household to challenge wiht infection (when relevant)
 */


package uk.co.ramp.covid.simulation.place;

import org.apache.commons.math3.random.RandomDataGenerator;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.population.CStatus;
import uk.co.ramp.covid.simulation.population.Person;

import java.util.*;

public class Household {
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
    private final ArrayList<Person> vDeaths;
    private int[] neighbourList;
    private final ArrayList<Person> vVisitors;
    private final RandomDataGenerator rng;

    // Create household defined by who lives there
    public Household(HouseholdType hType) {
        this.hType = hType;
        vPeople = new ArrayList<>();
        vDeaths = new ArrayList<>();
        vVisitors = new ArrayList<>();
        this.rng = RunModel.getRng();
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
    }

    public void addDeath(Person cPers) { vDeaths.add(cPers); }

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

    // Combine the household and neighbours Lists for Covid transmission
    public ArrayList<Person> combVectors() {
        var cList1 = new ArrayList<>(this.vPeople);
        var cList2 = new ArrayList<>(this.vVisitors);
        cList1.addAll(cList2);
        return cList1;
    }

    // Go through the household at each time step and see what they get up to
    public ArrayList<Person> cycleHouse() {
        ArrayList<Person> hVector = this.combVectors();
        for (int i = 0; i < hVector.size(); i++) {
            Person cPers = hVector.get(i);
            if (cPers.getInfectionStatus() && !cPers.isRecovered()) {
                cPers.stepInfection();
                if (cPers.cStatus() == CStatus.ASYMPTOMATIC || cPers.cStatus() == CStatus.PHASE1 || cPers.cStatus() == CStatus.PHASE2) {
                    for (int k = 0; k < hVector.size(); k++) {
                        if (k != i) {
                            Person nPers = hVector.get(k);
                            if (!nPers.getInfectionStatus()) {
                                nPers.infChallenge(1);
                            }
                        }
                    }
                }
                if (cPers.cStatus() == CStatus.DEAD) {
                    hVector.remove(i);
                    this.vDeaths.add(cPers);
                    this.vPeople.remove(cPers);
                    i--;
                }
                if (cPers.cStatus() == CStatus.RECOVERED) {
                    cPers.setRecovered(true);
                }
            }
        }
        return this.vPeople;
    }

    public int getDeaths() {
        return this.vDeaths.size();
    }

    // When neighbours visit, stick everybody in a single vector ans see wat they get up to
    private ArrayList<Person> neighbourVisit() {
        ArrayList<Person> visitPeople = new ArrayList<>();

        for (int i = 0; i < this.vPeople.size(); i++) {
            Person cPers = this.vPeople.get(i);
            if (!cPers.getQuarantine()) {
                visitPeople.add(cPers);
                this.vPeople.remove(i);
                i--;
            }
        }
        if (visitPeople.size() == 0) visitPeople = null;
        return visitPeople;
    }

    // Neighbours go into a List of their own - we don't copy the list whole because the way multiple neighbour visits can happen concurrently
    public void welcomeNeighbours(Household visitHouse) {
        ArrayList<Person> visitVector = visitHouse.neighbourVisit();
        if (visitVector != null) {
            this.vVisitors.addAll(visitVector);
        }
    }

    public ArrayList<Person> sendNeighboursHome() {
        ArrayList<Person> vGoHome = new ArrayList<>();

        for (int i = 0; i < this.vVisitors.size(); i++) {
            if (rng.nextUniform(0, 1) < 0.5) { // Assumes a 50% probability that people will go home each hour
                Person nPers = this.vVisitors.get(i);
                if (nPers.cStatus() == CStatus.DEAD) {
                    this.vVisitors.remove(i);
                    i--;
                } else {
                    vGoHome.add(nPers);
                    this.vVisitors.remove(i);
                    i--;
                }
            }
        }
        return vGoHome;
    }

    // Get a vector of people to go to the shops.
    public ArrayList<Person> shoppingTrip() {
        ArrayList<Person> vShop = new ArrayList<>();

        for (int i = 0; i < this.vPeople.size(); i++) {
            Person cPers = this.vPeople.get(i);
            if (!cPers.getQuarantine()) {
                vShop.add(cPers);
                this.vPeople.remove(i);
                i--;
            }
        }
        return vShop;
    }

    public List<Person> getInhabitants() {
        return vPeople;
    }

    public List<Person> getVisitors() { return vVisitors; }
}
