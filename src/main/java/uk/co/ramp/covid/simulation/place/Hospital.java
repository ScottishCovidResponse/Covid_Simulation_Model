package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

public class Hospital extends CommunalPlace {
    
    private RoundRobinAllocator<Shifts> shifts;

    public Hospital(Size s) {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.hospitalExpectedInteractionsPerHour;
        times = OpeningTimes.twentyfourSeven();
        allocateShifts();
    }

    @Override
    protected void setKey() {
        keyPremises =  PopulationParameters.get().buildingProperties.pHospitalKey.sample();
    }

    private void allocateShifts() {
        shifts = new RoundRobinAllocator<>();
        shifts.put(new Shifts(0,12, 0, 1, 2));
        shifts.put(new Shifts(12,0, 0, 1, 2));
        shifts.put(new Shifts(0,12, 3, 4, 5, 6));
        shifts.put(new Shifts(12,0, 3, 4, 5, 6));
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
        if (p.isWorking(this, t)) {
            s.incInfectionHospitalWorker();
        } else {
            s.incInfectionsHospitalVisitor();
        }
    }
    
    public boolean isPatient(Person p, Time t) {
        return p.hasHospitalAppt() && p.getHospitalAppt().isOccurring(t);
    }

    public void movePatients(Time t) {
        for (Person p : getPeople()) {
            if (p.hasMoved()) {
                continue;
            }

            if (isPatient(p, t.advance())) {
                p.stayInPlace(this);
            } else {
                if (!p.worksNextHour(this, t)) {
                    p.returnHome(this);
                }
            }
        }
    }
    
    @Override
    public void determineMovement(Time t, DailyStats s, Places places) {
        movePhase2(t, s, places, Person::isHospitalised);
        movePatients(t);
        moveShifts(t);
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incHospitalDeaths();
    }

    @Override
    public double getTransP(Person infected) {
        double transP = getBaseTransP(infected);
        if (infected.isHospitalised()) {
            transP *= CovidParameters.get().hospitalisationParameters.hospitalisationTransmissionReduction;
        }
        return transP;
    }
    
    @Override
    public double getEnvironmentAdjustment(Person susceptible, Person infected, Time t) {
    	
    	if(!susceptible.isHospitalised() && infected.isHospitalised() && this instanceof CovidHospital) {
    		return CovidParameters.get().hospitalisationParameters.hospitalisationTransmissionReduction;
    	}
    	else return environmentAdjustment;
    }
}
