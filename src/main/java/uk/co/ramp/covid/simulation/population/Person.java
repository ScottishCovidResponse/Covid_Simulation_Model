/*
 * Base code for running Person objects. There are a number of sub classes, but most methods are here
 */

package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.Covid;

public class Person {
    public boolean nursery;
    public boolean school;
    public boolean shopWorker;
    public boolean hospitalWorker;
    public boolean officeWorker;
    public boolean constructionWorker;
    public boolean teacher;
    public boolean restaurant;
    public int mIndex;
    public int hIndex;
    public boolean recovered;
    private boolean allocated;
    private Covid cVirus;
    private double transmissionProb;
    private boolean quarantine;
    private double quarantineProb; // Needs more thought. The probability that the person will go into quarantine
    private double quarantineVal;

    public Person() {
        this.allocated = false;
        this.transmissionProb = 0.45;
        this.mIndex = -1;
        this.quarantineProb = 0.9;
        this.quarantineVal = Math.random();
    }

    public void setAllocation() {
        this.allocated = true;
    }

    public int getMIndex() {
        return this.mIndex;
    }

    public void setMIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public int getHIndex() {
        return this.hIndex;
    }

    public void setHIndex(int hIndex) {
        this.hIndex = hIndex;
    }

    public boolean getQuarantine() {
        return this.quarantine;
    }

    public boolean infect() {
        boolean inf = false;
        if (!this.getInfectionStatus()) {
            this.cVirus = new Covid(this);
            inf = true;
        }

        return inf;
    }

    //Don't mess with this method
    public boolean getInfectionStatus() {
        return !(this.cVirus == null);
    }

    public CStatus stepInfection() {
        CStatus vStatus = this.cVirus.stepInfection();
        return vStatus;
    }

    public void infChallenge(double challengeProb) {
        if (Math.random() < this.transmissionProb / 24 * challengeProb) {
            this.cVirus = new Covid(this);
//		System.out.println("HERE");
        }
    }

    // This method is pretty important, it returns the Covid infection status
    public CStatus cStatus() {
        CStatus cStatus = CStatus.HEALTHY;
        if (!this.getInfectionStatus()) cStatus = CStatus.HEALTHY;
        if (this.getInfectionStatus()) {
            if (this.cVirus.latent) cStatus = CStatus.LATENT;
            if (this.cVirus.asymptomatic) cStatus = CStatus.ASYMPTOMATIC;
            if (this.cVirus.phase1) {
                cStatus = CStatus.PHASE1;
                this.quarantine = this.quarantineProb > this.quarantineVal;
            }
            if (this.cVirus.phase2) {
                cStatus = CStatus.PHASE2;
                this.quarantine = true;
            }
            if (this.cVirus.dead) cStatus = CStatus.DEAD;
            if (this.cVirus.recovered && !this.cVirus.dead) {
                cStatus = CStatus.RECOVERED;
                this.quarantine = false;
            }
        }
        return cStatus;
    }

}
