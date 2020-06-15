package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.HospitalisationParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

import java.util.ArrayList;
import java.util.List;

public class CareHome extends CommunalPlace implements Home {

    private RoundRobinAllocator<Shifts> shifts;

    public CareHome(Size s) {
        super(s);

        transAdjustment = PopulationParameters.get().buildingProperties.careHomeTransmissionConstant;

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

    // TODO-BA: Possibly add a predicate to the usual moveShifts since the hospitals have a similar "isHospitalised" check
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


    // TODO-BA: add similar predicate to above TODO
    public void movePhase2(Time t, Places places) {
        List<Person> left = new ArrayList<>();
        for (Person p : people) {

            // Care patients never go to hospitals during phase 2
            if (p.isInCare()) {
                continue;
            }

            if (p.cStatus() != null && p.cStatus() == CStatus.PHASE2) {
                if (p.goesToHosptialInPhase2()) {
                    Hospital h = places.getRandomCovidHospital();
                    p.hospitalise();
                    h.addPersonNext(p);
                    left.add(p);
                } else {
                    p.returnHome();
                    left.add(p);
                }
            }
        }
        people.removeAll(left);
    }

    @Override
    public void doMovement(Time t, boolean lockdown, Places places) {
        movePhase2(t, places);
        moveShifts(t, lockdown);
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
    public double getTransP(Person infected, Person target) {
        double transP = getBaseTransP(infected);
        // In case patients only infect staff due to quarantine
        if (infected.isInCare()) {
            if (target.isInCare()) {
                return 0.0;
            } else {
                return transP * CovidParameters.get().hospitalisationParameters.hospitalisationTransmissionReduction;
            }
        }
        return transP;
    }
}
