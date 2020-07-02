package uk.co.ramp.covid.simulation.integrationTests;

import org.junit.Ignore;
import org.junit.Test;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.population.Child;
import uk.co.ramp.covid.simulation.place.Hospital;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Time;

import static org.junit.Assert.*;

public class LockdownTests extends SimulationTest  {

    @Ignore("Refactor to new lockdown system")
    @Test
    public void furloughedStaffDontGoToWork() {
        final int simDays = 7;
        final int populationSize = 20000;
       
        Population pop = PopulationGenerator.genValidPopulation(populationSize);

        //pop.setLockdown(2,4,0.5);

        pop.setPostHourHook((population, time) -> {
            // Skip the first hour lockdown starts since people might still be at
            // work as they get the message they need to leave
            if (time.getDay() == 2 && time.getHour() == 1) {
                return;
            }

            for (Hospital plc : population.getPlaces().getNonCovidHospitals()) {
                for (Person p : plc.getPeople()) {
                    if (p.getPrimaryCommunalPlace() == plc
                            && !p.isHospitalised()
                            && !(p.hasHospitalAppt() && p.getHospitalAppt().isOccurring(time))) {
                            assertFalse(p.isFurloughed());
                    }
                }
            }
        });

        pop.simulate(simDays);
    }

    @Ignore("Refactor to new lockdown system")
    @Test
    public void testSchoolToggle() {
        final int populationSize = 20000;
        Population pop = PopulationGenerator.genValidPopulation(populationSize);


//        pop.getLockdownController().addComponent(
//                new SchoolToggleComponent(Time.timeFromDay(0),Time.timeFromDay(14), pop)
//        );

        // First week only even aged kids go to school
        // TODO: refactor with test hook
        Time t = new Time(0);
        for (int i = 0; i < 7; i++) {
           for (int j = 0; j < 24; j++) {
               pop.getLockdownController().implementLockdown(t);
               pop.timeStep(t, new DailyStats(t));
               t = t.advance();
               
               for (School s : pop.getPlaces().getSchools()) {
                    for (Person p : s.getPeople()) {
                        if (p instanceof Child) {
                            assertEquals(0, p.getAge() % 2);
                        }
                    }
               }
           }
        }

        // Odd aged kids go to school second week
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 24; j++) {
                pop.getLockdownController().implementLockdown(t);
                pop.timeStep(t, new DailyStats(t));
                t = t.advance();

                for (School s : pop.getPlaces().getSchools()) {
                    for (Person p : s.getPeople()) {
                        if (p instanceof Child) {
                            assertNotEquals(0, p.getAge() % 2);
                        }
                    }
                }
            }
        }
        
    }
}
