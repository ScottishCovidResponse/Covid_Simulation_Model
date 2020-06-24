/*
 * Base code for running Person objects. There are a number of sub classes, but most methods are here
 */

package uk.co.ramp.covid.simulation.population;

import org.apache.commons.math3.random.RandomDataGenerator;
import uk.co.ramp.covid.simulation.covid.Covid;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.CareHome;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Place;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.RNG;

public abstract class Person {


    public enum Sex {
        MALE, FEMALE
    }
    
    protected CommunalPlace primaryPlace = null;
    protected Shifts shifts = null;
    
    private final Sex sex;
    private final int age;

    private Home home;
    private boolean recovered;
    private Covid cVirus;
    private final double transmissionProb;
    
    private boolean isQuarantined;
    private boolean willQuarantine;
    
    private Boolean testOutcome = null;
    protected final RandomDataGenerator rng;

    private boolean isHospitalised = false;
    private boolean goesToHospitalInPhase2;
    
    private static int nPeople = 0;
    private final int personId;

    private boolean isInCare = false;
    protected boolean furloughed = false;
    
    private boolean moved = false;

    public abstract void reportInfection(DailyStats s);
    public abstract void reportDeath (DailyStats s);
    public abstract void allocateCommunalPlace(Places p);
    
    private final double covidMortalityAgeAdjustment;
    
    private final double covidSusceptibleVal; 


    public Person(int age, Sex sex) {
        this.age = age;
        this.sex = sex;
        this.rng = RNG.get();
        this.transmissionProb = PopulationParameters.get().personProperties.pTransmission.asDouble();
        this.willQuarantine = PopulationParameters.get().personProperties.pQuarantinesIfSymptomatic.sample();
        this.personId = nPeople++;
        
        this.covidMortalityAgeAdjustment = Math.pow((double) age / 85.0, 2.0);
        
        if(age <= 20) this.covidSusceptibleVal = PopulationParameters.get().personProperties.pSusceptibleChild; // The original paper gave parameters broken at age 20
        else this.covidSusceptibleVal = 1.0;

    }

    @Override
    public int hashCode() {
        return personId;
    }
    
    public void setHospitlaised() {
    	if(!isInCare) {
    		goesToHospitalInPhase2 = true;
    	}
    }

    public boolean isRecovered() {
        return recovered;
    }

    public void recover() {
        isHospitalised = false;
        recovered = true;
        isQuarantined = false;
    }

    public CommunalPlace getPrimaryCommunalPlace() {
        return this.primaryPlace;
    }
    
    public double getCovidMortalityAgeAdjustment() {
    	return covidMortalityAgeAdjustment;
    }

    public void setPrimaryPlace(CommunalPlace p) {
        this.primaryPlace = p;
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home h) {
        home = h;
    }
    
    public void returnHome(Place from) {
        moveTo(from, (Place) home);
    }
    
    public void moveTo(Place from, Place to) {
        //We can turn off this asserting during main runs, but it's a nice sanity check to have
        assert !hasMoved();

        to.addPersonNext(this);
        moved = true;
    }
    
    public void stayInPlace(Place from) {
        moveTo(from, from);
    }

    public boolean isHospitalised() {
        return isHospitalised;
    }

    public boolean isQuarantined() {
        return isQuarantined;
    }

    public boolean infect() {
        boolean inf = false;
        if (!this.getInfectionStatus()) {
            this.cVirus = new Covid(this);
            inf = true;
        }

        return inf;
    }

    public boolean isInCare() {
        return isInCare;
    }

    public void hospitalise(DailyStats s) {
        isHospitalised = true;
        s.incHospitalised();
    }

    public boolean goesToHosptialInPhase2() {
        return goesToHospitalInPhase2;
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

    public boolean infChallenge(double environmentAdjustment) {
        if (rng.nextUniform(0, 1) < environmentAdjustment * covidSusceptibleVal && this.cVirus == null) {
            this.cVirus = new Covid(this);
            return true;
        }
        return false;
    }

    // Try to place this person in a CareHome
    public boolean enterCare(Places places) {
        CareHome h = places.getRandomCareHome();
        if (h != null) {
            h.addPerson(this);

            // Permanently seconded to a CareHome
            setHome(h);

            isInCare = true;
            return true;
        }
        
        return false;
    }

    public Covid getcVirus() {
        return cVirus;
    }

    // This method is pretty important, it returns the Covid infection status
    public CStatus cStatus() {
        if (this.getInfectionStatus()) {
            if (this.cVirus.isLatent()) { return CStatus.LATENT; }
            if (this.cVirus.isAsymptomatic()) { return CStatus.ASYMPTOMATIC; };
            if (this.cVirus.isPhase1()) { return CStatus.PHASE1; }
            if (this.cVirus.isPhase2()) { return CStatus.PHASE2; }
            if (this.cVirus.isDead()) { return CStatus.DEAD; }
            if (this.cVirus.isRecovered()) { return CStatus.RECOVERED; }
        }
        return CStatus.HEALTHY;
    }
    
    public void enterQuarantine() {
        if (willQuarantine) {
            isQuarantined = true;
        }
    }

    public void forceQuarantine() {
        isQuarantined = true;
    }
    
    public void exitQuarantine() {
        isQuarantined = false;
    }

    public boolean isInfectious() {
        return cStatus() != null
                && (cStatus() == CStatus.ASYMPTOMATIC
                || cStatus() == CStatus.PHASE1
                || cStatus() == CStatus.PHASE2);
    }

    public double getTransAdjustment() {
    	return this.cVirus.getTransAdjustment();
    }

    public boolean hasPrimaryCommunalPlace() {
        return primaryPlace != null;
    }

    public abstract boolean avoidsPhase2(double testP);

    public boolean isWorking(CommunalPlace communalPlace, Time t) {
        if (primaryPlace == null || shifts == null
                || isFurloughed() || isHospitalised
                || !communalPlace.isOpen(t)) {
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
    
    public boolean worksNextHour(CommunalPlace communalPlace, Time t) {
        return isWorking(communalPlace, t.advance());
    }

    public void moveToPrimaryPlace(Place from) {
        if (primaryPlace != null) {
            moveTo(from, primaryPlace);
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
        return testOutcome != null;
    }

    public void getTested() {
        if (cVirus == null || wasTested() || !cVirus.isSymptomatic()) {
            return;
        }

        // Negative test
        if (!CovidParameters.get().testParameters.pDiagnosticTestDetectsSuccessfully.sample()) {
            exitQuarantine();
            home.stopIsolating();
            testOutcome = false;
        } else {
            testOutcome = true;
        }
    }

    public Boolean getTestOutcome() {
        return testOutcome;
    }

    public int getID() {
        return personId;
    }

    public void seedInfectionChallenge(Time t, DailyStats s) {
        Probability infectionChance = new Probability(getInfectionSeedRate() * t.getAbsDay() * this.covidSusceptibleVal);
        if (infectionChance.sample()) {
            cVirus = new Covid(this);
            cVirus.getInfectionLog().registerInfected(t);
            s.incSeedInfections();
        }
    }
    
    public boolean hasMoved() {
        return moved;
    }
    
    public void unsetMoved() {
        moved = false;
    }

    protected abstract double getInfectionSeedRate();
    
    public void furlough() {};

    public boolean isFurloughed() { return furloughed; }
    public void unFurlough() {
        furloughed = false;
    }
}
