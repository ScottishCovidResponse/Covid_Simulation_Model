package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.output.DailyStats;

public class Child extends Person {

    public Child(int age, Sex sex) {
        super(age, sex);
        if (age >= 18 || age < 5) {
            throw new InvalidAgeException("Trying to create a child outside the correct age range (5-17)");
        }
        shifts = Shifts.schoolTimes();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsChild();
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incDeathsChild();
    }

    // All children go to school
    @Override
    public void allocateCommunalPlace(Places p) {
        setPrimaryPlace(p.getRandomSchool());
    }

    @Override
    public boolean avoidsPhase2(double testP) {
        return testP > CovidParameters.get().diseaseParameters.childProgressionPhase2;
    }

    // Children don't get tested
    @Override
    public void getTested() { }

    @Override
    protected double getInfectionSeedRate() {
        return CovidParameters.get().infectionSeedProperties.infectionRateIncreaseInfantChild;
    }
}
