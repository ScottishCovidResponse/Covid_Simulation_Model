package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.RNG;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.List;

/** The Infection Seeder class handles both direct infections and
 * external infections (to simulate movement from outwith the model) */
public class InfectionSeeder {

    private final Population population;
    private int externalInfectionDays = 0;
    private boolean travelSeeding = false;
    private Probability pTravelSeeding;
    
    public InfectionSeeder(Population population) {
        this.population = population;
    }

    // ExternalSeeding runs from 0-externalInfectionDays inclusive.
    // As infections on day 0 are 0 this gives a full externalInfectionsDays worth of infections.
    private void seedExternal(Time t, DailyStats s) {
        if (t.getAbsDay() <= externalInfectionDays) {
            for (Person p : population.getAllPeople()) {
                p.seedInfectionChallenge(t, s);
            }
        }
    }

    public void seedInfections(Time t, DailyStats s) {
       seedExternal(t, s);
       seedTravel(t, s);
    }

    /** Travel seeding using a fixed probability of infection */
    private void seedTravel(Time t, DailyStats s) {
        if (travelSeeding) {
            for (Person p : population.getAllPeople()) {
                p.seedInfectionChallenge(pTravelSeeding, t, s);
            }
        }
    }
    
    public void forceNInfections(int n) {
        List<Household> households = population.getHouseholds();
        int i = 0;
        while (i < n) {
            int nInt = RNG.get().nextInt(0, households.size() - 1);
            if (households.get(nInt).getHouseholdSize() > 0) {
                if (households.get(nInt).seedInfection()) {
                    i++;
                }
            }
        }
    }
    
    public void setExternalInfectionDays(int days) {
        this.externalInfectionDays = days;
    }

    public void startTravelSeeding(Probability pTravelSeeding) {
        this.pTravelSeeding = pTravelSeeding;
        this.travelSeeding = true;
    }

    public void stopTravelSeeding() {
        this.travelSeeding = false;
    }
    
}
