/*
 * Base code for running Person objects. There are a number of sub classes, but most methods are here 
 */

package development_v1;

public class Person {
private boolean allocated;
public boolean nursery;
public boolean school;
public boolean shopWorker;
public boolean hospitalWorker;
public boolean officeWorker;
public boolean constructionWorker;
public boolean teacher;
public int mIndex;
public int hIndex;
private Covid cVirus;
private double transmissionProb;
public boolean recovered;

public Person() {
	this.allocated = false;
	this.transmissionProb = 0.5;
	this.mIndex = -1;
	
}
public void setAllocation() {
	this.allocated = true;
}

public void setMIndex(int mIndex) {
	this.mIndex = mIndex;
}

public void setHIndex(int hIndex) {
	this.hIndex = hIndex;
}

public int getMIndex() {
	return this.mIndex;
}

public int getHIndex() {
	return this.hIndex;
}

public boolean infect() {
	boolean inf = false;
	if(!this.getInfectionStatus()) {
		this.cVirus = new Covid(this);
		 inf = true;
	}

	return inf;
}

//Don't mess with this method
public boolean getInfectionStatus() {
	return !(this.cVirus == null);
}

public String stepInfection() {
	String vStatus = this.cVirus.stepInfection();
	return vStatus;
}

public void infChallenge(double challengeProb) {
	if(Math.random() < this.transmissionProb / 24 * challengeProb) {
		this.cVirus = new Covid(this);
//		System.out.println("HERE");
	}
}

public String cStatus() {
	String cStatus = "Healthy";
	if(!this.getInfectionStatus()) cStatus = "Healthy";
	if(this.getInfectionStatus()) {
		if(this.cVirus.latent) cStatus = "Latent";
		if(this.cVirus.asymptomatic) cStatus = "Asymptomatic";
		if(this.cVirus.phase1) cStatus = "Phase 1";
		if(this.cVirus.phase2) cStatus = "Phase 2";
		if(this.cVirus.dead) cStatus = "Dead";
		if(this.cVirus.recovered && !this.cVirus.dead) cStatus = "Recovered";
	}
	return cStatus;			
}

}
