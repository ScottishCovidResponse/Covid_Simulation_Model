package uk.co.ramp.covid.simulation;

public class DateRange {
    final Time start;
    final Time end;
    
    public DateRange(Time start, Time end) {
        this.start = start;
        this.end = end;
    }
    
    public boolean inRange(Time t) {
        return start.compareTo(t) <= 0 && end.compareTo(t) > 0;
    }

    public Time getStart() {
        return start;
    }

    public Time getEnd() {
        return end;
    }
}
