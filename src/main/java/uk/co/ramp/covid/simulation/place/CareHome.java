package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

public class CareHome extends CommunalPlace implements Home {

    private final RoundRobinAllocator<Shifts> shifts;

    public CareHome(Size s) {
        super(s);

        transConstant = PopulationParameters.get().buildingProperties.careHomeTransmissionConstant;

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

    @Override
    public void doMovement(Time t, boolean lockdown, Places places) {
        movePhase2(t, places, Person::isInCare);
        moveShifts(t, lockdown, Person::isInCare);
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
    public double getTransP(Time t, Person infected, Person target) {
        double transP = getBaseTransP(infected);
        // In case patients only infect staff due to quarantine
        boolean isQuarantined = infected.getcVirus().isSymptomatic()
                && t.getAbsTime() > infected.getcVirus().getInfectionLog().getSymptomaticTime().getAbsTime()
                                     + CovidParameters.get().careHomeParameters.hoursAfterSyptomsBeforeQuarantine;
        if (infected.isInCare() && isQuarantined) {
            if (target.isInCare()) {
                return 0.0;
            } else {
                return transP * CovidParameters.get().careHomeParameters.PPETransmissionReduction;
            }
        }
        return transP;
    }
}
