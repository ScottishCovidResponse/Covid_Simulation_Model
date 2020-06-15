package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Home;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

import java.util.ArrayList;
import java.util.List;

public class CareHome extends CommunalPlace implements Home {

    private RoundRobinAllocator<Shifts> shifts;

    public CareHome(Size s) {
        super(s);

        transAdjustment = PopulationParameters.get().buildingProperties.hospitalTransmissionConstant;

        shifts = new RoundRobinAllocator<>();
        shifts.put(new Shifts(6,14, 0, 1, 2));
        shifts.put(new Shifts(14,22, 0, 1, 2));
        shifts.put(new Shifts(6,14, 3, 4, 5, 6));
        shifts.put(new Shifts(14,22, 3, 4, 5, 6));
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

    // TODO-BA: Possibly add a predicate to the usual moveShifts since the hospitals have a similar "isHospitalised" check gg
    public void moveShifts(Time t, boolean lockdown) {
        List<Person> left = new ArrayList<>();
        for (Person p : people) {
            if (!p.isInCare() && !p.worksNextHour(this, t, lockdown)) {
                p.returnHome();
                left.add(p);
            }
        }
        people.removeAll(left);
    }

    @Override
    public void doMovement(Time t, boolean lockdown) {
        moveShifts(t, lockdown);
    }

    @Override
    public void isolate() { }

    @Override
    public void stopIsolating() { }
}
