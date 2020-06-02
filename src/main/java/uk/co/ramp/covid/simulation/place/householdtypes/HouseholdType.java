package uk.co.ramp.covid.simulation.place.householdtypes;

public abstract class HouseholdType {
    protected int adults = 0;
    protected int children = 0;
    protected int pensioners = 0;
    
    public void addAdult() {
        adults++;
    }
    
    public void addChild() {
        children++;
    }
    
    public void addPensioner() {
        pensioners++;
    }
    
    public abstract boolean adultRequired();
    public abstract boolean adultAllowed();
    public abstract boolean childRequired();
    public abstract boolean childAllowed();
    public abstract boolean pensionerRequired();
    public abstract boolean pensionerAllowed();
    
}
