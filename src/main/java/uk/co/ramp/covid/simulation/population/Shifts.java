package uk.co.ramp.covid.simulation.population;

import java.util.HashMap;
import java.util.Map;

/** Class to track the shifts people are at their primary places (i.e. nursery visit times are also shifts) */
public class Shifts {

    public static class Shift {
        private int start;
        private int end;

        public Shift(int s, int e) {
            start = s;
            end = e;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }
    }

    private Map<Integer, Shift> shifts;

    public Shifts() {
        shifts = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            shifts.put(i, new Shift(0,0));
        }
    }

    public Shifts(int s, int e, int... days) {
        this();
        for (int i = 0; i < days.length; i++) {
            shifts.put(days[i], new Shift(s, e));
        }
    }

    public Shift getShift(int day) {
        return shifts.get(day);
    }

    // Common shift patterns
    private static Shifts nineFiveFiveDays;
    public static Shifts nineFiveFiveDays() {
        if (nineFiveFiveDays == null) {
            nineFiveFiveDays = new Shifts(9, 17, 0, 1, 2, 3, 4);
        }
        return nineFiveFiveDays;
    }

    private static Shifts schoolTimes;
    public static Shifts schoolTimes() {
        if (schoolTimes == null) {
            schoolTimes = new Shifts(9,17, 0, 1, 2, 3, 4);

        }
        return schoolTimes;
    }

}