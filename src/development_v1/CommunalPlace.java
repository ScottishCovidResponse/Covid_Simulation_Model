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
		this.startTime = 8;
		this.endTime = 17;
		this.startDay = 1;
		this.endDay = 5;
		this.cindex = cindex;
		this.transProb = 0.5;
		
	}
	public void setIndex(int indexVal) {
		this.cindex = indexVal; 
	}
	public int getIndex() {
		return this.cindex;
	}
	public boolean checkVisit(Person cPers, int time, int day) {
		boolean cIn = false; 
		if(this.startTime == time) {
			cIn = true;
			this.vPeople.addElement(cPers);
		}
		return cIn;
	}
	
	public Vector returnPeople(int time, int day) {
		Vector cReturn = new Vector();
		
		
		return cReturn;
	}
	public Vector cyclePlace(int time, int day) {
		
		Vector cReturn = new Vector();
		String status = "";
		for(int i = 0; i < this.vPeople.size(); i++) {
			Person cPers = (Person) this.vPeople.elementAt(i);
			if(cPers.getInfectionStatus() & !cPers.recovered) {
				status = cPers.stepInfection();
				if(status == "Asymptomatic" || status == "Phase 1" || status == "Phase 2") {
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
					System.out.println("Work Dead");
					i--;
				}
				if(status == "Recovered") {
					cPers.recovered = true;
					System.out.println("Recovered");
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
