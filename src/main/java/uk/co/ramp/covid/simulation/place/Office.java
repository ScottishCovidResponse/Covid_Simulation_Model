package uk.co.ramp.covid.simulation.place;

public class Office extends CommunalPlace {
    public Office(int cIndex) {
        super(cIndex);
        this.transProb = super.transProb * (10d / (10000d / 400d));
        this.keyProb = 0.5;
        if (Math.random() > this.keyProb) this.keyPremises = true;
    }

}
