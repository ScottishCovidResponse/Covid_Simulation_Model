package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class Office extends CommunalPlace {
    public Office(int cindex) {
        super(cindex);
        this.transProb = PopulationParameters.get().getpOfficeTrans();
        this.keyProb = PopulationParameters.get().getpOfficeKey();
        if (Math.random() > this.keyProb) this.keyPremises = true;
    }

}
