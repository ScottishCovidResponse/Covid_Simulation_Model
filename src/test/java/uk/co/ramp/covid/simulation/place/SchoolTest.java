package uk.co.ramp.covid.simulation.place;


import org.junit.Test;
import uk.co.ramp.covid.simulation.util.DateRange;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static uk.co.ramp.covid.simulation.population.Person.Sex.FEMALE;
import static uk.co.ramp.covid.simulation.population.Person.Sex.MALE;

public class SchoolTest extends SimulationTest {

    @Test
    public void testSchoolTransProb(){
        School school = new School(CommunalPlace.Size.MED);
        double expProb = PopulationParameters.get().buildingProperties.baseTransmissionConstant;
        double delta = 0.01;
        assertEquals("Unexpected school TransProb", expProb, school.transConstant, delta);
    }

    @Test
    public void testSchoolWorkers() throws ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        Population p = PopulationGenerator.genValidPopulation(populationSize);
        p.allocatePeople();
        List<Person> staff;
        Time t = new Time(0);
        //Run for a whole week
        for (int day = 0; day < 7; day++) {
            int totStaff;
            int startTime = Shifts.schoolTimes().getShift(day).getStart();
            int endTime = Shifts.schoolTimes().getShift(day).getEnd();
            DailyStats s = new DailyStats(t);
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                t = t.advance();
                totStaff = 0;
                for (School place : p.getPlaces().getSchools()) {
                    staff = place.getStaff(t);
                    totStaff += staff.size();
                }

                if (day < 5) {

                    //Staff should be at school during school times only
                    if (i + 1 < startTime || i + 1 >= endTime) {
                        assertEquals("Unexpected staff at school", 0, totStaff);
                    } else {
                        assertTrue("No staff at school", totStaff > 0);
                    }
                } else {
                    //Staff should not be at school on weekends
                    assertEquals("Unexpected staff at school", 0, totStaff);
                }
            }

        }
    }
    
    private void checkSchoolsPopulated(Population p) {
        for (School s : p.getPlaces().getSchools()) {
            assertNotEquals(0, s.getNumPeople());
        }
    }

    private void checkSchoolsNotPopulated(Population p) {
        for (School s : p.getPlaces().getSchools()) {
            assertEquals(0, s.getNumPeople());
        }
    }

    @Test
    public void testSendHome() {
        School school = new School(CommunalPlace.Size.MED);
        school.addPerson(new Child(10, FEMALE));
        school.addPerson(new Child(5, MALE));
        int time = school.times.getClose() - 1;
        school.determineMovement(new Time(time),  false, null);
        school.commitMovement();
        int expPeople = 0;
        assertEquals("Unexpected children left in school", expPeople, school.getNumPeople());
    }

    @Test
    public void schoolsCanTakeHolidays() {
        List<DateRange> holidays = new ArrayList<>();

        // Holidays are non-inclusive
        holidays.add(new DateRange(Time.timeFromDay(2), Time.timeFromDay(3)));
        holidays.add(new DateRange(Time.timeFromDay(4), Time.timeFromDay(5)));
        PopulationParameters.get().buildingProperties.schoolHolidays = holidays;
        
        int populationSize = 10000;
        Population p = PopulationGenerator.genValidPopulation(populationSize);
        
        // Schools are open at midday so we sample then
        Time t = new Time(0);
        for (int i = 0; i < 12; i++) {
            p.timeStep(t, new DailyStats(t));
            t = t.advance();
        }
        checkSchoolsPopulated(p);

        // Day 1 - open
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, new DailyStats(t));
            t = t.advance();
        }
        checkSchoolsPopulated(p);

        // Day 2 - holiday
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, new DailyStats(t));
            t = t.advance();
        }
        checkSchoolsNotPopulated(p);

        // Day 3 - open
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, new DailyStats(t));
            t = t.advance();
        }
        checkSchoolsPopulated(p);

        // Day 4 - holiday
        for (int i = 0; i < 24; i++) {
            p.timeStep(t, new DailyStats(t));
            t = t.advance();
        }
        checkSchoolsNotPopulated(p);
    }

}
