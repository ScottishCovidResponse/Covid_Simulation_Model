package development_v1;

import java.util.Vector;

public class Restaurant extends CommunalPlace {
	public Restaurant(int cindex) {
		super(cindex);
		this.transProb = super.transProb * 5 / (5000 / 1000);
		this.startDay = 1;
		this.endDay = 7;
		this.startTime = 10;
		this.endTime = 22;
		this.keyPremises = false;
	}
	public void shoppingTrip(Vector vHouse) {
		for(int i = 0; i < vHouse.size(); i++) {
			this.vPeople.addElement((Person) vHouse.elementAt(i));
		}
	}
	
	public Vector sendHome(int hour) {
		Vector vReturn = new Vector();
		for(int i = 0; i < this.vPeople.size(); i++) {
			Person nPers = (Person) this.vPeople.elementAt(i);
					if(!nPers.shopWorker && Math.random() < 0.4 || hour < super.endTime) {// Assumes a median lenght of shopping trip of 2 hours					
					vReturn.addElement(nPers);
					this.vPeople.removeElementAt(i);
					i--;
					}
		}
		return vReturn;
	}

}
