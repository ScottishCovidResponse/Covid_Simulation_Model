/*
 * Base code for running Person objects. There are a number of sub classes, but most methods are here
 */

package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.covid.Covid;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.HospitalApptInfo;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.util.HospitalAppt;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
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
    
    private boolean isQuarantined;
    private final boolean willQuarantine;
    
    private Boolean testOutcome = null;

    private boolean isHospitalised = false;
    private boolean goesToHospitalInPhase2;
    
    private static int nPeople = 0;
    private final int personId;

    private boolean isInCare = false;
    protected boolean furloughed = false;
    
    private boolean moved = false;
    private Transport transport = null;

    private double lockdownHospitalApptAdjustment = 0.0;

    public abstract void reportInfection(DailyStats s);
    public abstract void reportDeath (DailyStats s);
    public abstract void allocateCommunalPlace(Places p);
    
    private final double covidMortalityAgeAdjustment;
    
    private final double covidSusceptibleVal; 
    
    private HospitalAppt hospitalAppt;

    public Person(int age, Sex sex) {
        this.age = age;
        this.sex = sex;
        this.willQuarantine = PopulationParameters.get().personProperties.pQuarantinesIfSymptomatic.sample();
        this.personId = nPeople++;
        
        this.covidMortalityAgeAdjustment = setMortality();
        
        if(age <= 20) {
            // The original paper gave parameters broken at age 20
            this.covidSusceptibleVal = PopulationParameters.get().personProperties.susceptibleChildConstant;
        } else {
            this.covidSusceptibleVal = 1.0;
        }

    }

    @Override
    public int hashCode() {
        return personId;
    }
    
    public double setMortality() {
    	double out = 0.0;
    	if(age > 50) out = Math.pow((((double) age - 50.0) / 50.0) + CovidParameters.get().diseaseParameters.caseMortalityBase, 2.0);
    	else out = CovidParameters.get().diseaseParameters.caseMortalityBase;
    	return out;
    }
    
    public void setWillBeHospitalised() {
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

        if (transport != null && from != to) {
            transport.addPassenger(this);
        }

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

    public boolean forceInfect() {
        if (cVirus == null) {
            this.cVirus = new Covid(this);
            return true;
        }
        return false;
    }

    public boolean isInCare() {
        return isInCare;
    }

    public void hospitalise(DailyStats s) {
        isHospitalised = true;
        s.newlyHospitalised.increment();
    }

    public boolean goesToHosptialInPhase2() {
        return goesToHospitalInPhase2;
    }

    public boolean isInfected() {
        return cVirus != null && !recovered;
    }

    public void stepInfection(Time t) {
        if (cVirus != null) {
            cVirus.stepInfection(t);
        }
    }

    public boolean infChallenge(double environmentAdjustment) {
        Probability pInf = new Probability(environmentAdjustment * covidSusceptibleVal);
        if (cVirus == null && pInf.sample()) {
            cVirus = new Covid(this);
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
        if (cVirus != null) {
            return cVirus.getStatus();
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

    public abstract boolean avoidsPhase2(double testP);

    public boolean isWorking(CommunalPlace communalPlace, Time t) {
        if (primaryPlace == null
                || shifts == null
                || primaryPlace != communalPlace
                || isFurloughed() || isHospitalised
                || !communalPlace.isOpen(t)) {
            return false;
        }

        int start = shifts.getShift(t.getDay()).getStart();
        int end = shifts.getShift(t.getDay()).getEnd();

        if (end < start) {
            end += 24;
        }

        return t.getHour() >= start && t.getHour() < end;
    }
    
    public boolean worksNextHour(CommunalPlace communalPlace, Time t) {
        return isWorking(communalPlace, t.advance());
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
        final double seedRate = CovidParameters.get().infectionSeedProperties.rateIncreaseSeed;
        double dayAdjust = Math.pow(seedRate, t.getAbsDay());
        Probability infectionChance = new Probability(getInfectionSeedInitial() * dayAdjust * covidSusceptibleVal);
        seedInfectionChallenge(infectionChance, t, s);
    }

    public void seedInfectionChallenge(Probability p, Time t, DailyStats s) {
        if (cVirus == null && p.sample()) {
            cVirus = new Covid(this);
            cVirus.getInfectionLog().registerInfected(t);
            s.seedInfections.increment();
        }
    }
    
    public boolean hasMoved() {
        return moved;
    }
    
    public void unsetMoved() {
        moved = false;
    }
    
    public void takesPublicTransport(Transport t) {
        transport = t;
    }

    protected abstract double getInfectionSeedInitial();
    
    public void forceFurlough() {
        furloughed = true;
    }

    public void furlough() {}

    // We can't determine this in household in case the person is working nightshift
    // Time is always the start of a day
    public void deteremineHospitalVisits(Time t, Places places) {
        // Appts might be across days so don't regenerate if we already have one
        if (hasHospitalAppt() && !getHospitalAppt().isOver(t)) {
            return;
        }

        double lockdownAdjust = 1.0 - lockdownHospitalApptAdjustment;

        Time startTime = null;
        int length = 0;

        // Priority order: InPatient/DayCase/OutPatient
        HospitalApptInfo info = PopulationParameters.get().hospitalAppsParams().getParams(sex, age);
        if (info.pInPatientDaily.adjust(lockdownAdjust).sample()) {
            startTime = new Time(t.getAbsTime() +
                    RNG.get().nextInt(
                            PopulationParameters.get().hospitalApptProperties.inPatientFirstStartTime,
                            PopulationParameters.get().hospitalApptProperties.inPatientLastStartTime));

            length = info.inPatientLengthDays.intValue() * 24;
        } else if (info.pDayCaseDaily.adjust(lockdownAdjust).sample()) {
            startTime = new Time(t.getAbsTime() +
                    PopulationParameters.get().hospitalApptProperties.dayCaseStartTime);

            length = (int) RNG.get().nextGaussian(
                    PopulationParameters.get().hospitalApptProperties.meanDayCaseTime,
                    PopulationParameters.get().hospitalApptProperties.SDDayCaseTime
            );
        } else if (info.pOutPatientDaily.adjust(lockdownAdjust).sample()) {
            startTime = new Time(t.getAbsTime() +
                    RNG.get().nextInt(
                            PopulationParameters.get().hospitalApptProperties.outPatientFirstStartTime,
                            PopulationParameters.get().hospitalApptProperties.outPatientLastStartTime));

            length = (int) RNG.get().nextExponential(
                    PopulationParameters.get().hospitalApptProperties.meanOutPatientTime);
        }

        if (startTime != null) {
            length = Math.max(length, 1);
            Hospital h = places.getRandomNonCovidHospital();
            // For small populations you might only have a COVID hospital
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

    public boolean isFurloughed() {
        return furloughed;
    }

    public void unFurlough() {
        furloughed = false;
    }

    public void setLockdownHospitalApptAdjustment(double lockdownHospitalApptAdjustment) {
        this.lockdownHospitalApptAdjustment = lockdownHospitalApptAdjustment;
    }
}
