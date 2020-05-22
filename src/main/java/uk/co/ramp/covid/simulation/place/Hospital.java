package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class Hospital extends CommunalPlace {

    public Hospital() {
        this(Size.UNKNOWN);
    }

    public Hospital(Size s) {
        super(s);
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpHospitalTrans();
        keyProb = PopulationParameters.get().getpHospitalKey();
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionHospital();
    }
}
