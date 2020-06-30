package uk.co.ramp.covid.simulation.integrationTests;

import org.junit.Test;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.place.Hospital;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Time;

import static org.junit.Assert.assertFalse;

public class LockdownTests extends SimulationTest  {

    @Test
    public void furloughedStaffDontGoToWork() {
        final int simDays = 7;
        final int populationSize = 20000;
       
        Population pop = PopulationGenerator.genValidPopulation(populationSize);
        pop.setLockdown(2,4,0.5);
        
        pop.setPostHourHook((population, time) -> {
            // We need to give working staff some time to go home so we delay this a few hours after furlough (starts day 3).
            if (time.getAbsTime() >= 3*24 + 1) {
                for (Hospital plc : population.getPlaces().getNonCovidHospitals()) {
                    for (Person p : plc.getPeople()) {
                        if (p.getPrimaryCommunalPlace() == plc && !p.isHospitalised()) {
                            assertFalse(p.isFurloughed());
                        }
                    }
                }
            }
        });

        pop.simulate(simDays);
    }
}
