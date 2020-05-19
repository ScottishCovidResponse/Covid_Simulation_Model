/*
 * Base code for running Person objects. There are a number of sub classes, but most methods are here
 */

package uk.co.ramp.covid.simulation.population;

import org.apache.commons.math3.random.RandomDataGenerator;
import uk.co.ramp.covid.simulation.Covid;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.place.Places;
import uk.co.ramp.covid.simulation.util.RNG;

public abstract class Person {
    private boolean shopWorker = false;
    private CommunalPlace primaryPlace = null;
    private Household home;
    private boolean recovered;
    private Covid cVirus;
    private final double transmissionProb;
    private boolean quarantine;
    private final double quarantineProb; // Needs more thought. The probability that the person will go into quarantine
    private final double quarantineVal;
    protected final RandomDataGenerator rng;
    
    public abstract void reportInfection(DailyStats s);
    public abstract void reportDeath (DailyStats s);
    public abstract void allocateCommunalPlace(Places p);

    public Person() {
        this.rng = RNG.get();
        this.transmissionProb = PopulationParameters.get().getpTransmission();
        this.quarantineProb = PopulationParameters.get().getpQuarantine();
        this.quarantineVal = rng.nextUniform(0, 1);
    }
    
    public void setShopWorker() {
        shopWorker = true;
    }

    public boolean isShopWorker() {
        return shopWorker;
    }

   
    public boolean isRecovered() {
        return recovered;
    }

    public void setRecovered(boolean recovered) {
        this.recovered = recovered;
    }

    public CommunalPlace getPrimaryCommunalPlace() {
        return this.primaryPlace;
    }

    public void setPrimaryPlace(CommunalPlace p) {
        this.primaryPlace = p;
    }

    public Household getHome() {
        return home;
    }

    public void setHome(Household h) {
        home = h;
    }
    
    public void returnHome() {
        home.addPerson(this);
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

    public boolean infChallenge(double challengeProb) {
        if (rng.nextUniform(0, 1) < this.transmissionProb / 24 * challengeProb) {
            this.cVirus = new Covid(this);
            return true;
        }
        return false;
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

    public boolean isInfectious() {
        return cStatus() == CStatus.ASYMPTOMATIC
                || cStatus() == CStatus.PHASE1
                || cStatus() == CStatus.PHASE2;
    }

    public boolean hasPrimaryCommunalPlace() {
        return primaryPlace != null;
    }

    public abstract boolean avoidsPhase2(double testP);
}
