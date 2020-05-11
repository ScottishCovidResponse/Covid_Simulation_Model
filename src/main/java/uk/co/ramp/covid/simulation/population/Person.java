/*
 * Base code for running Person objects. There are a number of sub classes, but most methods are here
 */

package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.Covid;

public class Person {
    private boolean nursery;
    private boolean school;
    private boolean shopWorker;
    private boolean hospitalWorker;
    private boolean officeWorker;
    private boolean constructionWorker;
    private boolean teacher;
    private boolean restaurant;
    private int mIndex;
    private int hIndex;
    private boolean recovered;
    private Covid cVirus;
    private final double transmissionProb;
    private boolean quarantine;
    private final double quarantineProb; // Needs more thought. The probability that the person will go into quarantine
    private final double quarantineVal;

    public Person() {
        this.transmissionProb = 0.45;
        this.mIndex = -1;
        this.quarantineProb = 0.9;
        this.quarantineVal = Math.random();
    }

    public boolean isNursery() {
        return nursery;
    }

    public void setNursery(boolean nursery) {
        this.nursery = nursery;
    }

    public boolean isSchool() {
        return school;
    }

    public void setSchool(boolean school) {
        this.school = school;
    }

    public boolean isShopWorker() {
        return shopWorker;
    }

    public void setShopWorker(boolean shopWorker) {
        this.shopWorker = shopWorker;
    }

    public boolean isHospitalWorker() {
        return hospitalWorker;
    }

    public void setHospitalWorker(boolean hospitalWorker) {
        this.hospitalWorker = hospitalWorker;
    }

    public boolean isOfficeWorker() {
        return officeWorker;
    }

    public void setOfficeWorker(boolean officeWorker) {
        this.officeWorker = officeWorker;
    }

    public boolean isConstructionWorker() {
        return constructionWorker;
    }

    public void setConstructionWorker(boolean constructionWorker) {
        this.constructionWorker = constructionWorker;
    }

    public boolean isTeacher() {
        return teacher;
    }

    public void setTeacher(boolean teacher) {
        this.teacher = teacher;
    }

    public boolean isRestaurant() {
        return restaurant;
    }

    public void setRestaurant(boolean restaurant) {
        this.restaurant = restaurant;
    }

    public boolean isRecovered() {
        return recovered;
    }

    public void setRecovered(boolean recovered) {
        this.recovered = recovered;
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
        return this.cVirus.stepInfection();
    }

    public void infChallenge(double challengeProb) {
        if (Math.random() < this.transmissionProb / 24 * challengeProb) {
            this.cVirus = new Covid(this);
        }
    }

    // This method is pretty important, it returns the Covid infection status
    public CStatus cStatus() {
        CStatus cStatus = CStatus.HEALTHY;
        if (this.getInfectionStatus()) {
            if (this.cVirus.isLatent()) cStatus = CStatus.LATENT;
            if (this.cVirus.isAsymptomatic()) cStatus = CStatus.ASYMPTOMATIC;
            if (this.cVirus.isPhase1()) {
                cStatus = CStatus.PHASE1;
                this.quarantine = this.quarantineProb > this.quarantineVal;
            }
            if (this.cVirus.isPhase2()) {
                cStatus = CStatus.PHASE2;
                this.quarantine = true;
            }
            if (this.cVirus.isDead()) cStatus = CStatus.DEAD;
            if (this.cVirus.isRecovered() && !this.cVirus.isDead()) {
                cStatus = CStatus.RECOVERED;
                this.quarantine = false;
            }
        }
        return cStatus;
    }

}
