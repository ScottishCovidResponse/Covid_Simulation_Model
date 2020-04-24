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

	
}
public void setAllocation() {
	this.allocated = true;
}

public void setIndex(int mIndex) {
	this.mIndex = mIndex;
}

public void setHIndex(int hIndex) {
	this.hIndex = hIndex;
}

public int getIndex() {
	return this.mIndex;
}

public int getHindex() {
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
}
