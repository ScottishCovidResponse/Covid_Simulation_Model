package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Places;

import java.util.HashSet;
import java.util.Set;

public abstract class HouseholdType {
    protected int adults = 0;
    protected int children = 0;
    protected int pensioners = 0;
    protected Set<Person> people = new HashSet<>();
    
    public void addAdult(Person p) {
        people.add(p);
        adults++;
    }
    
    public void addChild(Person p) {
        people.add(p);
        children++;
    }
    
    public void addPensioner(Person p) {
        people.add(p);
        pensioners++;
    }
    
    public abstract boolean adultRequired();
    public abstract boolean adultAllowed();
    public abstract boolean childRequired();
    public abstract boolean childAllowed();
    public abstract boolean pensionerRequired();
    public abstract boolean pensionerAllowed();

    public Household toHousehold(Places places) {
        Household h = new Household(places);
        people.forEach(h::addInhabitant);
        return h;
    }
}
