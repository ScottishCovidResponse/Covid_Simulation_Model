package uk.co.ramp.covid.simulation.imported.utils;


import java.util.Map;
import java.util.Objects;

import static uk.co.ramp.covid.simulation.imported.utils.VirusStatus.*;


public class SeirRecord {

    private final int time;
    private final int s;
    private final int e;
    private final int i;
    private final int r;

    public SeirRecord(int time, Map<VirusStatus, Integer> seirCounts) {

        this.time = time;
        this.s = seirCounts.get(SUSCEPTIBLE);
        this.e = seirCounts.get(EXPOSED);
        this.i = seirCounts.get(INFECTED);
        this.r = seirCounts.get(RECOVERED);

    }


    public SeirRecord(final int time, final int s, final int e, final int i, final int r) {

        this.time = time;
        this.s = s;
        this.e = e;
        this.i = i;
        this.r = r;

    }

    public int getTime() {
        return time;
    }

    public int getS() {
        return s;
    }

    public int getE() {
        return e;
    }

    public int getI() {
        return i;
    }

    public int getR() {
        return r;
    }

    @Override
    public String toString() {
        return "SeirRecord{" +
                "time=" + time +
                ", s=" + s +
                ", e=" + e +
                ", i=" + i +
                ", r=" + r +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeirRecord that = (SeirRecord) o;
        return time == that.time &&
                s == that.s &&
                e == that.e &&
                i == that.i &&
                r == that.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, s, e, i, r);
    }
}
