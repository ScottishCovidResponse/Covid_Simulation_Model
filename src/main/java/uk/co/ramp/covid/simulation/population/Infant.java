package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;

public class Infant extends Person {

    private boolean goesToNursery = false;

    public Infant(int age, Sex sex) {
        super(age, sex);
        if (age >= 5) {
            throw new InvalidAgeException("Trying to create an infant outside the correct age range (0-4)");
        }
        setNursery();
        shifts = Shifts.schoolTimes();
    }

    private void setNursery() {
        if (PopulationParameters.get().infantProperties.pAttendsNursery.sample()) {
            goesToNursery = true;
        }
    }

    public boolean isGoesToNursery() {
        return goesToNursery;
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.infantInfected.increment();
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.infantDeaths.increment();
    }

    @Override
    public void allocateCommunalPlace(Places p) {
        if (goesToNursery) {
            setPrimaryPlace(p.getRandomNursery());
        }

    }

    @Override
    public boolean avoidsPhase2(double testP) {
        return testP > CovidParameters.get().diseaseParameters.childProgressionPhase2;
    }

    // Infants don't get tested
    @Override
    public void getTested() { }

    @Override
    protected double getInfectionSeedInitial() {
        return CovidParameters.get().infectionSeedProperties.initialSeedInfantChildPensioner;
    }
}
