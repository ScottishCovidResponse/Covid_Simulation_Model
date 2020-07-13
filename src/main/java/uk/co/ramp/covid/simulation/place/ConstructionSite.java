package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

public class ConstructionSite extends CommunalPlace {

    public ConstructionSite(Size s) {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.constructionSiteExpectedInteractionsPerHour;
        times = OpeningTimes.nineFiveWeekdays();
    }

    @Override
    protected void setKey() {
        keyPremises = PopulationParameters.get().buildingProperties.pConstructionSiteKey.sample();
    }


    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t)) {
            s.constructionSiteInfectionsWorker.increment();
        } else {
            s.constructionSiteInfectionsVisitor.increment();
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
