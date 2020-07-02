package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.population.Child;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.List;

public class SchoolEasingEvent extends CommunalPlaceEasingEvent {
    
    private Probability pAttendsSchool = null;

    public SchoolEasingEvent(Time s, Population p, Probability keyPremises, Double socialDistance,
                             Probability visitAdjustment) {
        super(s, p, keyPremises, socialDistance);
        this.pAttendsSchool = visitAdjustment;
    }

    @Override
    protected void apply() {
        super.apply();
        
        // Handle reduced students
        for (Person p : population.getAllPeople()) {
            if (p instanceof Child) {
                // Furlough everyone and un-furlough visitAdjustment % of the children to return
                p.furlough();
                if (pAttendsSchool.sample()) {
                    p.unFurlough();
                }
            }
        }
    }

    @Override
    protected String getName() {
        return "SchoolEasing";
    }

    @Override
    protected List<? extends CommunalPlace> getPlaces() {
        return population.getPlaces().getSchools();
    }

    @Override
    protected boolean isValid() {
        return super.isValid() && pAttendsSchool != null;
    }
}
