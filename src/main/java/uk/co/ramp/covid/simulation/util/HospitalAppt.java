package uk.co.ramp.covid.simulation.util;

public class HospitalAppt {
    
    private final Time startTime;
    private final int lengthInHours;
    
    public HospitalAppt(Time start, int length) {
        startTime = start;
        lengthInHours = length;
    }

    public Time getStartTime() {
        return startTime;
    }
    
    public boolean isOver(Time t) {
        return t.getAbsTime() - startTime.getAbsTime() >= lengthInHours;
    }
}
