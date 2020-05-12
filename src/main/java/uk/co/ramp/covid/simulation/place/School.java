package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class School extends CommunalPlace {
    public School(int cindex) {
        super(cindex);
        int startTime = 9;
        int endTime = 15;
        this.transProb = PopulationParameters.get().getpSchoolTrans();
    }
}
