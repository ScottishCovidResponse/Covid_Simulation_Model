package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.output.DailyStats;

public class Pensioner extends Person {
    
    public Pensioner(int age, Sex sex) {
        super(age, sex);
        if (age < 65) {
            throw new InvalidAgeException("Trying to create a pensioner outside the correct age range (65+)");
        }
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.pensionerInfected.increment();
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.pensionerDeaths.increment();
    }

    @Override
    public void allocateCommunalPlace(Places p) {}

    @Override
    public boolean avoidsPhase2(double testP) {
        return testP > CovidParameters.get().diseaseParameters.pensionerProgressionPhase2;
    }

    @Override
    protected double getInfectionSeedInitial() {
        return CovidParameters.get().infectionSeedProperties.InitialSeedInfantChildPensioner;
    }
}
