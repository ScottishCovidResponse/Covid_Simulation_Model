/*
 * Paul Bessell
 * This initilaises each Household as a Vector of People.
 * It has a method for cycling throuhg the Household to challenge wiht infection (when relevant)
 */


package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.population.CStatus;
import uk.co.ramp.covid.simulation.population.Person;

import java.util.ArrayList;
import java.util.Random;

public class Household {
    int nType;
    private String type;
    private final ArrayList<Person> vPeople;
    private final ArrayList<Person> vDeaths;
    private int[] neighbourList;
    private final ArrayList<Person> vVisitors;

    // Create household defined by who lives there
    public Household(int nType) {
        this.nType = nType;
        this.setType();
        this.vPeople = new ArrayList<>();
        this.vDeaths = new ArrayList<>();
        this.vVisitors = new ArrayList<>();
    }

    // Turn the number to a String to make it easier on the eye
    public void setType() {

        switch (this.nType) {
            case 1:
                this.type = "Adult only";
                break;
            case 2:
                this.type = "Pensioner only";
                break;
            case 3:
                this.type = "Adult & pensioner";
                break;
            case 4:
                this.type = "Adult & children";
                break;
            default:
                this.type = "Invalid Type";
                break;
        }
    }

    public int getNeighbourIndex(int nNeighbour) {
        return this.neighbourList[nNeighbour];
    }

    public int nNeighbours() {
        return this.neighbourList.length;
    }

    public String getType() {
        return this.type;
    }

    public int getnType() {
        return this.nType;
    }

    public void addPerson(Person cPers) {
        this.vPeople.add(cPers);
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
        Person cPers = this.vPeople.get(new Random().nextInt(this.getHouseholdSize()));
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
            if (Math.random() < 0.5) { // Assumes a 50% probability that people will go home each hour
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

}
