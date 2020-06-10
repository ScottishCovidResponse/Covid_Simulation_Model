package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

import java.util.ArrayList;

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

    public void sendHome(Time t) {
        ArrayList<Person> left = new ArrayList<>();
        for (Person nPers : people) {
            if (nPers.worksNextHour(this, t, false)) {
                continue;
            }

            // Let recovered patients go home
            if (!nPers.isHospitalised()) {
                nPers.returnHome();
            }
        }
        people.removeAll(left);
    }

    @Override
    public void doMovement(Time t, boolean lockdown) {
        moveShifts(t, lockdown);
        sendHome(t);
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incHospitalDeaths();
    }
}
