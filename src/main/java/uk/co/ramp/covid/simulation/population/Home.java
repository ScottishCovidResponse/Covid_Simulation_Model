package uk.co.ramp.covid.simulation.population;

/** Captures home-like functionality for CareHomes/Households */
public interface Home {
    void isolate();
    void stopIsolating();
    void addPersonNext(Person person);
}
