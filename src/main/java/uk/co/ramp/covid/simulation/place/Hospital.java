package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.BuildingTimeParameters;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

import java.util.List;

public class Hospital extends CommunalPlace {
    
    private RoundRobinAllocator<Shifts> shifts;

    public Hospital(Size s) {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.hospitalExpectedInteractionsPerHour;
    }

    @Override
    protected void setKey() {
        keyPremises =  PopulationParameters.get().buildingProperties.pHospitalKey.sample();
    }

    @Override
    protected List<BuildingTimeParameters> getTimeInfo() {
        return PopulationParameters.get().buildingProperties.hospitalTimes;
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t)) {
            s.hospitalInfectionsWorker.increment();
        } else {
            s.hospitalInfectionsVisitor.increment();
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
        s.hospitalDeaths.increment();
    }

    @Override
    public double getTransP(Person infected) {
        double transP = getBaseTransP(infected);
        if (infected.isHospitalised()) {
            transP *= CovidParameters.get().hospitalisationParameters.hospitalisationTransmissionReduction;
        }
        return transP;
    }
    
}
