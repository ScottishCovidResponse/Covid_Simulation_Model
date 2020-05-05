package uk.co.ramp.covid.simulation.imported.utils;

public class ContactRecord {

    private final int time;
    private final int from;
    private final int to;
    private final double weight;

    public ContactRecord(int time, int from, int to, double weight) {
        this.time = time;
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public int getTime() {
        return time;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }
}
