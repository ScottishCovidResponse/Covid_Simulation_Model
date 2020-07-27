package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.BuildingTimeParameters;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

import java.util.List;

public class ConstructionSite extends CommunalPlace {

    public ConstructionSite(Size s) {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.constructionSiteExpectedInteractionsPerHour;
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
    protected List<BuildingTimeParameters> getTimeInfo() {
        return PopulationParameters.get().buildingProperties.constructionSiteTimes;
    }

}
