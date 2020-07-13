package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.util.Time;

public class CovidHospital extends Hospital {
    public CovidHospital(Size s) {
        super(s);
    }

    @Override
    public void movePatients(Time t) {
        for (Person p : getPeople()) {
            if (p.hasMoved()) {
                continue;
            }

            if (p.isHospitalised()) {
                p.stayInPlace(this);
            } else {
                if (!p.isWorking(this, t)) {
                    p.returnHome(this);
                }
            }
        }
    }

    @Override
    public double getEnvironmentAdjustment(Person susceptible, Person infected, Time t) {
        if(!susceptible.isHospitalised() && infected.isHospitalised()) {
            return CovidParameters.get().hospitalisationParameters.hospitalisationTransmissionReduction;
        }
        return environmentAdjustment;
    }
}
