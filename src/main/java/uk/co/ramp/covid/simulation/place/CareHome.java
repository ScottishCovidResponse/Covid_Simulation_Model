package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

public class CareHome extends CommunalPlace implements Home {

    private final RoundRobinAllocator<Shifts> shifts;

    public CareHome(Size s) {
        super(s);

        transAdjustment = PopulationParameters.get().buildingProperties.careHomeTransmissionConstant;

        // Care homes are "open" to staff from 6-22 (but can have residents in them all the time)
        times = new OpeningTimes(6, 22, OpeningTimes.getAllDays());

        shifts = new RoundRobinAllocator<>();
        shifts.put(new Shifts(6,14, 0, 1, 2));
        shifts.put(new Shifts(14,22, 0, 1, 2));
        shifts.put(new Shifts(6,14, 3, 4, 5, 6));
        shifts.put(new Shifts(14,22, 3, 4, 5, 6));
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
            s.incInfectionCareHomeWorker();
        } else {
            s.incInfectionCareHomeResident();
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
    public void determineMovement(Time t, DailyStats s, boolean lockdown, Places places) {
        movePhase2(t, s, places, Person::isInCare);
        moveHospital(t);
        moveShifts(t, lockdown, Person::isInCare);
        remainInPlace();
    }

    @Override
    public void isolate() { }

    @Override
    public void stopIsolating() { }

    @Override
    public void reportDeath(DailyStats s) {
        s.incCareHomeDeaths();
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

}
