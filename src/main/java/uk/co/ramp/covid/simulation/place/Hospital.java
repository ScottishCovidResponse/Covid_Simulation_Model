package uk.co.ramp.covid.simulation.place;

public class Hospital extends CommunalPlace {

    public Hospital(int cIndex) {
        super(cIndex);
        this.transProb = super.transProb * (15d / (5000d / 10d));
        this.startDay = 3; //Bodge set start day to a different day of the week to help syncing
        this.endDay = 7;
        this.keyProb = 0.0;
        if (Math.random() > this.keyProb) this.keyPremises = true;
    }

}
