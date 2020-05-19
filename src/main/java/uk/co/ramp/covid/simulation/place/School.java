package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class School extends CommunalPlace {
    public School(int cindex) {
        super(cindex);
        int startTime = 9; // TODO. Not used at the moment, but may be used in the future. LEave them in for completeness
        int endTime = 15;
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpSchoolTrans();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsSchool();
    }
}
