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
import java.util.Vector;

public class Household {
    int nType;
    private String type;
    private ArrayList<Person> vPeople;
    private ArrayList<Person> vDeaths;
    private int[] neighbourList;
    private ArrayList<Person> vVisitors;

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
        if (this.nType == 1) this.type = "Adult only";
        if (this.nType == 2) this.type = "Pensioner only";
        if (this.nType == 3) this.type = "Adult & pensioner";
        if (this.nType == 4) this.type = "Adult & children";

    }

    public int getNeighbourIndex(int nNeighbour) {
        return this.neighbourList[nNeighbour];
    }

    public int nNieghbours() {
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
        //	if(vPeople.size() == 0) System.out.println(this.type);
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

    // Combine the household and neighbours vectors for Covid transmission
    public Vector combVectors() {
        Vector cVector = new Vector();
        for (int i = 0; i < this.vPeople.size(); i++) cVector.addElement(this.vPeople.get(i));
        for (int i = 0; i < this.vVisitors.size(); i++) cVector.addElement(this.vVisitors.get(i));

        return cVector;
    }

    // Go through the household at each time step and see what they get up to
    public ArrayList<Person> cycleHouse() {
        //	if(this.vPeople.size() > 20) System.out.println("VPeople size = " + this.vPeople.size());
        Vector hVector = this.combVectors();
        for (int i = 0; i < hVector.size(); i++) {
            Person cPers = (Person) hVector.elementAt(i);
            if (cPers.getInfectionStatus() && !cPers.recovered) {
                CStatus status = cPers.stepInfection();
                if (cPers.cStatus() == CStatus.ASYMPTOMATIC || cPers.cStatus() == CStatus.PHASE1 || cPers.cStatus() == CStatus.PHASE2) {
                    //System.out.println(status + "   " + cPers.cStatus());
                    for (int k = 0; k < hVector.size(); k++) {
                        if (k != i) {
                            //	System.out.println("House size = " + k);
                            Person nPers = (Person) hVector.elementAt(k);
                            if (!nPers.getInfectionStatus()) {
                                nPers.infChallenge(1);
                                //	if(this.vVisitors.size() > 0) System.out.println(this.toString() +  " H index ="+ nPers.getHIndex());
                            }
                        }
                    }
                }
                if (cPers.cStatus() == CStatus.DEAD) {
                    hVector.removeElementAt(i);
                    this.vDeaths.add(cPers);
                    this.vPeople.remove(cPers);
                    //	System.out.println("House Dead");
                    i--;
                }
                if (cPers.cStatus() == CStatus.RECOVERED) {
                    cPers.recovered = true;
                    //	System.out.println("House Recovered");
                }
            }
        }
        return this.vPeople;
    }

    public int getDeaths() {
        return this.vDeaths.size();
    }

    // When neighbours visit, stick everybody in a single vector ans see wat they get up to
    private Vector neighbourVisit() {
        Vector visitPeople = new Vector();

        for (int i = 0; i < this.vPeople.size(); i++) {
            Person cPers = this.vPeople.get(i);
            if (!cPers.getQuarantine()) {
                visitPeople.addElement(cPers);
                this.vPeople.remove(i);
                i--;
            }
        }
        if (visitPeople.size() == 0) visitPeople = null;
        return visitPeople;
    }

    // Neighbours go into a Vector of their own - we don't copy the vector whole because thie way multiple neighbour visits can happen concurrently
    public void welcomeNeighbours(Household visitHouse) {
        Vector visitVector = visitHouse.neighbourVisit();
        if (visitVector != null) {
            for (int i = 0; i < visitVector.size(); i++) {
                //	System.out.println("Vector size = "+ visitVector.size() + " i = "+ i);
                this.vVisitors.add((Person) visitVector.elementAt(i));
            }
        }
    }

    public Vector sendNeighboursHome() {
        Vector vGoHome = new Vector();

        for (int i = 0; i < this.vVisitors.size(); i++) {
            if (Math.random() < 0.5) { // Assumes a 50% probability that people will go home each hour
                Person nPers = (Person) this.vVisitors.get(i);
                if (nPers.cStatus() == CStatus.DEAD) {
                    this.vVisitors.remove(i);
                    i--;
                } else {
                    vGoHome.addElement(nPers);
                    this.vVisitors.remove(i);
                    i--;
                }
            }
        }
        return vGoHome;
    }

    // Get a vector of people to go to the shops.
    public Vector shoppingTrip() {
        Vector vShop = new Vector();

        for (int i = 0; i < this.vPeople.size(); i++) {
            Person cPers = this.vPeople.get(i);
            if (!cPers.getQuarantine()) {
                vShop.addElement(cPers);
                this.vPeople.remove(i);
                i--;
            }
        }
        return vShop;
    }

}
