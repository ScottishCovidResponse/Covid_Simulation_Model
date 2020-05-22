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
    }

    private Map<Integer, Shift> shifts;

    public Shifts() {
        shifts = new HashMap<>();
    }

    public Shifts(Map<Integer, Shift> s) {
        shifts = s;
    }

    public Shift getShift(int day) {
        return shifts.get(day);
    }

    // Common shift patterns
    private static Shifts nineFive;
    public static Shifts getNineFive () {
        if (nineFive == null) {
            nineFive = new Shifts();
            Shift s = new Shift(9, 17);
            for (int i = 0; i < 7; i++) {
                nineFive.shifts.put(0, s);
            }
        }
        return nineFive;
    }

    private static Shifts schoolTimes;
    public static Shifts getSchoolTimes() {
        if (schoolTimes == null) {
            schoolTimes = new Shifts();
            Shift s = new Shift(9, 15);
            for (int i = 0; i < 5; i++) {
                schoolTimes.shifts.put(0, s);
            }
        }
        return schoolTimes;
    }
    
    // For backwards compatibility only
    private static Shifts allTimes;
    public static Shifts getAllTimes () {
        if (allTimes == null) {
            allTimes = new Shifts();
            Shift s = new Shift(0, 23);
            for (int i = 0; i < 7; i++) {
                allTimes.shifts.put(0, s);
            }
        }
        return allTimes;
    }
}
