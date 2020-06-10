/*
 * Base code for running Person objects. There are a number of sub classes, but most methods are here
 */

package uk.co.ramp.covid.simulation.population;

import org.apache.commons.math3.random.RandomDataGenerator;
import uk.co.ramp.covid.simulation.covid.Covid;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.Optional;

public abstract class Person {

    public enum Sex {
        MALE, FEMALE
    }
    
    private CommunalPlace primaryPlace = null;
    protected Shifts shifts = null;
    
    private Sex sex;
    private int age;

    private Household home;
    private boolean recovered;
    private Covid cVirus;
    private final double transmissionProb;
    private boolean quarantine;
    private final Probability quarantineProb; // Needs more thought. The probability that the person will go into quarantine
    private final double quarantineVal;
    private Optional<Boolean> testOutcome = Optional.empty();
    protected final RandomDataGenerator rng;
    
    public abstract void reportInfection(DailyStats s);
    public abstract void reportDeath (DailyStats s);
    public abstract void allocateCommunalPlace(Places p);


    public Person(int age, Sex sex) {
        this.age = age;
        this.sex = sex;
        this.rng = RNG.get();
        this.transmissionProb = PopulationParameters.get().personProperties.pTransmission.asDouble();
        this.quarantineProb = PopulationParameters.get().personProperties.pQuarantinesIfSymptomatic;
        this.quarantineVal = rng.nextUniform(0, 1);
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
        home.addPersonNext(this);
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

    public boolean isinfected() {
        return cVirus != null && !recovered;
    }

    //Don't mess with this method
    public boolean getInfectionStatus() {
        return !(this.cVirus == null);
    }

    public CStatus stepInfection(Time t) {
        return this.cVirus.stepInfection(t);
    }

    public boolean infChallenge(double challengeProb) {
        if (rng.nextUniform(0, 1) < this.transmissionProb / 24 * challengeProb) {
            this.cVirus = new Covid(this);
            return true;
        }
        return false;
    }

    public Covid getcVirus() {
        return cVirus;
    }

    // This method is pretty important, it returns the Covid infection status
    public CStatus cStatus() {
        CStatus cStatus = CStatus.HEALTHY;
        if (this.getInfectionStatus()) {
            if (this.cVirus.isLatent()) cStatus = CStatus.LATENT;
            if (this.cVirus.isAsymptomatic()) cStatus = CStatus.ASYMPTOMATIC;
            if (this.cVirus.isSymptomatic()) enterQuarantine();
            if (this.cVirus.isPhase1()) {
                cStatus = CStatus.PHASE1;
          //      this.quarantine = this.quarantineProb > this.quarantineVal;
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
    
    public void enterQuarantine() {
        quarantine = quarantineProb.asDouble() > quarantineVal;
    }

    public void forceQuarantine() {
        quarantine = true;
    }
    
    public void exitQuarantine() {
        quarantine = false;
    }

    public boolean isInfectious() {
        return cStatus() == CStatus.ASYMPTOMATIC
                || cStatus() == CStatus.PHASE1
                || cStatus() == CStatus.PHASE2;
    }
    
    public double getTransAdjustment() {
    	return this.cVirus.getTransAdjustment();
    }

    public boolean hasPrimaryCommunalPlace() {
        return primaryPlace != null;
    }

    public abstract boolean avoidsPhase2(double testP);

    public boolean isWorking(CommunalPlace communalPlace, Time t) {
        if (primaryPlace == null || shifts == null) {
            return false;
        }

        int start = shifts.getShift(t.getDay()).getStart();
        int end = shifts.getShift(t.getDay()).getEnd();

        if (end < start) {
            end += 24;
        }

        return primaryPlace == communalPlace
                && t.getHour() >= start
                && t.getHour() < end;
    }


    public boolean worksNextHour(CommunalPlace communalPlace, Time t, boolean lockdown) {
        if (primaryPlace == null || shifts == null || primaryPlace != communalPlace) {
            return false;
        }

        if (lockdown) {
            if (!communalPlace.isKeyPremises()) {
                return false;
            }
        }

        // Handle day crossovers
        int day = t.getDay();
        int nextHour = 0;
        if (t.getHour() + 1 == 24) {
            day = (day + 1) % 7;
            nextHour = 0;
        } else {
            nextHour = t.getHour() + 1;
        }

        int start = shifts.getShift(day).getStart();
        int end = shifts.getShift(day).getEnd();
        if (end < start) {
            end += 24;
        }

        boolean shouldWork = nextHour >= start && nextHour < end;

        return shouldWork;
    }

    public void visitPrimaryPlace() {
        if (primaryPlace != null) {
            primaryPlace.addPersonNext(this);
        }
    }

    // People need to leave early if they have a shift starting in 2 hours time
    // 1 hour travels home, 1 travels to work; There is currently no direct travel to work.
    public boolean mustGoHome(Time t) {
        if (primaryPlace != null && shifts != null) {
            return t.getHour() + 2 >= shifts.getShift(t.getDay()).getStart();
        }
        return false;
    }

    public Sex getSex() {
        return sex;
    }

    public int getAge() {
        return age;
    }

    public boolean wasTested() {
        return testOutcome.isPresent();
    }

    public void getTested() {
        if (cVirus == null || wasTested() || !cVirus.isSymptomatic()) {
            return;
        }

        // Negative test
        if (!CovidParameters.get().testParameters.pDiagnosticTestDetectsSuccessfully.sample()) {
            exitQuarantine();
            home.stopIsolating();
            testOutcome = Optional.of(false);
        } else {
            testOutcome = Optional.of(true);
        }
    }

    public Optional<Boolean> getTestOutcome() {
        return testOutcome;
    }

    public void seedInfectionChallenge(Time t, DailyStats s) {
        Probability infectionChance = new Probability(getInfectionSeedRate() * t.getAbsDay());
        if (infectionChance.sample()) {
            cVirus = new Covid(this);
            cVirus.getInfectionLog().registerInfected(t);
            s.incSeedInfections();
        }
    }

    protected abstract double getInfectionSeedRate();

}
