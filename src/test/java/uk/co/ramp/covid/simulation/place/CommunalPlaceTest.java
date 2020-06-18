package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.ImpossibleWorkerDistributionException;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;


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
    public void testAllPlaces() {
        //Run for 7 days
        for (int day = 0; day < 7; day++) {
            for (int i = 0; i < 24; i++) {
                p.timeStep(t, s);
                t = t.advance();
                for (CommunalPlace place : p.getPlaces().getAllPlaces()) {
                    // Get place opening and closing time
                    int open = place.getTimes().getOpen();
                    int close = place.getTimes().getClose();

                    if (place instanceof Shop || place instanceof Restaurant) {
                        testShopOrRestaurant(place, open, close, day);
                    } else if (place instanceof Hospital){
                        testHospital(place, day);
                    } else if (place instanceof CareHome){
                        testCarehome(place, open, close, day);
                    } else {
                        testOfficeHours(place, open, close, day);
                    }
                }
            }
        }
    }

    private void testShopOrRestaurant(CommunalPlace place, int open, int close, int day) {
        //Test if shop or restaurant is open or closed
        if (t.getHour() >= open && t.getHour() < close) {
            assertTrue("Day " + day + " Time " + t.getHour() + " " + place.toString() + " unexpectedly closed", place.isOpen(t));
        } else {
            assertFalse("Day " + day + " Time " + t.getHour() + " " + place.toString() + " unexpectedly open", place.isOpen(t));
        }

        //Test if shop or restaurant is open to visitors in the next hour
        if (t.getHour() >= open - 1 && t.getHour() < close - 1) {
            assertTrue("Day " + day + " Time " + t.getHour() + " " + place.toString() + " unexpectedly closed to visitors in next hour", place.isVisitorOpenNextHour(t));
        } else {
            assertFalse("Day " + day + " Time " + t.getHour() + " " + place.toString() + " unexpectedly open to visitors in next hour", place.isVisitorOpenNextHour(t));
        }
    }

    private void testOfficeHours(CommunalPlace place, int open, int close, int day) {
        //Test if place with 9-5 hours is open or closed
        if (t.getHour() >= open && t.getHour() < close && day < 5) {
            assertTrue("Day " + day + " Time " + t.getHour() + " " + place.toString() + " unexpectedly closed", place.isOpen(t));
        } else {
            assertFalse("Day " + day + " Time " + t.getHour() + " " + place.toString() + " unexpectedly open", place.isOpen(t));
        }
    }


    private void testCarehome(CommunalPlace place, int open, int close, int day) {
        if (t.getHour() >= open && t.getHour() < close) {
            assertTrue("Day " + day + " Time " + t.getHour() + " " + place.toString() + " unexpectedly closed", place.isOpen(t));
        } else {
            assertFalse("Day " + day + " Time " + t.getHour() + " " + place.toString() + " unexpectedly open", place.isOpen(t));
        }
    }

    private void testHospital(CommunalPlace place, int day) {
        // Hospitals should always be open
        assertTrue("Day " + day + " Time " + t.getHour() + " Hospital unexpectedly closed", place.isOpen(t));
    }
}