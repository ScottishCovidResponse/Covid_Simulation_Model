package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class Office extends CommunalPlace {
    public Office(int cindex) {
        super(cindex);
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpOfficeTrans();
        keyProb = PopulationParameters.get().getpOfficeKey();
        if (Math.random() > keyProb) keyPremises = true;
    }

}
