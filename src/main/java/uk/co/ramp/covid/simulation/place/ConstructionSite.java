package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

public class ConstructionSite extends CommunalPlace {

    public ConstructionSite(Size s) {
        super(s);
        transAdjustment = PopulationParameters.get().buildingProperties.constructionSiteTransmissionConstant;
        keyProb = PopulationParameters.get().buildingProperties.pConstructionSiteKey;
        if (keyProb.sample()) keyPremises = true;
        times = OpeningTimes.nineFiveWeekdays();
    }


    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t)) {
            s.incInfectionConstructionSiteWorker();
        } else {
            s.incInfectionsConstructionSiteVisitor();
        }
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
    
}
