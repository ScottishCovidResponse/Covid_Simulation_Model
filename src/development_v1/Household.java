/*
 * Paul Bessell
 * This initilaises each Household as a Vector of People.
 * It has a method for cycling throuhg the Household to challenge wiht infection (when relevant)
 */


package development_v1;

import java.util.*;
public class Household {
private String type;
int nType;
private Vector vPeople;
private Vector vDeaths;
private int[] neighbourList;
private Vector vVisitors;

	public Household(int nType) {
		this.nType = nType;	
		this.setType();
		this.vPeople = new Vector();
		this.vDeaths = new Vector();
		this.vVisitors = new Vector();
	}
	public void setType() {
		if(this.nType == 1)	this.type = "Adult only";
		if(this.nType == 2)	this.type = "Pensioner only";
		if(this.nType == 3)	this.type = "Adult & pensioner";
		if(this.nType == 4)	this.type = "Adult & children";

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
		this.vPeople.addElement(cPers);
	}
	public int getHouseholdSize() {
	//	if(vPeople.size() == 0) System.out.println(this.type);
		return this.vPeople.size();
		
	}
	public Person getPerson(int pos) {
		return (Person) this.vPeople.elementAt(pos);
	}
	
	public void setNeighbourList(int[] neighbours) {
		this.neighbourList = neighbours;
	}
	
	public boolean seedInfection() {
		Person cPers = (Person) this.vPeople.elementAt(new Random().nextInt(this.getHouseholdSize()));
		return cPers.infect();
	}
	
// Combine the household and neighbours vectors for Covid transmission
	private Vector combVectors() {
		Vector cVector = new Vector();
		for(int i = 0; i < this.vPeople.size(); i ++) cVector.addElement((Person) this.vPeople.elementAt(i));
		for(int i = 0; i < this.vVisitors.size(); i ++) cVector.addElement((Person) this.vVisitors.elementAt(i));
		
		return cVector;
	}
	
	public Vector cycleHouse() {
	//	if(this.vPeople.size() > 20) System.out.println("VPeople size = " + this.vPeople.size());
		Vector hVector = this.combVectors();
		for(int i = 0; i < hVector.size(); i++) {
			Person cPers = (Person) hVector.elementAt(i);
			if(cPers.getInfectionStatus() && !cPers.recovered) {
				String status = cPers.stepInfection();
				if(cPers.cStatus() == "Asymptomatic" || cPers.cStatus() == "Phase 1" || cPers.cStatus() == "Phase 2") {
				//System.out.println(status + "   " + cPers.cStatus());
					for(int k = 0; k < hVector.size(); k++) {
						if(k!=i) {
					//	System.out.println("House size = " + k);
							Person nPers = (Person) hVector.elementAt(k);
							if(!nPers.getInfectionStatus()) {
								nPers.infChallenge(1);
							//	if(this.vVisitors.size() > 0) System.out.println(this.toString() +  " H index ="+ nPers.getHIndex());
							}
						}
					}
				}
				if(status == "Dead") {
					hVector.removeElementAt(i);
					this.vDeaths.addElement(cPers);
				//	System.out.println("House Dead");
					i--;
				}
				if(status == "Recovered") {
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
	
	private Vector neighbourVisit() {
		Vector visitPeople = new Vector();
		
		for(int i = 0; i < this.vPeople.size(); i++) {
			Person cPers = (Person) this.vPeople.elementAt(i);
			if(!cPers.getQuarantine()) {
				visitPeople.addElement(cPers);
				this.vPeople.removeElementAt(i);
				i--;
			}			
		}
		if(visitPeople.size() == 0) visitPeople = null;
		return visitPeople;
	}
	
	public void welcomeNeighbours(Household visitHouse) {
		Vector visitVector = visitHouse.neighbourVisit();
		if(visitVector != null) {
		for(int i = 0; i < visitVector.size(); i++) {
		//	System.out.println("Vector size = "+ visitVector.size() + " i = "+ i);
			this.vVisitors.addElement((Person) visitVector.elementAt(i));
		}
		}
	}
	
	public Vector sendNeighboursHome() {
		Vector vGoHome = new Vector();
		
		for(int i = 0; i < this.vVisitors.size(); i++) {
			if(Math.random() < 0.5) { // Assumes a 50% probability that people will go home each hour
				Person nPers = (Person) this.vVisitors.elementAt(i);
				vGoHome.addElement(nPers);
				this.vVisitors.removeElementAt(i);
			}
		} 		
		return vGoHome;
	}
	
}
