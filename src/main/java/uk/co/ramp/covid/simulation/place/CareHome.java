package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.parameters.BuildingTimeParameters;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;

import java.util.List;
import java.util.Objects;

public class CareHome extends CommunalPlace implements Home {
    private CareHomeResidentRange expectedResidents;
    private int residents = 0;

    public CareHome(Size s, CareHomeResidentRange expectedResidents) {
        super(s);
        this.expectedResidents = expectedResidents;
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.careHomeExpectedInteractionsPerHour;
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
    protected List<BuildingTimeParameters> getTimeInfo() {
        return PopulationParameters.get().buildingProperties.careHomeTimes;
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
