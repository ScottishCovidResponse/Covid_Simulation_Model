package uk.co.ramp.covid.simulation.place;

public class ConstructionSite extends CommunalPlace {

    public ConstructionSite(int cIndex) {
        super(cIndex);
        this.transProb = super.transProb * 10d / (5000d / 100d);
        this.keyProb = 0.5;
        if (Math.random() > this.keyProb) this.keyPremises = true;

    }
}
