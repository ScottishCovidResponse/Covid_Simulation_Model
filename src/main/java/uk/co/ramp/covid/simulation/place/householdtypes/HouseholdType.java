package uk.co.ramp.covid.simulation.place.householdtypes;

public abstract class HouseholdType {
    int nadults = 0;
    int nchildren = 0;
    int npensioners = 0;
    
    public void addAdult() {
        nadults++;
    }
    
    public void addChild() {
        nchildren++;
    }
    
    public void addPensioner() {
        npensioners++;
    }
    
    public abstract boolean adultRequired();
    public abstract boolean adultAllowed();
    public abstract boolean childRequired();
    public abstract boolean childAllowed();
    public abstract boolean pensionerRequired();
    public abstract boolean pensionerAllowed();
    
}
