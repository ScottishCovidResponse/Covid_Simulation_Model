package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

public class ConstructionSite extends CommunalPlace {

    public ConstructionSite() {
        this(Size.UNKNOWN);
    }
    
    public ConstructionSite(Size s) {
        super(s);
        transAdjustment = PopulationParameters.get().getpConstructionSiteTrans();
        keyProb = PopulationParameters.get().getpConstructionSiteKey();
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;
        times = OpeningTimes.nineFiveWeekdays();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionConstructionSite();
    }

    @Override
    public Shifts getShifts() {
        nStaff++;
        return Shifts.nineFiveFiveDays();
    }

    @Override
    public boolean isFullyStaffed() {
        return nStaff > 0;
    }
    
    public void updateTransProb() {
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpConstructionSiteTrans() / super.people.size();    	
    }

}
