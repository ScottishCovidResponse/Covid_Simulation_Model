package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class Office extends CommunalPlace {

    public enum OfficeSize {
        SMALL, MED, LARGE, UNKNOWN;
    }

    private OfficeSize size;

    public Office(OfficeSize s) {
        this();
        size = s;
    }

    public Office() {
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpOfficeTrans();
        keyProb = PopulationParameters.get().getpOfficeKey();
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;
        size = OfficeSize.UNKNOWN;
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionOffice();
    }
    
    public OfficeSize getSize() {
        return size;
    }

    public void setSize(OfficeSize s) {
        size = s;
    }

}
