package uk.co.ramp.covid.simulation.output.network;

import uk.co.ramp.covid.simulation.place.Place;
import uk.co.ramp.covid.simulation.population.Person;

public class ContactPairWithLocation implements Comparable<ContactPairWithLocation> {
    private final int person1;
    private final int person2;
    private final String location;
    
    public int getPerson1() { return person1; }
    public int getPerson2() { return person2; }
    public String getLocation() { return location; }
    
    public ContactPairWithLocation(Person a, Person b, Place place) {
        if (a.getID() < b.getID()) {
            person1 = a.getID();
            person2 = b.getID();
        } else {
            person1 = b.getID();
            person2 = a.getID();
        }
        location = place.getClass().getSimpleName();
    }
    
    @Override
    public int hashCode() {
        return (person1 + "-" + person2 + location).hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ContactPairWithLocation))
            return false;
        
        ContactPairWithLocation other = (ContactPairWithLocation)o;
        return this.person1 == other.person1
                && this.person2 == other.person2
                && this.location.equals(other.location);
    }

    @Override
    public int compareTo(ContactPairWithLocation other) {
        if (this.person1 != other.person1) {
            return Integer.compare(this.person1, other.person1);
        } if (this.person2 != other.person2) {
            return Integer.compare(this.person2, other.person2);
        } else {
            return this.location.compareTo(other.location);
        }
    }
}
