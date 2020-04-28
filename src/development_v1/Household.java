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
Vector vPeople;
private int[] neighbourList;
	public Household(int nType) {
		this.nType = nType;	
		this.setType();
		this.vPeople = new Vector();
	}
	public void setType() {
		if(this.nType == 1)	this.type = "Adult only";
		if(this.nType == 2)	this.type = "Pensioner only";
		if(this.nType == 3)	this.type = "Adult & pensioner";
		if(this.nType == 4)	this.type = "Adult & children";

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
	
	public Vector cycleHouse() {
		if(this.vPeople.size() > 20) System.out.println("VPeople size = " + this.vPeople.size());
		for(int i = 0; i < this.vPeople.size(); i++) {
			Person cPers = (Person) this.vPeople.elementAt(i);
			if(cPers.getInfectionStatus() & !cPers.recovered) {
				String status = cPers.stepInfection();
				if(status == "Asymptomatic" || status == "Phase 1" || status == "Phase 2") {
					for(int k = 0; k < this.vPeople.size(); k++) {
						if(k!=i) {
					//	System.out.println("House size = " + k);
							Person nPers = (Person) this.vPeople.elementAt(k);
							if(!nPers.getInfectionStatus()) {
								nPers.infChallenge(1);
							}
						}
					}
				}
				if(status == "Dead") {
					this.vPeople.removeElementAt(i);
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
	
}
