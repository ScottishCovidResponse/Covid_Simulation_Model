/*
 * Code for managing the Communal Places where People objects mix
 */

package development_v1;
import java.util.*;

public class CommunalPlace {

protected int startTime;
protected int endTime;
public int cindex; 
protected Vector vPeople;
protected int startDay;
protected int endDay;
protected double transProb; 

	public CommunalPlace(int cindex) {
		this.vPeople = new Vector();
		this.startTime = 8; // The hour of the day that the Communal Place starts
		this.endTime = 17; // The hour of the day that it ends
		this.startDay = 1; // Days of the week that it is active - start
		this.endDay = 5; // Days of the week that it is active - end
		this.cindex = cindex; // This sets the index for each Communal Place to avoid searching
		this.transProb = 0.5; // Pretty important parameter. This defines the transmission rate within this Communal Place
		
	}
	public void setIndex(int indexVal) {
		this.cindex = indexVal; 
	}
	public int getIndex() {
		return this.cindex;
	}
	
	// Check whether a Person might visit at that hour of the day
	public boolean checkVisit(Person cPers, int time, int day) {
		boolean cIn = false; 
		if(this.startTime == time) {
			cIn = true;
			this.vPeople.addElement(cPers);
		}
		return cIn;
	}

	// Cyctek through the People objects in the Place and test their infection status etc
	public Vector cyclePlace(int time, int day) {
		
		Vector cReturn = new Vector();
		String status = "";
		for(int i = 0; i < this.vPeople.size(); i++) {
			Person cPers = (Person) this.vPeople.elementAt(i);
			if(cPers.getInfectionStatus() & !cPers.recovered) {
				status = cPers.stepInfection();
				if(cPers.cStatus() == "Asymptomatic" || cPers.cStatus() == "Phase 1" || cPers.cStatus() == "Phase 2") {
					for(int k = 0; k < this.vPeople.size(); k++) {
						if(k!=i) {
							Person nPers = (Person) this.vPeople.elementAt(k);
							if(!nPers.getInfectionStatus()) {
								nPers.infChallenge(this.transProb);
							}
						}
					}
				}
				if(status == "Dead") {
					this.vPeople.removeElementAt(i);
				//	System.out.println("Work Dead");  // Printing key metrics of infection to check that the model is working
					i--;
				}
				if(status == "Recovered") {
					cPers.recovered = true;
				//	System.out.println("Recovered");  // Printing key metrics of infection to check that the model is working
				}
			}
			if(time == this.endTime & status != "Dead") {
				cReturn.addElement(cPers);
				this.vPeople.removeElementAt(i);
				i--;
			}
		}
		return cReturn;
	}

}
