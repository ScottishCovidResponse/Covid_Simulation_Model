package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.ImpossibleWorkerDistributionException;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.util.SimulationTest;


import static org.junit.Assert.*;

public class CommunalPlaceTest extends SimulationTest {

    int populationSize;
    Population p;
    Time t;
    DailyStats s;


    @Before
    public void setup() throws ImpossibleWorkerDistributionException {
        populationSize = 10000;
        p = PopulationGenerator.genValidPopulation(populationSize);
        p.allocatePeople();
        t = new Time(0);
        s = new DailyStats(t);
    }

    @Test
    public void testIsShopVisitorOpen() {
        //Run for 7 days
        for (int day = 0; day < 7; day++) {
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                t = t.advance();
                for (Shop shop : p.getPlaces().getShops()) {
                    // Get shop opening and closing time
                    int open = shop.getTimes().getOpen();
                    int close = shop.getTimes().getClose();
                    //Test if shop is open or closed
                    if (t.getHour() >= open && t.getHour() < close) {
                        assertTrue("Day " + day + " Time " + t.getHour() + " shop unexpectedly closed", shop.isOpen(day, t.getHour()));
                    } else {
                        assertFalse("Day " + day + " Time " + t.getHour() + " shop unexpectedly open", shop.isOpen(day, t.getHour()));
                    }

                    //Test if shop is open to visitors in the next hour
                    if (t.getHour() >= open - 1 && t.getHour() < close - 1) {
                        assertTrue("Day " + day + " Time " + t.getHour() + " shop unexpectedly closed to visitors in next hour", shop.isVisitorOpenNextHour(t));
                    } else {
                        assertFalse("Day " + day + " Time " + t.getHour() + " shop unexpectedly open to visitors in next hour", shop.isVisitorOpenNextHour(t));
                    }
                }
            }
        }
    }

    @Test
    public void testIsRestaurantVisitorOpen() {
        //Run for 7 days
        for (int day = 0; day < 7; day++) {
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                t = t.advance();
                for (Restaurant restaurant : p.getPlaces().getRestaurants()) {
                    // Get restaurant opening and closing time
                    int open = restaurant.getTimes().getOpen();
                    int close = restaurant.getTimes().getClose();
                    //Test if restaurant is open or closed
                    if (t.getHour() >= open && t.getHour() < close) {
                        assertTrue("Day " + day + " Time " + t.getHour() + " restaurant unexpectedly closed", restaurant.isOpen(day, t.getHour()));
                    } else {
                        assertFalse("Day " + day + " Time " + t.getHour() + " restaurant unexpectedly open", restaurant.isOpen(day, t.getHour()));
                    }

                    //Test if restaurant is open to visitors in the next hour
                    if (t.getHour() >= open - 1 && t.getHour() < close - 1) {
                        assertTrue("Day " + day + " Time " + t.getHour() + " restaurant unexpectedly closed to visitors in next hour", restaurant.isVisitorOpenNextHour(t));
                    } else {
                        assertFalse("Day " + day + " Time " + t.getHour() + " restaurant unexpectedly open to visitors in next hour", restaurant.isVisitorOpenNextHour(t));
                    }
                }
            }
        }
    }


    @Test
    public void testIsOfficeOpen() {
        //Run for 7 days
        for (int day = 0; day < 7; day++) {
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                t = t.advance();
                for (Office office : p.getPlaces().getOffices()) {
                    // Get office opening and closing time
                    int open = office.getTimes().getOpen();
                    int close = office.getTimes().getClose();
                    //Test if office is open or closed
                    if (t.getHour() >= open && t.getHour() < close && day < 5) {
                        assertTrue("Day " + day + " Time " + t.getHour() + " office unexpectedly closed", office.isOpen(day, t.getHour()));
                    } else {
                        assertFalse("Day " + day + " Time " + t.getHour() + " office unexpectedly open", office.isOpen(day, t.getHour()));
                    }
                }
            }
        }
    }

    @Test
    public void testIsConstructionSiteOpen() {
        //Run for 7 days
        for (int day = 0; day < 7; day++) {
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                t = t.advance();
                for (ConstructionSite cs : p.getPlaces().getConstructionSites()) {
                    // Get Construction Site opening and closing time
                    int open = cs.getTimes().getOpen();
                    int close = cs.getTimes().getClose();
                    //Test if Construction Site is open or closed
                    if (t.getHour() >= open && t.getHour() < close && day < 5) {
                        assertTrue("Day " + day + " Time " + t.getHour() + " Construction Site unexpectedly closed", cs.isOpen(day, t.getHour()));
                    } else {
                        assertFalse("Day " + day + " Time " + t.getHour() + " Construction Site unexpectedly open", cs.isOpen(day, t.getHour()));
                    }
                }
            }
        }
    }

    @Test
    public void testIsSchoolOpen() {
        //Run for 7 days
        for (int day = 0; day < 7; day++) {
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                t = t.advance();
                for (School school : p.getPlaces().getSchools()) {
                    // Get school opening and closing time
                    int open = school.getTimes().getOpen();
                    int close = school.getTimes().getClose();
                    //Test if school is open or closed
                    if (t.getHour() >= open && t.getHour() < close && day < 5) {
                        assertTrue("Day " + day + " Time " + t.getHour() + " school unexpectedly closed", school.isOpen(day, t.getHour()));
                    } else {
                        assertFalse("Day " + day + " Time " + t.getHour() + " school unexpectedly open", school.isOpen(day, t.getHour()));
                    }
                }
            }
        }
    }

    @Test
    public void testIsNurseryOpen() {
        //Run for 7 days
        for (int day = 0; day < 7; day++) {
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                t = t.advance();
                for (Nursery nursery : p.getPlaces().getNurseries()) {
                    // Get nursery opening and closing time
                    int open = nursery.getTimes().getOpen();
                    int close = nursery.getTimes().getClose();
                    //Test if nursery is open or closed
                    if (t.getHour() >= open && t.getHour() < close && day < 5) {
                        assertTrue("Day " + day + " Time " + t.getHour() + " nursery unexpectedly closed", nursery.isOpen(day, t.getHour()));
                    } else {
                        assertFalse("Day " + day + " Time " + t.getHour() + " nursery unexpectedly open", nursery.isOpen(day, t.getHour()));
                    }
                }
            }
        }
    }

    @Test
    public void testIsHospitalOpen() {
        //Run for 7 days
        for (int day = 0; day < 7; day++) {
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                t = t.advance();
                for (Hospital place : p.getPlaces().getHospitals()) {
                    // Hospitals should always be open
                    assertTrue("Day " + day + " Time " + t.getHour() + " Hospital unexpectedly closed", place.isOpen(day, t.getHour()));
                }
            }
        }
    }
}