package uk.co.ramp.covid.simulation.place;

public class Hospital extends CommunalPlace {

    public Hospital(int cindex) {
        super(cindex);
        this.transProb = super.transProb * (15 / (5000 / 10));
        this.startDay = 3; //Bodge set start day to a different day of the week to help syncing
        this.endDay = 7;
        this.keyProb = 0.0;
        if (Math.random() > this.keyProb) this.keyPremises = true;
    }

}
