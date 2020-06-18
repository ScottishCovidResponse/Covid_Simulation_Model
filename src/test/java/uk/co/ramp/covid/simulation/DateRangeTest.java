package uk.co.ramp.covid.simulation;

import org.junit.Test;

import static org.junit.Assert.*;

public class DateRangeTest {
    
    @Test
    public void inRange() {
        DateRange d = new DateRange( Time.timeFromDay(1),Time.timeFromDay(3));
        assertTrue(d.inRange(Time.timeFromDay(1)));
        assertTrue(d.inRange(Time.timeFromDay(2)));
        assertFalse(d.inRange(Time.timeFromDay(3)));
        assertFalse(d.inRange(Time.timeFromDay(0)));
        assertFalse(d.inRange(Time.timeFromDay(4)));
        assertFalse(d.inRange(Time.timeFromDay(3).advance()));
    }


}