package uk.co.ramp.covid.simulation.place;

public class Office extends CommunalPlace {
    public Office(int cindex) {
        super(cindex);
        this.transProb = super.transProb * (10 / (10000 / 400));
        this.keyProb = 0.5;
        if (Math.random() > this.keyProb) this.keyPremises = true;
        //	System.out.println(this.keyPremises);
    }

}
