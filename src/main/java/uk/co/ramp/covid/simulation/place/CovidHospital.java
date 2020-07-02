package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.util.Time;

public class CovidHospital extends Hospital {
    public CovidHospital(Size s) {
        super(s);
    }

    @Override
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
}
