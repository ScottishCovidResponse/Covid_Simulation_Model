/*
 * Base code for running Person objects. There are a number of sub classes, but most methods are here
 */

package uk.co.ramp.covid.simulation.population;

import org.apache.commons.math3.random.RandomDataGenerator;
import uk.co.ramp.covid.simulation.covid.Covid;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.HospitalApptInfo;
import uk.co.ramp.covid.simulation.place.Hospital;
import uk.co.ramp.covid.simulation.util.HospitalAppt;
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
    private boolean quarantine;
    private final Probability quarantineProb; // Needs more thought. The probability that the person will go into quarantine
    private Boolean testOutcome = null;
    protected final RandomDataGenerator rng;

    private boolean isHospitalised = false;
    private final boolean goesToHospitalInPhase2;
    
    private static int nPeople = 0;
    private final int personId;

    private boolean isInCare = false;
    
    private boolean moved = false;

    public abstract void reportInfection(DailyStats s);
    public abstract void reportDeath (DailyStats s);
    public abstract void allocateCommunalPlace(Places p);
    
    private final double covidMortalityAgeAdjustment;
    
    private final double covidSusceptibleVal; 
    
    private HospitalAppt hospitalAppt;

    public Person(int age, Sex sex) {
        this.age = age;
        this.sex = sex;
        this.rng = RNG.get();
        this.transmissionProb = PopulationParameters.get().personProperties.pTransmission.asDouble();
        this.quarantineProb = PopulationParameters.get().personProperties.pQuarantinesIfSymptomatic;
        this.goesToHospitalInPhase2 = CovidParameters.get().hospitalisationParameters.pPhase2GoesToHosptial.sample();
        this.personId = nPeople++;
        
        this.covidMortalityAgeAdjustment = Math.pow((double) age / 85.0, 2.0);
        
        if(age <= 20) this.covidSusceptibleVal = PopulationParameters.get().personProperties.pSusceptibleChild; // The original paper gave parameters broken at age 20
        else this.covidSusceptibleVal = 1.0;

    }

    @Override
    public int hashCode() {
        return personId;
    }

    public boolean isRecovered() {
        return recovered;
    }

    public void recover() {
        isHospitalised = false;
        recovered = true;
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

    public boolean infChallenge(double challengeProb) {
        if (rng.nextUniform(0, 1) < this.transmissionProb / 24 * challengeProb * covidSusceptibleVal) {
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
        CStatus cStatus = CStatus.HEALTHY;
        if (this.getInfectionStatus()) {
            if (this.cVirus.isLatent()) cStatus = CStatus.LATENT;
            if (this.cVirus.isAsymptomatic()) cStatus = CStatus.ASYMPTOMATIC;
            if (this.cVirus.isSymptomatic()) enterQuarantine();
            if (this.cVirus.isPhase1()) {
                cStatus = CStatus.PHASE1;
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
        quarantine = quarantineProb.sample();
    }

    public void forceQuarantine() {
        quarantine = true;
    }
    
    public void exitQuarantine() {
        quarantine = false;
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

    protected boolean isWorking(CommunalPlace communalPlace, Time t, boolean furloughed) {
        if (primaryPlace == null || shifts == null || furloughed) {
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

    public boolean isWorking(CommunalPlace communalPlace, Time t) {
        return isWorking(communalPlace, t, false);
    }


    public boolean worksNextHour(CommunalPlace communalPlace, Time t) {
        if (primaryPlace == null || shifts == null || primaryPlace != communalPlace
                || !communalPlace.isOpenNextHour(t)) {
            return false;
        }

        // Handle day crossovers
        int day = t.getDay();
        int nextHour = 0;
        if (t.getHour() + 1 == 24) {
            day = (day + 1) % 7;
        } else {
            nextHour = t.getHour() + 1;
        }

        int start = shifts.getShift(day).getStart();
        int end = shifts.getShift(day).getEnd();
        if (end < start) {
            end += 24;
        }

        return nextHour >= start && nextHour < end;
    }

    public void moveToPrimaryPlace(Place from) {
        if (primaryPlace != null) {
            moveTo(from, primaryPlace);
        }
    }

    // People need to leave early if they have a shift starting in 2 hours time, 
    // or if they have a hospital appt to get to
    // 1 hour travels home, 1 travels to work; There is currently no direct travel to work/hospitals.
    public boolean mustGoHome(Time t) {
        if (hospitalAppt != null) {
            return t.getHour() + 2 >= hospitalAppt.getStartTime().getHour();
        }

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

    // We can't determine this in household in case the person is working nightshift
    // Time is always the start of a day
    public void deteremineHospitalVisits(Time t, boolean lockdown, Places places) {
        HospitalApptInfo info = PopulationParameters.get().hospitalAppsParams().getParams(sex, age);
        
        // Appts might be across days so don't regenerate if we already have one
        if (hasHospitalAppt() && !getHospitalAppt().isOver(t)) {
            return;
        }
        
        double lockdownAdjust = lockdown ? 0.75 : 1.0;
        
        if (new Probability(info.pDayCase.asDouble() * lockdownAdjust).sample()) {
            // TODO-CHECK: Is this anytime after 8 or always at 8?
            Time startTime = new Time(t.getAbsTime() +
                    PopulationParameters.get().hospitalApptTimings.dayCaseStartTime);

            int length = (int) RNG.get().nextGaussian(
                    PopulationParameters.get().hospitalApptTimings.meanDayCaseTime,
                    PopulationParameters.get().hospitalApptTimings.SDDayCaseTime
            );
            if (length < 1) { length =  1; }

            // For small populations there might not be any non-COVID hospitals
            Hospital h = places.getRandomNonCovidHospital();
            if (h != null) {
                hospitalAppt = new HospitalAppt(startTime, length, h);
            }

        } else if (new Probability(info.pInPatient.asDouble() * lockdownAdjust).sample()) {
            Time startTime = new Time(t.getAbsTime() +
                    RNG.get().nextInt(
                            PopulationParameters.get().hospitalApptTimings.inPatientFirstStartTime,
                            PopulationParameters.get().hospitalApptTimings.inPatientLastStartTime));

            int length = info.inPatientLength.intValue();
            if (length < 1) { length =  1; }

            Hospital h = places.getRandomNonCovidHospital();
            if (h != null) {
                hospitalAppt = new HospitalAppt(startTime, length, h);
            }

        } else if (new Probability(info.pOutPatient.asDouble() * lockdownAdjust).sample()) {
            Time startTime = new Time(t.getAbsTime() +
                    RNG.get().nextInt(
                            PopulationParameters.get().hospitalApptTimings.outPatientFirstStartTime,
                            PopulationParameters.get().hospitalApptTimings.outPatientLastStartTime));


            int length = (int) RNG.get().nextExponential(
                    PopulationParameters.get().hospitalApptTimings.meanOutPatientTime);
            if (length < 1) { length =  1; }

            Hospital h = places.getRandomNonCovidHospital();
            if (h != null) {
                hospitalAppt = new HospitalAppt(startTime, length, h);
            }

        } else {
            hospitalAppt = null;
        }
    }
    
    public boolean hasHospitalAppt() {
        return hospitalAppt != null;
    }
    
    public HospitalAppt getHospitalAppt() {
        return hospitalAppt;
    }

    public void setHospitalAppt(HospitalAppt hospitalAppt) {
        this.hospitalAppt = hospitalAppt;
    }

}
