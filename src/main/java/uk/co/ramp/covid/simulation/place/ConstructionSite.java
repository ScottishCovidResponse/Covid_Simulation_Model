package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

public class ConstructionSite extends CommunalPlace {

    public ConstructionSite(Size s) {
        super(s);
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpConstructionSiteTrans();
        keyProb = PopulationParameters.get().getpConstructionSiteKey();
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;
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
