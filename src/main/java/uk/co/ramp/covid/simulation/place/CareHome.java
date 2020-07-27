package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.parameters.BuildingTimeParameters;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.ShiftAllocator;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;

import java.util.Objects;

public class CareHome extends CommunalPlace implements Home {

    private final ShiftAllocator shifts;

    private CareHomeResidentRange expectedResidents;
    private int residents = 0;

    public CareHome(Size s, CareHomeResidentRange expectedResidents) {
        super(s);

        this.expectedResidents = expectedResidents;

        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.careHomeExpectedInteractionsPerHour;

        // Care homes are "open" to staff from 6-22 (but can have residents in them all the time)
       // times = new OpeningTimes(6, 22, OpeningTimes.getAllDays());


        // Care homes are "open" to staff at certain times, but can have residents in them at all times.

        // TODO: Generalise to all place times
        BuildingTimeParameters timings = PopulationParameters.get().buildingProperties.careHomeTimes.get(0);
        times = timings.openingTime;

        // We use a copy constructor here so that the "next" pointer isn't shared by all places
        shifts = new ShiftAllocator(timings.shifts);
    }

    public void addResident(Person p) {
        people.add(p);
        residents++;
    }

    public boolean residentsNeeded() {
        return residents < expectedResidents.min;
    }

    public CareHomeResidentRange getResidentRange() {
        return expectedResidents;
    }

    public boolean residentsInRange() {
        return expectedResidents.inRange(residents);
    }

    @Override
    protected void setKey() {
        keyPremises = true;
    }

    @Override
    public Shifts getShifts() {
        nStaff++;
        return shifts.getNext();
    }

    @Override
    public boolean isFullyStaffed() {
        return nStaff >= 4;
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (!p.isInCare()) {
            s.careHomeInfectionsWorker.increment();
        } else {
            s.careHomeInfectionsResident.increment();
        }
    }

    private void moveHospital(Time t) {
        for (Person p : getPeople()) {
            if (p.hasMoved() || !p.hasHospitalAppt()) {
                continue;
            }
            if (p.getHospitalAppt().getStartTime().equals(t.advance())) {
                p.moveTo(this, p.getHospitalAppt().getApptLocation());
            }
        }
    }

    @Override
    public void determineMovement(Time t, DailyStats s, Places places) {
        movePhase2(t, s, places, Person::isInCare);
        moveHospital(t);
        moveShifts(t, Person::isInCare);
        remainInPlace();
    }


    @Override
    public void stopIsolating() { }

    @Override
    public void reportDeath(DailyStats s) {
        s.careHomeDeaths.increment();
    }

    @Override
    public double getEnvironmentAdjustment(Person target, Person infected, Time t) {
    	if(!infected.isInCare()) { 
    		return environmentAdjustment;
    		}
    	
    	else {
    		boolean isQuarantined = infected.getcVirus().isSymptomatic()
    				&& t.getAbsTime() > infected.getcVirus().getInfectionLog().getSymptomaticTime().getAbsTime()
                                     + CovidParameters.get().careHomeParameters.hoursAfterSymptomsBeforeQuarantine;
    			if (!isQuarantined) {
    				return environmentAdjustment;
    				}
    			else {
    				if (target.isInCare()) {
    					return 0.0;
    				} 
    				else {
    					return CovidParameters.get().careHomeParameters.PPETransmissionReduction;
    				}
    			}
    		}
    }

    // Essentially a Pair-type
    public static class CareHomeResidentRange {
        // Probability the care home has this range of residents
        public Probability probability;

        public int min;
        public int max;

        public CareHomeResidentRange(int min, int max, Probability p) {
            this.min = min; this.max = max; this.probability = p;
        }

        public int getRange() { return max - min; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CareHomeResidentRange that = (CareHomeResidentRange) o;
            return min == that.min &&
                    max == that.max;
        }

        @Override
        public int hashCode() {
            return Objects.hash(probability, min, max);
        }

        public boolean inRange(int residents) {
            return residents >= min && residents < max;
        }
    }

}
