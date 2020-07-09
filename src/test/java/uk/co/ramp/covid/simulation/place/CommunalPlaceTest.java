package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.ImpossibleWorkerDistributionException;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
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
        p.setPostHourHook((pop, time) -> {
            for (CommunalPlace place : pop.getPlaces().getCommunalPlaces()) {
                // Get place opening and closing time
                int open = place.getTimes().getOpen();
                int close = place.getTimes().getClose();

                if (place instanceof Shop || place instanceof Restaurant) {
                    testShopOrRestaurant(place, open, close, t.getDay());
                } else if (place instanceof Hospital){
                    testHospital(place, t.getDay());
                } else if (place instanceof CareHome){
                    testCarehome(place, open, close, t.getDay());
                } else {
                    testOfficeHours(place, open, close, t.getDay());
                }
            }
        });
        p.simulate(7);
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