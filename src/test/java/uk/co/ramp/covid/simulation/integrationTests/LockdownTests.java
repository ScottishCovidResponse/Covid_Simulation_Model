package uk.co.ramp.covid.simulation.integrationTests;

import org.junit.Test;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Hospital;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.place.Place;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
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

        Time t = new Time(0);
        
        // First 2 days people go to work as usual
        for (int i = 0; i < simDays; i++) {
            pop.timeStep(t, new DailyStats(t));
            t = t.advance();

            // We only check hospitals since they 1. support furlough, 2. aren't visitable by staff members
            for (Hospital plc : pop.getPlaces().getHospitals()) {
                for (Person p : plc.getPeople()) {
                    if (p.getPrimaryCommunalPlace() == plc && !p.isHospitalised()) {
                        assertFalse(p.isFurloughed());
                    }
                }
            }
        }
    }
}
