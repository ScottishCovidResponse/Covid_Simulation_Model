package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class Nursery extends CommunalPlace {
    public Nursery(int cindex) {
        super(cindex);
        this.transProb = PopulationParameters.get().getpNurseryTrans();
    }
}
