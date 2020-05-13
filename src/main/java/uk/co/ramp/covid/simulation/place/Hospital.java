package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class Hospital extends CommunalPlace {

    public Hospital(int cindex) {
        super(cindex);
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpHospitalTrans();
        startDay = 3; //Bodge set start day to a different day of the week to help syncing
        endDay = 7;
        keyProb = PopulationParameters.get().getpHospitalKey();
        if (Math.random() > keyProb) keyPremises = true;
    }

}
