package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Nursery;
import uk.co.ramp.covid.simulation.place.School;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;

public class LockdownController {
    
    private Time lockdownStart;
    private Time lockdownEnd;
    
    private boolean schoolLockdown = false;
    
    private double socialDist = 1.0;
    private boolean inLockdown = false;
    
    private final Population population;

    public LockdownController(Population p) {
        population = p;
    }
    
    public void setLockdown(Time start, Time end, double sdist) {
        lockdownStart = start;
        lockdownEnd = end;
        socialDist = sdist;
    }

    public void setSchoolLockdown(Time start, Time end, double sdist) {
        lockdownStart = start;
        lockdownEnd = end;
        socialDist = sdist;
        schoolLockdown = true;
    }

    public void implementLockdown(Time now) {
        if (now.equals(lockdownStart)) {
            enterLockdown(now);
        }

        if (now.equals(lockdownEnd)) {
            exitLockdown(now);
        }
    }
    
    private void furloughStaff() {
        for (Person p : population.getAllPeople()) {
            p.furlough();
        }
    }

    private void unFurloughStaff() {
        for (Person p : population.getAllPeople()) {
            p.unFurlough();
        }
    }
    
    private void lockdownPlaces() {
        for (CommunalPlace cPlace : population.getPlaces().getAllPlaces()) {
            cPlace.enterLockdown(socialDist);
        }
    }

    private void unLockdownPlaces() {
        for (CommunalPlace cPlace : population.getPlaces().getAllPlaces()) {
            cPlace.exitLockdown();
        }
    }

    private void enterLockdown(Time t) {
        inLockdown = true;
        lockdownPlaces();
        furloughStaff();
    }

    // This sets the schools exempt from lockdown if that is triggered. Somewhat fudged at present by setting the schools to be KeyPremises
    private void schoolExemption() {
        for (School s : population.getPlaces().getSchools()) {
            s.overrideKeyPremises(true);
        }
        for (Nursery n : population.getPlaces().getNurseries()) {
            n.overrideKeyPremises(true);
        }
    }

    private void exitLockdown(Time t) {
        // TODO: School lockdown still needs fixed this is the original "population" behaviour
        if (schoolLockdown) {
            schoolExemption();
        } else {
            unLockdownPlaces();
            unFurloughStaff();
            inLockdown = false;
        }
    }

    public boolean inLockdown(Time t) {
       return inLockdown;
    }

    public Time getLockdownStart() {
        return lockdownStart;
    }

    public Time getLockdownEnd() {
        return lockdownEnd;
    }
}
