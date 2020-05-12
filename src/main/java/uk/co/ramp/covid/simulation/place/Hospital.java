package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class Hospital extends CommunalPlace {

    public Hospital(int cindex) {
        super(cindex);
        this.transProb = PopulationParameters.get().getpHospitalTrans();
        this.startDay = 3; //Bodge set start day to a different day of the week to help syncing
        this.endDay = 7;
        this.keyProb = PopulationParameters.get().getpHospitalKey();
        if (Math.random() > this.keyProb) this.keyPremises = true;
    }

}
