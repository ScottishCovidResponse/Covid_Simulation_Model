package uk.co.ramp.covid.simulation.place;

public class School extends CommunalPlace {
    public School(int cindex) {
        super(cindex);
        int startTime = 9;
        int endTime = 15;
        this.transProb = super.transProb * 30 / (34000 / 50); // These transmission probabilities are long winded, but they do make sense
        //	this.keyPremises = false;
    }
}
