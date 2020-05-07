package uk.co.ramp.covid.simulation.place;

public class Nursery extends CommunalPlace {
    public Nursery(int cIndex) {
        super(cIndex);
        this.transProb = super.transProb * 30d / (34000d / 50d);
    }
}
