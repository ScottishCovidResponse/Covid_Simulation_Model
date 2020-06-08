package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;

public class Infant extends Person {

    private boolean goesToNursery;

    public Infant(int age, Sex sex) {
        super(age, sex);
        if (age >= 5) {
            throw new InvalidAgeException("Trying to create an infant outside the correct age range (0-4)");
        }
        setNursery();
        shifts = Shifts.schoolTimes();
    }

    private void setNursery() {
        if (rng.nextUniform(0, 1) < PopulationParameters.get().infantAllocation.pAttendsNursery.asDouble()) {
            goesToNursery = true;
        } else {
            goesToNursery = false;
        }
    }

    public boolean isGoesToNursery() {
        return goesToNursery;
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsInfant();
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incDeathsInfant();
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
}
