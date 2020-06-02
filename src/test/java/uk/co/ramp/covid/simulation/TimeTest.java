package uk.co.ramp.covid.simulation;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeTest {

    @Test
    public void zeroTimeInitialisedCorrectly() {
        Time t = new Time(0);
        assertEquals(0, t.getDay());
        assertEquals(0, t.getAbsDay());
        assertEquals(0, t.getHour());
    }

    @Test
    public void midDayTimeInitialisedCorrectly() {
        Time t = new Time(12);
        assertEquals(0, t.getDay());
        assertEquals(0, t.getAbsDay());
        assertEquals(12, t.getHour());
    }

    @Test
    public void nonFirstDayTimeInitialisedCorrectly() {
        Time t = new Time(26);
        assertEquals(1, t.getDay());
        assertEquals(1, t.getAbsDay());
        assertEquals(2, t.getHour());
    }

    @Test
    public void weekRolleroverWorks() {
        Time t = new Time(168);
        assertEquals(0, t.getDay());
        assertEquals(7, t.getAbsDay());
        assertEquals(0, t.getHour());
        Time t2 = t.advance();
        assertEquals(0, t2.getDay());
        assertEquals(7, t2.getAbsDay());
        assertEquals(1, t2.getHour());
    }


    @Test
    public void advance() {
        Time t = new Time(0);
        Time t2 = t.advance();

        assertEquals(0, t2.getDay());
        assertEquals(0, t2.getAbsDay());
        assertEquals(1, t2.getHour());
    }


    @Test
    public void advanceOverDays() {
        Time t = new Time(23);
        Time t2 = t.advance();

        assertEquals(1, t2.getDay());
        assertEquals(0, t2.getHour());
        assertEquals(1, t2.getAbsDay());
    }
}