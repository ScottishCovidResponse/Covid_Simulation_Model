package uk.co.ramp.covid.simulation.util;

import uk.co.ramp.covid.simulation.place.Hospital;

public class HospitalAppt {
    
    private final Time startTime;
    private final int lengthInHours;
    private final Hospital hospital;
    
    public HospitalAppt(Time start, int length, Hospital h) {
        startTime = start;
        lengthInHours = length;
        hospital = h;
    }

    public Hospital getApptLocation() { return hospital; }

    public Time getStartTime() {
        return startTime;
    }
    
    public boolean isOver(Time t) {
        return t.getAbsTime() - startTime.getAbsTime() >= lengthInHours;
    }
    
    public boolean isOccurring(Time t) {
        return t.getAbsTime() >= startTime.getAbsTime() && !isOver(t);
    }
}
