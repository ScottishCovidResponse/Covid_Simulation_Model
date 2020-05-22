package uk.co.ramp.covid.simulation.place;

import java.util.BitSet;

/** This class tracks the opening/closing times of the various places */
public class OpeningTimes {
    private int open;
    private int close;

    // Some places may only be open to visitors during the day, e.g. re-stocking at night
    private int visitorOpen;
    private int visitorClose;

    // 0 index is Monday; 0011111 => weekday only, 1100000 => weekend only etc.
    private BitSet openDays;

    OpeningTimes(int open, int close, int vopen, int vclose, BitSet days) {
        this.open = open;
        this.close = close;
        this.visitorOpen = vopen;
        this.visitorClose = vclose;
        this.openDays = days;
    }

    OpeningTimes(int open, int close, BitSet days) {
        this(open, close, open, close, days);
    }
    
    public boolean isOpen(int time, int day) {
        return time >= open && time < close && openDays.get(day);
    }

    public boolean isOpenToVisitors(int time, int day) {
        return time >= visitorOpen && time < visitorClose && openDays.get(day);
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getClose() {
        return close;
    }

    public void setClose(int close) {
        this.close = close;
    }

    public int getVisitorOpen() {
        return visitorOpen;
    }

    public void setVisitorOpen(int visitorOpen) {
        this.visitorOpen = visitorOpen;
    }

    public int getVisitorClose() {
        return visitorClose;
    }

    public void setVisitorClose(int visitorClose) {
        this.visitorClose = visitorClose;
    }

    public BitSet getOpenDays() {
        return openDays;
    }

    public void setOpenDays(BitSet openDays) {
        this.openDays = openDays;
    }

    private static BitSet weekdays;
    private static BitSet weekend;
    private static BitSet allDays;
    
    public static BitSet getWeekdays() {
        if (weekdays == null) {
            weekdays = new BitSet();
            weekdays.set(0, 4);
        }
        return weekdays;
    }
    
    public static BitSet getWeekend() {
        if (weekend == null) {
            weekend = new BitSet();
            weekend.set(5, 6);
        }
        return weekend;
    }

    public static BitSet getAllDays() {
        if (allDays == null) {
            allDays = new BitSet();
            allDays.set(0, 6);
        }
        return allDays;
    }
}
