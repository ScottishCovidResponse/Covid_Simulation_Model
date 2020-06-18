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
        transAdjustment = PopulationParameters.get().buildingProperties.hospitalTransmissionConstant;
        keyProb = PopulationParameters.get().buildingProperties.pHospitalKey;
        times = OpeningTimes.twentyfourSeven();
        if (keyProb.sample()) keyPremises = true;
        allocateShifts();
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

    public void movePatients(Time t) {
        for (Person p : getPeople()) {
            if (p.hasMoved() || p.isWorking(this, t)) {
                continue;
            }
            
            if (p.isHospitalised()) {
                p.stayInPlace(this);
            } else {
                p.returnHome(this);
            }
        }
    }
    
    @Override
    public void determineMovement(Time t, boolean lockdown, Places places) {
        movePhase2(t, places, Person::isHospitalised);
        moveShifts(t, lockdown, Person::isHospitalised);
        movePatients(t);
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incHospitalDeaths();
    }

    @Override
    public double getTransP(Time t, Person infected, Person target) {
        double transP = getBaseTransP(infected);
        if (infected.isHospitalised()) {
            transP *= CovidParameters.get().hospitalisationParameters.hospitalisationTransmissionReduction;
        }
        return transP;
    }
}
