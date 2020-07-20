package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.place.householdtypes.*;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

import java.util.function.Supplier;

// Household populations
// These values define the probability of a household being an adult only, adult and child household etc
public class HouseholdDistribution {
    /** One household per populationToHouseholdsRatio people */
    public Double populationToHouseholdsRatio = null;

    // Distribution of household types
    /** 1 non-pensioner adult */
    public Probability pSingleAdult = null;
    /** 2 non-pensioner adults and no children */
    public Probability pSmallAdult = null;
    /** 1 adult of any age and any number of children */
    public Probability pSingleParent = null;
    /** 2 adults of any age and 1 or 2 children */
    public Probability pSmallFamily = null;
    /** 2 adults of any age and 3 or more children */
    public Probability pLargeTwoAdultFamily = null;
    /** 3 or more (any age) adults and 1 or more children */
    public Probability pLargeManyAdultFamily = null;
    /** 3 or more adults (any age) and no children */
    public Probability pLargeAdult = null;
    /** 1 adult and 1 pensioner */
    public Probability pAdultPensioner = null;
    /** 2 pensioners */
    public Probability pDoubleOlder = null;
    /** 1 pensioner */
    public Probability pSingleOlder = null;

    public ProbabilityDistribution<Supplier<Household>> householdTypeDistribution() {
        ProbabilityDistribution<Supplier<Household>> p = new ProbabilityDistribution<>();
        p.add(pSingleAdult, SingleAdult::new);
        p.add(pSmallAdult, SmallAdult::new);
        p.add(pSingleParent, SingleParent::new);
        p.add(pSmallFamily, SmallFamily::new);
        p.add(pLargeTwoAdultFamily, LargeTwoAdultFamily::new);
        p.add(pLargeManyAdultFamily, LargeManyAdultFamily::new);
        p.add(pLargeAdult, LargeAdult::new);
        p.add(pAdultPensioner, AdultPensioner::new);
        p.add(pDoubleOlder, DoubleOlder::new);
        p.add(pSingleOlder, SingleOlder::new);
        return p;
    }

    public boolean isValid() {
        return householdTypeDistribution().isValid() && populationToHouseholdsRatio >= 1;
    }

}
