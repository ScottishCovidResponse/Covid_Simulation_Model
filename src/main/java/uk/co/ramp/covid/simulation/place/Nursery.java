package uk.co.ramp.covid.simulation.place;

public class Nursery extends CommunalPlace {
    public Nursery(int cindex) {
        super(cindex);
        this.transProb = super.transProb * 30d / (34000d / 50d);
    }
}
